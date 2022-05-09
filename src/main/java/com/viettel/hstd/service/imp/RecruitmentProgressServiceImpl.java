package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poiji.bind.Poiji;
import com.poiji.option.PoijiOptions;
import com.viettel.hstd.constant.ImportConstant;
import com.viettel.hstd.constant.RecruitmentProgressExcelValidator;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.dto.hstd.ImportHistoryDTO;
import com.viettel.hstd.dto.hstd.PositionDescriptionDTO;
import com.viettel.hstd.dto.hstd.RecruitmentProgressDTO;
import com.viettel.hstd.dto.hstd.RecruitmentProgressEmployeeDTO;
import com.viettel.hstd.entity.hstd.*;
import com.viettel.hstd.entity.vps.EmployeeVhrEntityFull;
import com.viettel.hstd.entity.vps.PositionEntity;
import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.repository.hstd.*;
import com.viettel.hstd.repository.vps.EmployeeVhrRepositoryFull;
import com.viettel.hstd.repository.vps.PositionRepository;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.RecruitmentProgressEmployeeService;
import com.viettel.hstd.service.inf.EmailAlertConfigService;
import com.viettel.hstd.service.inf.EmailConfigService;
import com.viettel.hstd.service.inf.FileService;
import com.viettel.hstd.service.inf.RecruitmentProgressService;
import com.viettel.hstd.service.mapper.ImportHistoryConverter;
import com.viettel.hstd.service.mapper.RecruitmentProgressConverter;
import com.viettel.hstd.service.mapper.RecruitmentProgressEmployeeConverter;
import com.viettel.hstd.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class RecruitmentProgressServiceImpl implements RecruitmentProgressService {

    @Value("${email.recruitment-plan-alert-id}")
    private Long emailTemplateId; //Thư cảnh báo tuyển dụng (Nhân sự phụ trách)

    @Value("${email.tcld-email-id}")
    private Long senderVtId;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final RecruitmentProgressRepository recruitmentProgressRepository;
    private final RecruitmentProgressEmployeeRepository recruitmentProgressEmployeeRepository;
    private final RecruitmentProgressDetailRepository recruitmentProgressDetailRepository;
    private final RecruitmentProgressConverter recruitmentProgressConverter;
    private final RecruitmentProgressEmployeeConverter recruitmentProgressEmployeeConverter;
    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailAlertConfigService emailAlertConfigService;
    private final Message message;
    private final EmailConfigService emailConfigService;
    private final RecruitmentProgressEmployeeService recruitmentProgressEmployeeService;
    private final FileService fileService;
    private final PositionRepository positionRepository;
    private final VhrFutureOrganizationRepository organizationRepository;
    private final EmployeeVhrRepositoryFull employeeVhrRepository;
    private final RecruitmentProgressExcelValidator recruitmentProgressExcelValidator;
    private final ExcelUtil excelUtil;
    private final AuthenticationFacade authenticationFacade;
    private final ImportHistoryRepository importHistoryRepository;
    private final ImportHistoryDetailRepository importHistoryDetailRepository;
    private final ImportHistoryConverter importHistoryConverter;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper;
    private final RecruitmentProgressService self;

    private static final int SHEET_NUMBER = 0;
    private static final int HEADER_START = 1;
    private static final String LIST_DELIMITER = ",";

    @Override
    @Transactional(readOnly = true)
    public RecruitmentProgressEntity findById(Long id) {
        return recruitmentProgressRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(message.getMessage("message.bad_request")));
    }

    @Override
    @Transactional(readOnly = true)
    public RecruitmentProgressDTO.SingleResponse getOne(Long id) {
        RecruitmentProgressDTO.SingleResponse response = recruitmentProgressConverter.entityToSingleResponse(findById(id));
        List<RecruitmentProgressEmployeeDTO.Response> employeesResponse = recruitmentProgressEmployeeRepository.findByRecruitmentProgressId(id);

        Map<Boolean, List<RecruitmentProgressEmployeeDTO.Response>> employeeResponseSplitByIsHr = employeesResponse
                .stream()
                .collect(Collectors.groupingBy(RecruitmentProgressEmployeeDTO.Response::getIsHr));
        response.setListEmployee(employeeResponseSplitByIsHr.getOrDefault(true, new ArrayList<>()));
        response.setListEmailRecipients(employeeResponseSplitByIsHr.getOrDefault(false, new ArrayList<>()));
        return response;

    }

    @Override
    @Transactional
    public void save(RecruitmentProgressEntity entity) {
        recruitmentProgressRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecruitmentProgressDTO.Response> search(RecruitmentProgressDTO.SearchCriteria criteria) {
        List<SearchDTO.OrderDTO> orders = criteria.getSortList();
        List<Sort.Order> queryOrderList = new ArrayList<>();
        //TODO cannot dynamic sort with native query, find other solution
//        for (SearchDTO.OrderDTO order : orders) {
//            Sort.Order queryOrder = new Sort.Order(
//                    order.direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, order.property);
//            queryOrderList.add(queryOrder);
//        }
        Page<RecruitmentProgressDTO.Projection> page = recruitmentProgressRepository.search(criteria.getPositionId(), criteria.getOrganizationId(),
                criteria.getCompletionRate(), criteria.getEmployeeId(), criteria.getEmployeeEmailRecipientId(), criteria.getFromDeadline(), criteria.getToDeadline(),
                PageRequest.of(criteria.getPage(), criteria.getSize(), Sort.by(queryOrderList)));
        return page.map(recruitmentProgressConverter::projectionToResponse);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        boolean isExistedDetail = recruitmentProgressDetailRepository.existsByRecruitmentProgressId(id);
        if (isExistedDetail) throw new BadRequestException(message.getMessage("recruitment.progress.cannot.delete"));
        recruitmentProgressRepository.deleteById(id);
        recruitmentProgressEmployeeRepository.deleteAllByRecruitmentProgressId(id);
    }

    @Override
    @Transactional
    public void create(RecruitmentProgressDTO.RecruitmentProgressRequest request) {
        boolean isExisted = recruitmentProgressRepository.existsByPositionCodeAndOrganizationCodeAndDeadline(request.getPositionCode(), request.getOrganizationCode(), request.getDeadline());
        if (isExisted) throw new BadRequestException(message.getMessage("recruitment.progress.existed"));
        RecruitmentProgressEntity entity = recruitmentProgressConverter.requestToEntity(request);
        recruitmentProgressRepository.save(entity);

        List<RecruitmentProgressEmployeeEntity> listEmployees = request.getListEmployee()
                .stream().map(requestEmployee -> recruitmentProgressEmployeeConverter.requestToEntity(requestEmployee, entity.getId()))
                .collect(Collectors.toList());
        recruitmentProgressEmployeeRepository.saveAll(listEmployees);

    }

    @Override
    @Transactional
    public void update(RecruitmentProgressDTO.RecruitmentProgressRequest request, Long id) {
        RecruitmentProgressEntity entity = recruitmentProgressRepository.findById(id).orElseThrow(() -> new BadRequestException(message.getMessage("message.not_found")));
        recruitmentProgressConverter.copy(entity, request);
        recruitmentProgressRepository.save(entity);

        recruitmentProgressEmployeeRepository.deleteAllByRecruitmentProgressId(id);

        List<RecruitmentProgressEmployeeEntity> listEmployees = request.getListEmployee()
                .stream().map(requestEmployee -> recruitmentProgressEmployeeConverter.requestToEntity(requestEmployee, entity.getId()))
                .collect(Collectors.toList());
        recruitmentProgressEmployeeRepository.saveAll(listEmployees);

    }

    @Override
    @Async("taskScheduler")
    @Scheduled(cron = "0 0 7 * * MON,TUE,WED,THU,FRI")
    public void sendEmailAlert() {
        EmailTemplateEntity emailTemplateEntity = emailTemplateRepository.findById(emailTemplateId)
                .orElseThrow(() -> new BadRequestException(message.getMessage("message.not_found")));
        String template = emailTemplateEntity.getContent();

        Map<Pair<String, String>, List<RecruitmentProgressDTO.EmailResponse>> map = recruitmentProgressEmployeeService.getEmailAlertPerEmployee();
        sendMail(template, map);
    }

    private void sendMail(String template, Map<Pair<String, String>, List<RecruitmentProgressDTO.EmailResponse>> map) {
        if (!map.isEmpty()) {
            JavaMailSender javaMailSender = emailConfigService.configJavaMail(senderVtId);

            String[] dateSubject = new String[]{FORMATTER.format(LocalDate.now())};
            map.keySet().stream().filter(info -> info.getRight() != null).forEach(info -> sendEmailPerson(template, map, javaMailSender, dateSubject, info)

            );
        }
    }

    private void sendEmailPerson(String template, Map<Pair<String, String>, List<RecruitmentProgressDTO.EmailResponse>> map, JavaMailSender javaMailSender, String[] dateSubject, Pair<String, String> info) {
        try {
            MimeMessage emailMessage = javaMailSender.createMimeMessage();
            String content = emailAlertConfigService.createEmailContent(template, map.get(info), info.getLeft(), dateSubject[0]);
            emailMessage.setHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");
            InternetAddress[] toList = InternetAddress.parse(info.getRight());
            emailMessage.setRecipients(javax.mail.Message.RecipientType.TO, toList);
            String recipientsCC = getRecipientsCC(map.get(info));
            if (!recipientsCC.trim().equals(StringUtils.EMPTY)) {
                emailMessage.addRecipients(javax.mail.Message.RecipientType.CC, InternetAddress.parse(recipientsCC));
            }
            emailMessage.setFrom("tcld_ctct@viettel.com.vn");
            emailMessage.setSubject(this.message.getMessage("email.alert.subject", dateSubject));
            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(content, "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);
            emailMessage.setContent(multipart);
            javaMailSender.send(emailMessage);

        } catch (MessagingException e) {
            log.error("ERROR", e);
        }
    }

    private String getRecipientsCC(List<RecruitmentProgressDTO.EmailResponse> emailResponseList) {
        Set<String> addressList = new HashSet<>();
        for (RecruitmentProgressDTO.EmailResponse e : emailResponseList) {
            String recipientsString = e.getListRecipients();
            if (Objects.nonNull(recipientsString)) {
                String[] recipients = recipientsString.split(",");
                Collections.addAll(addressList, recipients);
            }
        }
        return String.join(",", addressList);
    }

    @Override
    @Transactional
    public Long importFromFile(String fileUrl, String fileTitle) throws IOException {

        List<RecruitmentProgressDTO.RecruitmentProgressExcelRow> listExcelRow =
                excelUtil.getListExcelRow(RecruitmentProgressDTO.RecruitmentProgressExcelRow.class, fileUrl,
                        excelUtil.getPoijiExcelType(fileUrl), SHEET_NUMBER, HEADER_START, LIST_DELIMITER);

        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        ImportHistoryDTO.Request importRequest = ImportHistoryDTO.Request.builder()
                .importCode(ImportConstant.ImportCode.RECRUITMENT_PLAN)
                .importStatus(ImportConstant.ImportStatus.PROCESSING)
                .fileUrl(fileUrl)
                .fileTitle(fileTitle)
                .employeeId(sSoResponse.getEmployeeId())
                .employeeCode(sSoResponse.getEmployeeCode())
                .employeeName(sSoResponse.getFullName())
                .build();
        ImportHistoryEntity importHistoryEntity = importHistoryConverter.requestToEntity(importRequest);
        importHistoryRepository.save(importHistoryEntity);

        applicationEventPublisher.publishEvent(new RecruitmentProgressDTO.Event(listExcelRow, importHistoryEntity));

        return importHistoryEntity.getId();


    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("importThreadPool")
    public void validateAndImportRows(RecruitmentProgressDTO.Event event) throws JsonProcessingException {
        List<RecruitmentProgressDTO.RecruitmentProgressExcelRow> errors = new ArrayList<>();
        List<RecruitmentProgressDTO.RecruitmentProgressRequest> requests = new ArrayList<>();

        List<RecruitmentProgressDTO.RecruitmentProgressExcelRow> listExcelRow = event.getLists();

        for (RecruitmentProgressDTO.RecruitmentProgressExcelRow excelRow : listExcelRow) {
            Object parseObject = parseRowToRequest(excelRow);
            if (parseObject instanceof RecruitmentProgressDTO.RecruitmentProgressExcelRow)
                errors.add((RecruitmentProgressDTO.RecruitmentProgressExcelRow) parseObject);
            else if (parseObject instanceof RecruitmentProgressDTO.RecruitmentProgressRequest)
                requests.add((RecruitmentProgressDTO.RecruitmentProgressRequest) parseObject);
        }

        if (!errors.isEmpty()) self.saveImportHistoryFailed(event, errors);
        else self.saveAll(event,requests);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void saveImportHistoryFailed(RecruitmentProgressDTO.Event event,
                                        List<RecruitmentProgressDTO.RecruitmentProgressExcelRow> rowsFormatErrors)
            throws JsonProcessingException {
        ImportHistoryEntity importHistoryEntity = event.getImportHistoryEntity();
        importHistoryEntity.setImportStatus(ImportConstant.ImportStatus.FAILED);
        importHistoryRepository.save(importHistoryEntity);


        List<ImportHistoryDetailEntity> detailEntities = new ArrayList<>();
        for (RecruitmentProgressDTO.RecruitmentProgressExcelRow row : rowsFormatErrors) {
            ImportHistoryDetailEntity importHistoryDetailEntity = new ImportHistoryDetailEntity();
            importHistoryDetailEntity.setImportHistoryId(importHistoryEntity.getId());
            importHistoryDetailEntity.setRowContent(objectMapper.writeValueAsString(row));
            detailEntities.add(importHistoryDetailEntity);
        }
        if (!detailEntities.isEmpty()) importHistoryDetailRepository.saveAll(detailEntities);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void saveAll(RecruitmentProgressDTO.Event event, List<RecruitmentProgressDTO.RecruitmentProgressRequest> requests) {

        List<RecruitmentProgressEmployeeEntity> employeeRequests = new ArrayList<>();
        List<RecruitmentProgressEntity> entities = recruitmentProgressConverter.requestsToEntities(requests);
        recruitmentProgressRepository.saveAll(entities);

        int size = entities.size();
        for (int element = 0; element < size; element++) {
            Long progressId = entities.get(element).getId();
            List<RecruitmentProgressEmployeeDTO.Request> empRequest = requests.get(element).getListEmployee();
            List<RecruitmentProgressEmployeeEntity> employeeEntities = empRequest.stream()
                    .map(requestEmployee -> recruitmentProgressEmployeeConverter.requestToEntity(requestEmployee, progressId))
                    .collect(Collectors.toList());
            employeeRequests.addAll(employeeEntities);
        }
        recruitmentProgressEmployeeRepository.saveAll(employeeRequests);

        //save import history
        ImportHistoryEntity importHistoryEntity = event.getImportHistoryEntity();
        importHistoryEntity.setImportStatus(ImportConstant.ImportStatus.SUCCESS);
        importHistoryRepository.save(importHistoryEntity);
    }

    private Object parseRowToRequest(RecruitmentProgressDTO.RecruitmentProgressExcelRow excelRow) {
        try {
            RecruitmentProgressDTO.RecruitmentProgressRequest request = new RecruitmentProgressDTO.RecruitmentProgressRequest();

            String positionCode = excelRow.getPositionCode();
            if (positionCode == null || positionCode.length() == 0)
                throw new BadRequestException(message.getMessage("recruitment.progress.field.not.found", new String[]{RecruitmentProgressExcelValidator.POSITION_CODE}));
            PositionEntity positionEntity = positionRepository.findFirstByPositionCode(positionCode)
                    .orElseThrow(() -> new BadRequestException(message.getMessage("position.not.found")));
            recruitmentProgressConverter.copyPositionToRequest(request, positionEntity);

            String organizationCode = excelRow.getOrganizationCode();
            if (organizationCode == null || organizationCode.length() == 0)
                throw new BadRequestException(message.getMessage("recruitment.progress.field.not.found", new String[]{RecruitmentProgressExcelValidator.ORGANIZATION_CODE}));
            VhrFutureOrganizationEntity organizationEntity = organizationRepository.findFirstByCode(excelRow.getOrganizationCode())
                    .orElseThrow(() -> new BadRequestException(message.getMessage("organization.not.found")));
            recruitmentProgressConverter.copyOrganizationToRequest(request, organizationEntity);

            String hrPlan = excelRow.getHrPlan();
            String errorMsgHrPlan = recruitmentProgressExcelValidator.validatePositiveNumber(hrPlan, RecruitmentProgressExcelValidator.HR_PLAN);
            if (Objects.nonNull(errorMsgHrPlan)) throw new BadRequestException(errorMsgHrPlan);
            request.setHrPlan(Integer.valueOf(excelRow.getHrPlan()));

            request.setCurrentEmp(employeeVhrRepository.countByPosIdAndPath(request.getPositionId(), request.getOrganizationId()));

            String deadline = excelRow.getDeadline();
            String errorDeadline = recruitmentProgressExcelValidator.validateDeadline(deadline);
            if (Objects.nonNull(errorDeadline)) throw new BadRequestException(errorDeadline);
            request.setDeadline(LocalDate.parse(deadline, FORMATTER));

            request.setDescription(excelRow.getDescription());

            if (recruitmentProgressRepository.existsByPositionCodeAndOrganizationCodeAndDeadline(request.getPositionCode(),
                    request.getOrganizationCode(), request.getDeadline()))
                throw new BadRequestException(message.getMessage("recruitment.progress.existed"));

            List<RecruitmentProgressEmployeeDTO.Request> employeeRequests = new ArrayList<>();
            Set<String> emailEmployee = new HashSet<>();
            Set<String> emailRecipient = new HashSet<>();

            List<String> listEmployee = excelRow.getListEmployees();
            if (listEmployee == null || listEmployee.isEmpty())
                throw new BadRequestException(message.getMessage("list.employee.invalid"));
            for (String e : listEmployee) {
                if (emailEmployee.contains(e.trim())) break;
                EmployeeVhrEntityFull employeeVhrEntity = employeeVhrRepository.findFirstByEmail(e.trim())
                        .orElseThrow(() -> new BadRequestException(message.getMessage("employee.vhr.email.not.found", new String[]{e.trim()})));
                RecruitmentProgressEmployeeDTO.Request empRequest = recruitmentProgressEmployeeConverter.vhrEntityToRequest(employeeVhrEntity);
                empRequest.setIsHr(true);
                employeeRequests.add(empRequest);
                emailEmployee.add(e.trim());
            }

            List<String> listRecipient = excelRow.getListRecipients();
            if (Objects.nonNull(listRecipient) && !listRecipient.isEmpty()) {
                for (String e : listRecipient) {
                    if (emailRecipient.contains(e.trim())) break;
                    EmployeeVhrEntityFull employeeVhrEntity = employeeVhrRepository.findFirstByEmail(e.trim())
                            .orElseThrow(() -> new BadRequestException(message.getMessage("employee.vhr.email.not.found", new String[]{e.trim()})));
                    RecruitmentProgressEmployeeDTO.Request empRequest = recruitmentProgressEmployeeConverter.vhrEntityToRequest(employeeVhrEntity);
                    empRequest.setIsHr(false);
                    employeeRequests.add(empRequest);
                    emailRecipient.add(e.trim());
                }
            }
            request.setListEmployee(employeeRequests);
            return request;

        } catch (Exception ex) {
            excelRow.setValid(false);
            excelRow.setErrors(ex.getMessage());
            return excelRow;
        }
    }

}
