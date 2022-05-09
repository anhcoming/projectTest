package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.constant.ImportConstant;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.dto.hstd.ImportHistoryDTO;
import com.viettel.hstd.dto.hstd.PositionDescriptionDTO;
import com.viettel.hstd.dto.hstd.PositionDescriptionFileDTO;
import com.viettel.hstd.dto.hstd.PositionDescriptionRecipientDTO;
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
import com.viettel.hstd.service.inf.FileService;
import com.viettel.hstd.service.inf.PositionDescriptionService;
import com.viettel.hstd.service.mapper.ImportHistoryConverter;
import com.viettel.hstd.service.mapper.PositionDescriptionConverter;
import com.viettel.hstd.service.mapper.PositionDescriptionFileConverter;
import com.viettel.hstd.service.mapper.PositionDescriptionRecipientConverter;
import com.viettel.hstd.util.DateUtils;
import com.viettel.hstd.util.ExcelUtil;
import com.viettel.hstd.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
@Slf4j
public class PositionDescriptionServiceImpl implements PositionDescriptionService {
    private final PositionDescriptionRepository positionDescriptionRepository;
    private final PositionDescriptionFileRepository positionDescriptionFileRepository;
    private final PositionDescriptionRecipientRepository positionDescriptionRecipientRepository;
    private final VhrFutureOrganizationRepository vhrFutureOrganizationRepository;
    private final EmployeeVhrRepositoryFull employeeVhrRepositoryFull;
    private final ImportHistoryRepository importHistoryRepository;
    private final ImportHistoryDetailRepository importHistoryDetailRepository;
    private final PositionRepository positionRepository;
    private final FileService fileService;
    private final Message message;
    private final Validator customValidator;
    private final ExcelUtil excelUtil;
    private final PositionDescriptionConverter positionDescriptionConverter;
    private final PositionDescriptionRecipientConverter recipientConverter;
    private final PositionDescriptionFileConverter positionDescriptionFileConverter;
    private final ImportHistoryConverter importHistoryConverter;
    private final AuthenticationFacade authenticationFacade;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper;
    @Lazy
    private final PositionDescriptionService self;


    @Value("${app.store.path-store-media}")
    private String pathStore;

    private static final Set<String> ATTACHMENT_RESTRICTION = new HashSet<>(Arrays.asList(".doc", ".docx", ".xls", ".xlsx", ".pdf"));
    private static final int SHEET_NUMBER = 0;
    private static final int HEADER_START = 2;
    private static final String LIST_DELIMITER = ",";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long importExcel(String importFileTitle, String importFileUrl, String attachmentFileUrl) throws IOException {

        List<PositionDescriptionDTO.PositionDescriptionExcelRow> excelRows =
                excelUtil.getListExcelRow(PositionDescriptionDTO.PositionDescriptionExcelRow.class, importFileUrl,
                        excelUtil.getPoijiExcelType(importFileUrl), SHEET_NUMBER, HEADER_START, LIST_DELIMITER);
        Map<String, String> attachments = extractZipFile(attachmentFileUrl);

        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        ImportHistoryDTO.Request importRequest = ImportHistoryDTO.Request.builder()
                .importCode(ImportConstant.ImportCode.POSITION_DESCRIPTION)
                .importStatus(ImportConstant.ImportStatus.PROCESSING)
                .fileUrl(importFileUrl)
                .fileTitle(importFileTitle)
                .employeeId(sSoResponse.getEmployeeId())
                .employeeCode(sSoResponse.getEmployeeCode())
                .employeeName(sSoResponse.getFullName())
                .build();
        ImportHistoryEntity importHistoryEntity = importHistoryConverter.requestToEntity(importRequest);
        importHistoryRepository.save(importHistoryEntity);

        applicationEventPublisher.publishEvent(new PositionDescriptionDTO.Event(excelRows, attachments, importHistoryEntity));

        return importHistoryEntity.getId();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("importThreadPool")
    public void validateAndImportRows(PositionDescriptionDTO.Event event) throws JsonProcessingException {
        List<PositionDescriptionDTO.PositionDescriptionExcelRow> rowsFormatErrors = validateRowFormat(event.getExcelRows());
        if (!rowsFormatErrors.isEmpty()) {
            //save to db import history failed
            self.saveImportHistoryFailed(event, rowsFormatErrors);
            return;
        }

        List<?> validatedLists = validateDataRow(event.getExcelRows(), event.getAttachments());
        if (!validatedLists.isEmpty()) {
            if (validatedLists.get(0) instanceof PositionDescriptionDTO.PositionDescriptionExcelRow) {
                //save to db import history failed
                self.saveImportHistoryFailed(event, (List<PositionDescriptionDTO.PositionDescriptionExcelRow>) validatedLists);
            } else {
                self.saveBatch(event, (List<PositionDescriptionDTO.Request>) validatedLists);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void saveImportHistoryFailed(PositionDescriptionDTO.Event event, List<PositionDescriptionDTO.PositionDescriptionExcelRow> rowsFormatErrors) throws JsonProcessingException {
        ImportHistoryEntity importHistoryEntity = event.getImportHistoryEntity();
        importHistoryEntity.setImportStatus(ImportConstant.ImportStatus.FAILED);
        importHistoryRepository.save(importHistoryEntity);


        List<ImportHistoryDetailEntity> detailEntities = new ArrayList<>();
        for (PositionDescriptionDTO.PositionDescriptionExcelRow row : rowsFormatErrors) {
            ImportHistoryDetailEntity importHistoryDetailEntity = new ImportHistoryDetailEntity();
            importHistoryDetailEntity.setImportHistoryId(importHistoryEntity.getId());
            importHistoryDetailEntity.setRowContent(objectMapper.writeValueAsString(row));
            detailEntities.add(importHistoryDetailEntity);
        }
        if (!detailEntities.isEmpty()) importHistoryDetailRepository.saveAll(detailEntities);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveBatch(PositionDescriptionDTO.Event event, List<PositionDescriptionDTO.Request> requests) {

        //save position description
        List<PositionDescriptionEntity> positionDescriptionEntities = positionDescriptionConverter.requestsToEntities(requests);
        positionDescriptionRepository.saveAll(positionDescriptionEntities);

        //save position description recipient and file
        List<PositionDescriptionRecipientEntity> recipientRequestEntities = new ArrayList<>();
        List<PositionDescriptionFileEntity> fileRequestEntities = new ArrayList<>();
        for (int element = 0; element < positionDescriptionEntities.size(); element++) {
            List<PositionDescriptionRecipientDTO.Request> requestList = requests.get(element).getRecipientRequests();
            if (requestList != null && !requestList.isEmpty()) {
                for (PositionDescriptionRecipientDTO.Request request : requestList) {
                    recipientRequestEntities.add(recipientConverter.requestToEntity(request, positionDescriptionEntities.get(element).getId()));
                }

            }
            List<PositionDescriptionFileDTO.Request> fileRequestList = requests.get(element).getFileRequests();
            if (fileRequestList != null && !fileRequestList.isEmpty()) {
                for (PositionDescriptionFileDTO.Request request : fileRequestList) {
                    fileRequestEntities.add(positionDescriptionFileConverter.requestToEntity(request, positionDescriptionEntities.get(element).getId()));
                }

            }
        }
        positionDescriptionRecipientRepository.saveAll(recipientRequestEntities);
        positionDescriptionFileRepository.saveAll(fileRequestEntities);

        //save import history
        ImportHistoryEntity importHistoryEntity = event.getImportHistoryEntity();
        importHistoryEntity.setImportStatus(ImportConstant.ImportStatus.SUCCESS);
        importHistoryRepository.save(importHistoryEntity);
    }

    private List<PositionDescriptionDTO.PositionDescriptionExcelRow> validateRowFormat(List<PositionDescriptionDTO.PositionDescriptionExcelRow> rows) {
        List<PositionDescriptionDTO.PositionDescriptionExcelRow> errors = new ArrayList<>();
        rows.stream().parallel().forEach(row -> {
            Set<ConstraintViolation<PositionDescriptionDTO.PositionDescriptionExcelRow>> rowConstraintViolations = customValidator.validate(row);
            if (!rowConstraintViolations.isEmpty()) {
                String rowErrors = rowConstraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", "));
                row.setErrors(rowErrors);
                errors.add(row);
            }
        });
        return errors;
    }

    private List<?> validateDataRow(List<PositionDescriptionDTO.PositionDescriptionExcelRow> rows, Map<String, String> attachments) {
        List<PositionDescriptionDTO.Request> requests = new ArrayList<>();
        List<PositionDescriptionDTO.PositionDescriptionExcelRow> errors = new ArrayList<>();

        for (PositionDescriptionDTO.PositionDescriptionExcelRow row : rows) {
            Object parseObject = parseRow(row, attachments);
            if (parseObject instanceof PositionDescriptionDTO.PositionDescriptionExcelRow) {
                errors.add((PositionDescriptionDTO.PositionDescriptionExcelRow) parseObject);
            } else if (parseObject instanceof PositionDescriptionDTO.Request) {
                requests.add((PositionDescriptionDTO.Request) parseObject);
            }
        }
        if (errors.isEmpty()) return requests;
        return errors;


    }

    @Transactional(readOnly = true)
    public Object parseRow(PositionDescriptionDTO.PositionDescriptionExcelRow row, Map<String, String> attachments) {
        PositionDescriptionDTO.Request request = positionDescriptionConverter.excelRowToRequest(row);

        //check existed position description
        boolean existedRow = positionDescriptionRepository.existsRequest(row.getUnitCode(), row.getDepartmentCode(), row.getGroupCode(), row.getPositionCode());
        if (existedRow) {
            row.setErrors(message.getMessage("position.description.existed"));
            return row;
        }
        //check file
        List<String> fileAttachments = row.getFileAttachments();
        if (!attachments.keySet().containsAll(fileAttachments)) {
            row.setErrors(message.getMessage("position.description.attachment.not.found"));
            return row;
        } else {
            List<PositionDescriptionFileDTO.Request> fileRequests = new ArrayList<>();
            for (String fileAttachment : fileAttachments) {
                PositionDescriptionFileDTO.Request fileRequest = new PositionDescriptionFileDTO.Request();
                fileRequest.setFileTitle(fileAttachment);
                fileRequest.setFileUrl(attachments.get(fileAttachment));
                fileRequests.add(fileRequest);
            }
            request.setFileRequests(fileRequests);
        }

        //check unit code
        String unitCode = row.getUnitCode();
        VhrFutureOrganizationEntity unitEntity = vhrFutureOrganizationRepository.findFirstByCode(unitCode).orElse(null);
        if (Objects.nonNull(unitEntity)) {
            request.setUnitId(unitEntity.getOrganizationId());
            request.setUnitName(unitEntity.getName());
        } else {
            row.setErrors(message.getMessage("organization.not.found"));
            return row;
        }

        //check department code
        VhrFutureOrganizationEntity departmentEntity = null;
        if (Objects.nonNull(row.getDepartmentCode()) && Objects.nonNull(row.getDepartmentName())) {
            String path = unitEntity.getPath();
            departmentEntity = vhrFutureOrganizationRepository.findFirstByCodeAndPathLike(row.getDepartmentCode(), path.concat("%")).orElse(null);
            if (Objects.nonNull(departmentEntity)) {
                request.setDepartmentId(departmentEntity.getOrganizationId());
                request.setDepartmentName(departmentEntity.getName());
            } else {
                row.setErrors(message.getMessage("department.not.found"));
                return row;
            }
        }

        //check group code
        VhrFutureOrganizationEntity groupEntity;
        if (Objects.nonNull(row.getGroupCode()) && Objects.nonNull(row.getGroupName()) && Objects.nonNull(departmentEntity)) {
            String path = departmentEntity.getPath();
            groupEntity = vhrFutureOrganizationRepository.findFirstByCodeAndPathLike(row.getGroupCode(), path.concat("%")).orElse(null);
            if (Objects.nonNull(groupEntity)) {
                request.setGroupId(groupEntity.getOrganizationId());
                request.setGroupName(groupEntity.getName());
            } else {
                row.setErrors(message.getMessage("group.not.found"));
                return row;
            }
        }

        //check position code
        String positionCode = row.getPositionCode();
        PositionEntity positionEntity = positionRepository.findFirstByPositionCode(positionCode).orElse(null);
        if (Objects.nonNull(positionEntity)) {
            request.setPositionId(positionEntity.getPositionId());
            request.setPositionName(positionEntity.getPositionName());
        } else {
            row.setErrors(message.getMessage("position.not.found"));
            return row;
        }

        //check recipients

        Set<String> recipientsCode = new HashSet<>(row.getEmployeeRecipients());
        if (recipientsCode.isEmpty()) return request;

        Map<String, List<EmployeeVhrEntityFull>> employeeEntities = employeeVhrRepositoryFull.findByEmployeeCodeIn(recipientsCode)
                .stream().collect(Collectors.groupingBy(EmployeeVhrEntityFull::getEmployeeCode));

        List<PositionDescriptionRecipientDTO.Request> recipientRequests = new ArrayList<>();

        for (String recipientCode : recipientsCode) {
            if (!employeeEntities.containsKey(recipientCode)) {
                row.setErrors(message.getMessage("employee.vhr.code.not.found", new String[]{recipientCode}));
                return row;
            } else {
                PositionDescriptionRecipientDTO.Request recipientRequest = recipientConverter.employeeVhrToRequest(
                        employeeEntities.get(recipientCode).get(0));
                recipientRequests.add(recipientRequest);
            }
        }
        request.setRecipientRequests(recipientRequests);
        return request;
    }


    private Map<String, String> extractZipFile(String fileUrl) {
        Map<String, String> result = new HashMap<>();
        if (Objects.isNull(fileUrl)) return result;
        Resource resource = fileService.downloadFileWithEncodePath(fileUrl);

        try (ZipFile zipFile = new ZipFile(resource.getFile())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
            Path uploadFolder = getPathUploadFolder();


            List<FileHeader> fileHeaders = zipFile.getFileHeaders();
            for (FileHeader fileHeader : fileHeaders) {
                if (!fileHeader.isDirectory()) {
                    String originalFileName = fileHeader.getFileName();
                    String fileName = originalFileName.substring(originalFileName.lastIndexOf("/") + 1);
                    String extension = FileUtils.getFileExtension(originalFileName);
                    if (!ATTACHMENT_RESTRICTION.contains(extension)) break;

                    String fileNameWithoutExtension = fileName.replace(extension, "");
                    LocalDateTime now = LocalDateTime.now();
                    String newFileName = formatter.format(now).concat(Objects.requireNonNull(FileUtils.getNameFileMD5(fileNameWithoutExtension, extension)));
                    zipFile.extractFile(fileHeader, uploadFolder.toUri().getPath(), newFileName);

                    String fileEncodeUrl = String.valueOf(now.getYear()).concat("-")
                            .concat(String.format("%02d", now.getMonthValue())).concat("-")
                            .concat(String.valueOf(now.getDayOfMonth())).concat("-")
                            .concat(newFileName);
                    result.putIfAbsent(fileName, fileEncodeUrl);
                }
            }
        } catch (Exception ex) {
            log.error("ERROR", ex);
        }
        return result;


    }


    private String getUploadFolder() {
        String dateString = DateUtils.getNowTime("yyyy-MM-dd");
        String[] dateParts = dateString.split("-");
        String year = dateParts[0];
        String month = dateParts[1];
        String day = dateParts[2];
        return pathStore + "/" + year + "/" + month + "/" + day + "/";
    }

    private Path getPathUploadFolder() throws IOException {
        String uploadFolder = getUploadFolder();
        final Path fileStorageLocation = Paths.get(uploadFolder)
                .toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation))
            Files.createDirectories(fileStorageLocation);
        return fileStorageLocation;

    }

    @Override
    @Transactional(readOnly = true)
    public Page<PositionDescriptionDTO.Response> search(PositionDescriptionDTO.SearchCriteria criteria) {
        List<SearchDTO.OrderDTO> orders = criteria.getSortList();
        List<Sort.Order> queryOrderList = new ArrayList<>();
        for (SearchDTO.OrderDTO order : orders) {
            Sort.Order queryOrder = new Sort.Order(
                    order.direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, order.property);
            queryOrderList.add(queryOrder);
        }
        Page<PositionDescriptionEntity> page = positionDescriptionRepository.search(criteria.getUnitId(), criteria.getDepartmentId(), criteria.getGroupId(), criteria.getPositionId(), criteria.getHasDescription(),
                PageRequest.of(criteria.getPage(), criteria.getSize(), Sort.by(queryOrderList)));
        return page.map(positionDescriptionConverter::entityToResponse);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        positionDescriptionRepository.deleteById(id);
        positionDescriptionFileRepository.deleteAllByPositionDescriptionId(id);
        positionDescriptionRecipientRepository.deleteAllByPositionDescriptionId(id);
    }


    @Override
    @Transactional
    public void save(PositionDescriptionDTO.Request request) {
        boolean existedRequest = positionDescriptionRepository.existsRequest(request.getUnitCode(),
                request.getDepartmentCode(), request.getGroupCode(), request.getPositionCode());
        if (existedRequest) throw new BadRequestException(message.getMessage("position.description.existed"));
        PositionDescriptionEntity positionDescriptionEntity =
                positionDescriptionRepository.save(positionDescriptionConverter.requestToEntity(request));
        Long id = positionDescriptionEntity.getId();

        if (Objects.nonNull(request.getFileRequests())) {
            List<PositionDescriptionFileEntity> fileEntities = request.getFileRequests()
                    .stream().map(fileRequest -> positionDescriptionFileConverter.requestToEntity(fileRequest, id))
                    .collect(Collectors.toList());
            if (!fileEntities.isEmpty()) positionDescriptionFileRepository.saveAll(fileEntities);
        }

        if (Objects.nonNull(request.getRecipientRequests())) {
            List<PositionDescriptionRecipientEntity> recipientEntities = request.getRecipientRequests().stream()
                    .map(recipientRequest -> recipientConverter.requestToEntity(recipientRequest, id))
                    .collect(Collectors.toList());
            if (!recipientEntities.isEmpty()) positionDescriptionRecipientRepository.saveAll(recipientEntities);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public PositionDescriptionDTO.SingleResponse findById(Long id) {

        PositionDescriptionEntity entity = positionDescriptionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(message.getMessage("message.not_found")));

        PositionDescriptionDTO.SingleResponse response = positionDescriptionConverter.entityToSingleResponse(entity);

        if (Boolean.TRUE.equals(response.getHasDescription())) {
            List<PositionDescriptionFileDTO.Response> fileResponse =
                    positionDescriptionFileRepository.getListFileByPositionDescriptionId(entity.getId());
            response.setFiles(fileResponse);
        } else {
            List<PositionDescriptionRecipientDTO.Response> recipientResponse =
                    positionDescriptionRecipientRepository.getRecipientsByPositionDescriptionId(entity.getId());
            response.setRecipients(recipientResponse);
        }

        return response;
    }

    @Override
    public void update(Long id, PositionDescriptionDTO.Request request) {
        PositionDescriptionEntity positionDescriptionEntity =positionDescriptionConverter.requestToEntity(request);
        positionDescriptionEntity.setId(id);
        positionDescriptionRepository.save(positionDescriptionEntity);

        if (Objects.nonNull(request.getFileRequests())) {
            List<PositionDescriptionFileEntity> fileEntities = request.getFileRequests()
                    .stream().map(fileRequest -> positionDescriptionFileConverter.requestToEntity(fileRequest, id))
                    .collect(Collectors.toList());
            if (!fileEntities.isEmpty()) positionDescriptionFileRepository.saveAll(fileEntities);
        }

        if (Objects.nonNull(request.getRecipientRequests())) {
            List<PositionDescriptionRecipientEntity> recipientEntities = request.getRecipientRequests().stream()
                    .map(recipientRequest -> recipientConverter.requestToEntity(recipientRequest, id))
                    .collect(Collectors.toList());
            if (!recipientEntities.isEmpty()) positionDescriptionRecipientRepository.saveAll(recipientEntities);
        }
    }
}
