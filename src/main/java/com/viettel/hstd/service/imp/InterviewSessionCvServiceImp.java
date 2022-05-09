package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.constant.*;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.constant.*;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.*;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.*;
import com.viettel.hstd.dto.hstd.InterviewSessionCvDTO.*;
import com.viettel.hstd.entity.hstd.*;
import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.filter.HSTDFilter;
import com.viettel.hstd.repository.hstd.*;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.*;
import com.viettel.hstd.util.FolderExtension;
import com.viettel.hstd.util.HTMLtoExcel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional
public class InterviewSessionCvServiceImp extends BaseService implements InterviewSessionCvService {
    //<editor-fold desc="Khoi tao">
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    Message message;

    @Autowired
    InterviewSessionCvRepository interviewSessionCvRepository;
    @Autowired
    InterviewSessionRepository interviewSessionRepository;
    @Autowired
    CvRepository cvRepository;

    @Autowired
    EmailService emailService;
    @Autowired
    EmailTemplateService emailTemplateService;

    @Autowired
    private EmailCategoryRepository emailCategoryRepository;

    @Autowired
    MapUtils mapUtils;
    @Autowired
    private FileAttachmentRepository fileAttachmentRepository;
    @Autowired
    private RecruiteeAccountRepository recruiteeAccountRepository;
    @Autowired
    private DocumentTypeRepository documentTypeRepository;
    @Autowired
    private RecruitProfileAttachmentRepository recruitProfileAttachmentRepository;
    @Autowired
    private FolderExtension folderExtension;
    @Autowired
    private VhrFutureOrganizationRepository organizationRepository;
    @Autowired
    private EmployeeVhrTempRepository employeeVhrTempRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    HSTDFilter hstdFilter;

    //</editor-fold>

    @Override
    public InterviewSessionCvResponse findOneById(Long id) {
        InterviewSessionCvEntity entity = interviewSessionCvRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        InterviewSessionCvResponse response = objectMapper.convertValue(entity, InterviewSessionCvResponse.class);
        response.cvId = entity.getCvEntity().getCvId();
        response.interviewSessionId = entity.getInterviewSessionEntity().getInterviewSessionId();
        response.lstAttachment = fileAttachmentRepository.findByFileItemIdAndFileType(id, FileTypeConstant.InterviewCv).stream().map(obj -> this.objectMapper.convertValue(obj, FileAttachmentDTO.FileAttachmentResponse.class)).collect(Collectors.toList());
        if (entity.getCvEntity() != null) {
            response.cvId = entity.getCvEntity().getCvId();
        }
        return response;
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        InterviewSessionCvEntity entity = interviewSessionCvRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        if (entity.getIsWork() != null && entity.getIsWork().equals(AcceptJobStatus.ACCEPT)) {
            throw new BadRequestException("Ứng viên đã trúng tuyển không được phép xóa");
        }

        if (interviewSessionCvRepository.isHasContract(id)) {
            throw new BadRequestException("Ứng viên đã trúng tuyển và được cấp tài khoản VHR nên không thể xóa trong danh sách trúng tuyển");
        }
        ArrayList<RecruiteeAccountEntity> recruiteeAccountEntities = recruiteeAccountRepository.findByInterviewCvId(id);
        if (!recruiteeAccountEntities.isEmpty()) {
            List<Long> recruiteeAccounIds = recruiteeAccountEntities.stream().map(RecruiteeAccountEntity::getRecruiteeAccountId).collect(Collectors.toList());
            recruiteeAccountRepository.softDeleteAll(recruiteeAccounIds);
        }
        interviewSessionCvRepository.softDelete(id);
        addLog("INTERVIEW_SESSION_CV", "DELETE", id.toString());
        return true;
    }

    @Override
    public Boolean addOrUpdateListCv(Long interviewId, List<InterviewSessionCvRequest> request) {
        InterviewSessionEntity interviewSessionEntity = interviewSessionRepository.findById(interviewId).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
//        List<InterviewSessionCvEntity> lstCvEntities = (
//                entity.getInterviewSessionCvEntities().stream().filter(x ->
//                        request.stream().anyMatch(y -> y.cvId == x.getInterviewSessionCvId()))
//                        .collect(Collectors.toList()));
//        if (request != null && request.size() > 0) {
//            int size = lstCvEntities.size();
//            for (InterviewSessionCvRequest cv : request) {
//                if (cv.cvId != null && cv.cvId > 0) {
//                    if (!lstCvEntities
//                            .stream().anyMatch(x -> x.getCvEntity() != null &&
//                                    x.getCvEntity().getCvId() == cv.cvId)
//                            || size == 0) {
//                        InterviewSessionCvEntity interviewCvEntity = new InterviewSessionCvEntity();
//                        CvEntity cvEntity = new CvEntity();
//                        cvEntity.setCvId(cv.cvId);
//                        interviewCvEntity.setDelFlag(false);
//                        interviewCvEntity.setIsActive(true);
//                        interviewCvEntity.setCvEntity(cvEntity);
//                        interviewCvEntity.setInterviewSessionEntity(entity);
//                        interviewSessionCvRepository.save(interviewCvEntity);
////                        lstCvEntities.add(interviewCvEntity);
//                    }
//                }
//            }
//        } else {
//            if(lstCvEntities != null && lstCvEntities.size() > 0){
//                List<Long> seveIdList = lstCvEntities
//                        .stream().map(InterviewSessionCvEntity::getInterviewSessionCvId)
//                        .collect(Collectors.toList());
//                interviewSessionCvRepository.softDeleteAll(seveIdList);
//            }
////            entity.getInterviewSessionCvEntities().clear();
//        }
//        entity.getInterviewSessionCvEntities().clear();
//        entity.getInterviewSessionCvEntities().addAll(lstCvEntities);
//        entity = interviewSessionRepository.save(entity);

        List<Long> cvIds = request.stream().map(obj -> obj.cvId).collect(Collectors.toList());

        List<InterviewSessionCvEntity> commonInterviewSessionCvEntities = new ArrayList<>();
        List<InterviewSessionCvEntity> need2BDeletedInterviewSessionCvEntities = new ArrayList<>();
        List<InterviewSessionCvEntity> need2BAddedInterviewSessionCvEntities = new ArrayList<>();


//        commonInterviewSessionCvEntities = (
//                interviewSessionEntity.getInterviewSessionCvEntities().stream().filter(x ->
//                        request.stream().anyMatch(y -> y.cvId == x.getInterviewSessionCvId()))
//                        .collect(Collectors.toList()));
        interviewSessionEntity.getInterviewSessionCvEntities().forEach(bridgeEntity -> {
            if (cvIds.contains(bridgeEntity.getCvEntity().getCvId())) {
                commonInterviewSessionCvEntities.add(bridgeEntity);
            } else {
                need2BDeletedInterviewSessionCvEntities.add(bridgeEntity);
            }
        });
        List<Long> interviewSessionCvIdDelete = need2BDeletedInterviewSessionCvEntities.stream().map(InterviewSessionCvEntity::getInterviewSessionCvId).collect(Collectors.toList());

        if (interviewSessionCvRepository.isHasContract(interviewSessionCvIdDelete)) {
            throw new BadRequestException("Có ít nhất một ứng viên trong đợt phỏng vấn đã trúng tuyển và được VHR cấp tài khoản, không thể loại bỏ nó khỏi đợt phỏng vấn. Làm ơn kiểm tra lại");
        }

        List<Long> commonCVId = commonInterviewSessionCvEntities.stream().map(obj -> obj.getCvEntity().getCvId()).collect(Collectors.toList());

        InterviewSessionEntity finalInterviewSessionEntity = interviewSessionEntity;
        need2BAddedInterviewSessionCvEntities = cvIds.stream().filter(obj -> !commonCVId.contains(obj)).map(cvId -> {
            InterviewSessionCvEntity entity = new InterviewSessionCvEntity();

            CvEntity cvEntity = cvRepository.findById(cvId).orElseThrow(() -> new NotFoundException("Không tìm thấy CV"));
            entity.setCvEntity(cvEntity);
            entity.setInterviewSessionEntity(finalInterviewSessionEntity);
            return entity;
        }).collect(Collectors.toList());


        interviewSessionEntity.getInterviewSessionCvEntities().clear();
        interviewSessionEntity.getInterviewSessionCvEntities().addAll(commonInterviewSessionCvEntities);
        interviewSessionEntity.getInterviewSessionCvEntities().addAll(need2BAddedInterviewSessionCvEntities);

        interviewSessionCvRepository.softDeleteAll(interviewSessionCvIdDelete);
        interviewSessionCvRepository.saveAll(need2BAddedInterviewSessionCvEntities);

        interviewSessionEntity = interviewSessionRepository.save(interviewSessionEntity);
        return true;
    }

    @Override
    public InterviewSessionCvResponse create(InterviewSessionCvRequest request) {
        InterviewSessionCvEntity entity = objectMapper.convertValue(request, InterviewSessionCvEntity.class);
        entity.setCvEntity(cvRepository.findById(request.cvId).get());
        entity.setInterviewSessionEntity(interviewSessionRepository.findById(request.interviewSessionId).get());

        if (entity.getCvEntity() != null && entity.getInterviewSessionEntity() != null) {
            entity = interviewSessionCvRepository.save(entity);
            if (entity.getIsWork().equals(AcceptJobStatus.ACCEPT)) {
                createRecruiteeAccountAndAdditionalInfo(entity);
            }
            //<editor-fold desc="Luu lai thong tin luong 3 thang gan nhat">
            if (request.lstAttachment != null && request.lstAttachment.size() > 0) {
                for (FileAttachmentDTO.FileAttachmentRequest item : request.lstAttachment) {
                    if (item.fileName != null && item.filePath != null && item.filePath.trim().length() > 0) {
                        FileAttachmentEntity attachment = objectMapper.convertValue(item, FileAttachmentEntity.class);
                        attachment.setFileItemId(entity.getInterviewSessionCvId());
                        attachment.setFileType(FileTypeConstant.InterviewCv);
                        fileAttachmentRepository.save(attachment);
                    }
                }
            }
            //</editor-fold>
        }
        return convertEntity2Response(entity);
    }

    @Override
    @Transactional
    public InterviewSessionCvResponse update(Long id, InterviewSessionCvRequest request) {
        InterviewSessionCvEntity entity = interviewSessionCvRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        InterviewSessionCvEntity newE = objectMapper.convertValue(request, InterviewSessionCvEntity.class);

//        CvEntity oldCv = new CvEntity();
//        oldCv.setCvId(entity.getCvEntity().getCvId());
//        InterviewSessionEntity interview = new InterviewSessionEntity();
//        interview.setInterviewSessionId(entity.getInterviewSessionEntity().getInterviewSessionId());

        mapUtils.customMap(newE, entity);
        entity.setInterviewSessionCvId(id);
//        entity.setCvEntity(cvRepository.findById(request.cvId).get());
//        entity.setInterviewSessionEntity(interviewSessionRepository.findById(request.interviewSessionId).get());
//        entity.setInterviewSessionEntity(interview);
//        entity.setCvEntity(oldCv);

        if (entity.getCvEntity() != null && entity.getInterviewSessionEntity() != null) {
            entity = interviewSessionCvRepository.save(entity);
            if (entity.getIsWork().equals(AcceptJobStatus.ACCEPT)) {
                createRecruiteeAccountAndAdditionalInfo(entity);
            } else {
                if (interviewSessionCvRepository.isHasContract(id)) {
                    throw new BadRequestException("Ứng viên đã trúng tuyển và cấp tài khoản từ VHR không thể sửa trạng thái làm việc");
                }
            }
            //<editor-fold desc="Luu lai thong tin luong 3 thang gan nhat">
            ArrayList<FileAttachmentEntity> lstAttachment = fileAttachmentRepository.findByFileItemIdAndFileType(id, FileTypeConstant.InterviewCv);
            if (request.lstAttachment == null) {
                request.lstAttachment = new ArrayList<>();
            }
            for (FileAttachmentEntity attachment : lstAttachment) {
                if (request.lstAttachment.stream().anyMatch(x -> x.filePath.equalsIgnoreCase(attachment.getFilePath()))) {
                    request.lstAttachment = request.lstAttachment.stream().filter(x -> !x.filePath.equalsIgnoreCase(attachment.getFilePath())).collect(Collectors.toList());
                } else {
                    fileAttachmentRepository.delete(attachment);
                }
            }
            if (request.lstAttachment.size() > 0) {
                for (FileAttachmentDTO.FileAttachmentRequest item : request.lstAttachment) {
                    if (item.fileName != null && item.filePath != null && item.filePath.trim().length() > 0) {
                        FileAttachmentEntity attachment = objectMapper.convertValue(item, FileAttachmentEntity.class);
                        attachment.setFileItemId(entity.getInterviewSessionCvId());
                        attachment.setFileType(FileTypeConstant.InterviewCv);
                        fileAttachmentRepository.save(attachment);
                    }
                }
            }
            //</editor-fold>

            if (entity.getResult() != InterviewResult.NOT_EVALUATE_YET) {
                CvEntity cvEntity = entity.getCvEntity();
                cvEntity.setInterviewState(true);
                cvRepository.save(cvEntity);
            }
        }
        return convertEntity2Response(entity);
    }

    @Override
    public List<InterviewSessionCvResponse> findByCvId(Long cvId) {
        ArrayList<InterviewSessionCvEntity> list = interviewSessionCvRepository.findByCvId(cvId);
        return list.stream().map(obj -> this.objectMapper.convertValue(obj, InterviewSessionCvResponse.class)).collect(Collectors.toList());
    }

    @Override
    public Page<InterviewSessionCvResponse> findPage(SearchDTO searchRequest) {
        hstdFilter.unitDepartmentFilter(searchRequest, "interviewSessionEntity.unitId");
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<InterviewSessionCvEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<InterviewSessionCvEntity> list;
        if (searchRequest.pagedFlag) {
            list = interviewSessionCvRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = interviewSessionCvRepository.findAll(p);
        }

        return list.map(this::convertEntity2Response);
    }

    public Boolean sendOffer(ArrayList<Long> ids, Long templateId) {
        try {
            if (templateId == null) {
                //Fake id
                templateId = 1L;
            }
            EmailTemplateDTO.EmailTemplateResponse template = emailTemplateService.findOneById(templateId);
            if (template != null) {
                ArrayList<CvEntity> list = cvRepository.findByIdIn(ids);
                if (list != null && list.size() > 0) {
                    SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();

                    for (CvEntity cvEntity : list) {
//                        Map<String, Object> map = objectMapper.convertValue(cvEntity, Map.class);
                        Map<String, String> map = CustomMapper.convert(cvEntity);
                        template.content = EmailUtils.replaceContent(template.content, map);
                        Multipart multipart = new MimeMultipart();
                        MimeBodyPart messageBodyPart = new MimeBodyPart();
                        messageBodyPart.setContent(template.content, "text/html; charset=utf-8");
                        multipart.addBodyPart(messageBodyPart);
                        emailService.sendMessage(template.name, multipart, cvEntity.getEmail());

                        //<editor-fold desc="Them bang lich su gui email">
                        EmailCategoryEntity entity = new EmailCategoryEntity();
                        entity.setEmailSend(cvEntity.getEmail());
                        entity.setIsStatus(true);
                        entity.setEmailTemplateId(templateId);
                        entity.setName(template.name);
                        entity.setCreatedName(sSoResponse.getFullName());
                        emailCategoryRepository.save(entity);
                        //</editor-fold>

                    }
                    return true;
                }
            }
        } catch (Exception ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
        }
        return false;
    }


    public List<InterviewSessionCvResponse> findByInterviewSessionId(Long id) {
        ArrayList<InterviewSessionCvEntity> list = interviewSessionCvRepository.findByInterviewSessionId(id);
        return list.stream().map(obj -> this.objectMapper.convertValue(obj, InterviewSessionCvResponse.class)).collect(Collectors.toList());
    }

    public String getEmailContent(Long id, Long templateId) {
        EmailTemplateDTO.EmailTemplateResponse template = emailTemplateService.findOneById(templateId);
        if (template != null) {
//            CvEntity cvEntity = cvRepository.findById(id)
//                    .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
//            Map<String, String> map = CustomMapper.convert(cvEntity);
//            template.content = EmailUtils.replaceContent(template.content, map, false);
            InterviewSessionCvEntity entity = interviewSessionCvRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
//            entity.setRecruiteeAccountEntities(null);
            Map<String, String> map = CustomMapper.convert(entity);
            map.remove("recruiteeAccountEntities");
            map.remove("interviewSessionEntity");
            map.remove("cvEntity");
            if (entity.getCvEntity() != null) {
//                entity.getCvEntity().setInterviewSessionCvEntities(null);
                CvEntity cvEntity = entity.getCvEntity();
                map.putAll(CustomMapper.convert(cvEntity));
            }
            if (entity.getInterviewSessionEntity() != null) {
//                entity.getInterviewSessionEntity().setInterviewSessionCvEntities(null);
//                entity.getInterviewSessionEntity().setEmployeeInterviewSessionEntities(null);
                InterviewSessionEntity interviewSessionEntity = entity.getInterviewSessionEntity();
                map.putAll(CustomMapper.convert(interviewSessionEntity));
            }
            template.content = EmailUtils.replaceContent(template.content, map, false);
        }
        return template != null ? template.content : "";
    }

    @Autowired
    private EmployeeVhrTempService employeeVhrTempService;

    /**
     * Them moi ho so nhan su
     *
     * @param interviewCv
     */
    private void createRecruiteeAccountAndAdditionalInfo(InterviewSessionCvEntity interviewCv) {
        EmployeeVhrTempDTO.EmployeeVhrTempRequest profile = null;
        EmployeeVhrTempDTO.EmployeeVhrTempResponse employee = employeeVhrTempService.findByInterviewCvId(interviewCv.getInterviewSessionCvId());
        if (employee != null) {
            profile = objectMapper.convertValue(employee, EmployeeVhrTempDTO.EmployeeVhrTempRequest.class);
        } else {
            profile = new EmployeeVhrTempDTO.EmployeeVhrTempRequest();
        }
        ArrayList<RecruiteeAccountEntity> lstAccount = recruiteeAccountRepository.findByInterviewCvId(interviewCv.getInterviewSessionCvId());
        if (lstAccount != null && lstAccount.size() > 0) {
            profile.accountId = lstAccount.get(0).getRecruiteeAccountId();
        }
        if ((profile.employeeVhrTempId == null || profile.employeeVhrTempId == 0) && (interviewCv.getContractType() != profile.contractType || profile.contractType == null)) {
            //<editor-fold desc="Xoa du lieu neu da tao truoc do">
            if (profile.contractType != null && interviewCv.getContractType() != profile.contractType) {
                List<RecruitProfileAttachmentEntity> lstAttachment = recruitProfileAttachmentRepository.findByInterviewSessionCvId(interviewCv.getInterviewSessionCvId());
                if (lstAttachment != null && lstAttachment.size() > 0) {
//                    List<Long> ids = lstAttachment
//                            .stream().map(RecruitProfileAttachmentEntity::getRecProfileAttachmentId)
//                            .collect(Collectors.toList());
                    recruitProfileAttachmentRepository.deleteAll(lstAttachment);
                }
            }
            //</editor-fold>
            addDocumentAttachment(profile.contractType, profile.interviewSessionCvId);
        }
        profile.fullname = interviewCv.getCvEntity().getFullName();

        VhrFutureOrganizationEntity unitEntity = organizationRepository.findById(interviewCv.getInterviewSessionEntity().getUnitId()).orElseThrow(() -> new NotFoundException("Không tìm thấy đơn vị"));
        profile.organizationId = unitEntity.getOrganizationId();
        profile.organizationName = unitEntity.getName();

        if (interviewCv.getInterviewSessionEntity().getDepartmentId() != null) {
            VhrFutureOrganizationEntity departmentEntity = organizationRepository.findById(interviewCv.getInterviewSessionEntity().getDepartmentId()).orElseThrow(() -> new NotFoundException("Không tìm thấy phòng ban"));
            profile.organizationId = departmentEntity.getOrganizationId();
            profile.organizationName = departmentEntity.getName();
        }

        profile.interviewDate = interviewCv.getInterviewDate();
        if (interviewCv.getInterviewDate() == null && interviewCv.getInterviewSessionEntity().getStartDate() != null) {
            profile.interviewDate = interviewCv.getInterviewSessionEntity().getStartDate().toLocalDate();
        }

        profile.userBirthday = interviewCv.getCvEntity().getUserBirthday();
        profile.gender = interviewCv.getCvEntity().getGender();

        profile.personalIdNumber = interviewCv.getCvEntity().getPersonalIdNumber();
        profile.mobileNumber = interviewCv.getCvEntity().getPhoneNumber();
        profile.email = interviewCv.getCvEntity().getEmail();

        profile.positionId = interviewCv.getCvEntity().getPositionId();
        profile.positionName = interviewCv.getCvEntity().getApplyPosition();

        profile.interviewSessionCvId = interviewCv.getInterviewSessionCvId();

        profile.resultEmailSendDate = interviewCv.getResultEmailSendDate();

        profile.contractType = interviewCv.getContractType();
        profile.labourContractTypeId = interviewCv.getContractType().getValue();

        profile.filePath = interviewCv.getCvEntity().getFilePath();
        profile.fileName = interviewCv.getCvEntity().getFileName();

        profile.educationSubjectName = interviewCv.getCvEntity().getMajor();
        profile.trainingSpeciality = interviewCv.getCvEntity().getTechnicalExpertiseProfession();
        profile.educationName = interviewCv.getCvEntity().getSchool();

        profile.joinCompanyDate = interviewCv.getStartingDate();

        if (profile.status == null) {
            profile.status = EmployeeStatusConstant.draft;
        }

        if (profile.employeeVhrTempId != null && profile.employeeVhrTempId > 0) {
            employeeVhrTempService.update(profile.employeeVhrTempId, profile);
        } else {
            employeeVhrTempService.create(profile);
        }
    }

    /**
     * Them ho so nhan vien can upload
     *
     * @param contractType
     * @param interviewCvId
     */
    private void addDocumentAttachment(ContractType contractType, Long interviewCvId) {
        if (contractType != null && contractType != ContractType.UNKNOWN) {
            ArrayList<DocumentTypeEntity> lstDocument = documentTypeRepository.findByType(contractType);
            for (DocumentTypeEntity item : lstDocument) {
                RecruitProfileAttachmentEntity entity = new RecruitProfileAttachmentEntity();
                entity.setInterviewSessionCvId(interviewCvId);
                entity.setDocumentTypeId(item.getDocumentTypeId());
                entity.setStatus(AttachmentDocumentStatus.PENDING);
                recruitProfileAttachmentRepository.save(entity);
            }
        }
    }

    @Override
    public FileDTO.FileResponse exportExcel(SearchDTO searchRequest) {

        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<InterviewSessionCvEntity> specs = SearchUtils.getSpecifications(searchRequest);
        Page<InterviewSessionCvEntity> list;
        if (searchRequest.pagedFlag) {
            list = interviewSessionCvRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = interviewSessionCvRepository.findAll(p);
        }
        try {
            Resource resource = new ClassPathResource("template/KetQuaPhongVan.xlsx");
            InputStream inp = resource.getInputStream();
            Workbook workbook = new XSSFWorkbook(inp);
            Sheet sheet = workbook.getSheetAt(0);
            CellStyle borderCellStyle = workbook.createCellStyle();
            borderCellStyle.setBorderTop(BorderStyle.MEDIUM);
            borderCellStyle.setBorderBottom(BorderStyle.MEDIUM);
            borderCellStyle.setBorderLeft(BorderStyle.MEDIUM);
            borderCellStyle.setBorderRight(BorderStyle.MEDIUM);
            LocalDate now = LocalDate.now();
            Cell tempContentCell = sheet.getRow(2).getCell(10);
            tempContentCell.setCellValue("Hà Nội, ngày " + now.getDayOfMonth() + " tháng " + now.getMonth().getValue() + " năm " + now.getYear());
            int size = list.getContent().size();
            Row tempContentRow = sheet.getRow(2);
            tempContentCell = tempContentRow.getCell(0);
            List<InterviewSessionCvEntity> lstInterviewCv = list.getContent();
            Font font = sheet.getWorkbook().createFont();
            font.setFontName("Times New Roman");
            font.setFontHeightInPoints((short) 11);
            CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
            cellStyle.setFont(font);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setWrapText(true);
            for (int i = 0; i < size; i++) {
                Row rows = sheet.createRow(i + 8);
                Cell cell = rows.createCell(0);
                cell.setCellValue(i + 1);
                cell.setCellStyle(cellStyle);
                //Ho va ten
                cell = rows.createCell(1);
                cell.setCellValue(lstInterviewCv.get(i).getCvEntity() != null && lstInterviewCv.get(i).getCvEntity().getFullName() != null ? lstInterviewCv.get(i).getCvEntity().getFullName() : "");
                cell.setCellStyle(cellStyle);
                //Nam sinh
                cell = rows.createCell(2);
                cell.setCellValue(lstInterviewCv.get(i).getCvEntity() != null && lstInterviewCv.get(i).getCvEntity().getUserBirthday() != null ? (lstInterviewCv.get(i).getCvEntity().getUserBirthday().getYear() + "") : "");
                cell.setCellStyle(cellStyle);
                //Gioi tinh
                cell = rows.createCell(3);
                cell.setCellValue(lstInterviewCv.get(i).getCvEntity() != null && lstInterviewCv.get(i).getCvEntity().getGender() != null && lstInterviewCv.get(i).getCvEntity().getGender().equals(Gender.MALE) ? "Nam" : "Nữ");
                cell.setCellStyle(cellStyle);
                //trinh do
                cell = rows.createCell(4);
                cell.setCellValue(lstInterviewCv.get(i).getCvEntity() != null && lstInterviewCv.get(i).getCvEntity().getTechnicalExpertiseProfession() != null ? lstInterviewCv.get(i).getCvEntity().getTechnicalExpertiseProfession() : "");
                cell.setCellStyle(cellStyle);
                //chuyen mon
                cell = rows.createCell(5);
                cell.setCellValue(lstInterviewCv.get(i).getCvEntity().getMajor() != null ? lstInterviewCv.get(i).getCvEntity().getMajor() : "");
                cell.setCellStyle(cellStyle);
                //noi dao tao
                cell = rows.createCell(6);
                cell.setCellValue(lstInterviewCv.get(i).getCvEntity().getSchool() != null ? lstInterviewCv.get(i).getCvEntity().getSchool() : "");
                cell.setCellStyle(cellStyle);
                //chuyen mon
                cell = rows.createCell(7);
                cell.setCellValue(lstInterviewCv.get(i).getExpertiseGrade() != null ? fmt(lstInterviewCv.get(i).getExpertiseGrade()) : "");
                cell.setCellStyle(cellStyle);
                //phu hop voi vh viettel
                cell = rows.createCell(8);
                cell.setCellValue(lstInterviewCv.get(i).getCulturalAppropriationGrade() != null ? fmt(lstInterviewCv.get(i).getCulturalAppropriationGrade()) : "");
                cell.setCellStyle(cellStyle);
                //phong cach
                cell = rows.createCell(9);
                cell.setCellValue(lstInterviewCv.get(i).getStyleGrade() != null ? fmt(lstInterviewCv.get(i).getStyleGrade()) : "");
                cell.setCellStyle(cellStyle);
                //kinh nghiem
                cell = rows.createCell(10);
                cell.setCellValue(lstInterviewCv.get(i).getExperienceGrade() != null ? fmt(lstInterviewCv.get(i).getExperienceGrade()) : "");
                cell.setCellStyle(cellStyle);
                //tong diem
                cell = rows.createCell(11);
                cell.setCellValue(lstInterviewCv.get(i).getSumPoint() != null ? fmt(lstInterviewCv.get(i).getSumPoint()) : "");
                cell.setCellStyle(cellStyle);
                //trac nghiem tam the
                cell = rows.createCell(12);
                cell.setCellValue(lstInterviewCv.get(i).getCharacteristicMultipleChoice());
                cell.setCellStyle(cellStyle);
                //tieng anh
                cell = rows.createCell(13);
                cell.setCellValue(lstInterviewCv.get(i).getEnglish());
                cell.setCellStyle(cellStyle);
                //muc do hieu biet cong nghe phan mem
                cell = rows.createCell(14);
                cell.setCellValue(lstInterviewCv.get(i).getTechnologyFamiliarityGrade() != null ? fmt(lstInterviewCv.get(i).getTechnologyFamiliarityGrade()) : "");
                cell.setCellStyle(cellStyle);
                //danh gia va ket luan
                cell = rows.createCell(15);
                cell.setCellValue(HTMLtoExcel.fromHtmlToCellValue(lstInterviewCv.get(i).getReview(), workbook));
                cell.setCellStyle(cellStyle);
                //ghi chu
                cell = rows.createCell(16);
                cell.setCellValue("");
                cell.setCellStyle(cellStyle);
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);
            byteArrayOutputStream.close();
            workbook.close();
            byte[] data = Base64.getEncoder().encode(byteArrayOutputStream.toByteArray());
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = "KetQuaPhongVan.xlsx";
            fileDTO.size = (long) data.length;
            fileDTO.type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            fileDTO.data = new String(data, StandardCharsets.US_ASCII);

            return fileDTO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public InterviewSessionCvResponse findByTransCode(String transCode) {
        InterviewSessionCvEntity entity = interviewSessionCvRepository.findByTransCode(transCode);
        return objectMapper.convertValue(entity, InterviewSessionCvResponse.class);
    }

    @Override
    public InterviewSessionCvResponse updateData(Long id, InterviewSessionCvResponse request) {
        InterviewSessionCvEntity entity = interviewSessionCvRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        entity.setTransCode(request.transCode);
        entity.setIsCallVoffice(request.isCallVoffice);
        if (request.interviewReportFile != null && request.interviewReportFile.trim().length() > 0) {
            entity.setInterviewReportFile(request.interviewReportFile);
        }
        entity = interviewSessionCvRepository.save(entity);
        return objectMapper.convertValue(entity, InterviewSessionCvResponse.class);
    }

    /**
     * Tai xuong file trinh ky v-office
     *
     * @param id
     * @return
     */
    @Override
    public FileDTO.FileResponse downloadVofficeSignedFile(Long id) {
        InterviewSessionCvEntity entity = interviewSessionCvRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        if (entity.getIsCallVoffice() != null && entity.getIsCallVoffice() && entity.getSignedFile() != null && entity.getSignedFile().trim().length() > 0) {
            try {
                String pathStore = folderExtension.getUploadFolder();
                File file = new File(pathStore + File.separator + entity.getSignedFile().replaceAll("-", "/"));
                byte[] data = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
                String contentType = Files.probeContentType(file.toPath());
                FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
                fileDTO.fileName = file.getName();
                fileDTO.size = file.length();
                fileDTO.type = contentType;
                fileDTO.data = new String(data, StandardCharsets.US_ASCII);
                return fileDTO;
            } catch (IOException e) {
                e.printStackTrace();
                FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
                fileDTO.filePath = e.getMessage();
                return fileDTO;
            }

        }
        return null;
    }

    @Override
    public InterviewSessionCvResponse updateInterviewReportFile(String fileName, Long id) {
        InterviewSessionCvEntity entity = interviewSessionCvRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        entity.setInterviewReportFile(fileName);
        entity = interviewSessionCvRepository.save(entity);
        return objectMapper.convertValue(entity, InterviewSessionCvResponse.class);
    }

    @Override
    public InterviewSessionCvResponse updateSignedFile(String fileName, Long id) {
        InterviewSessionCvEntity entity = interviewSessionCvRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        entity.setSignedFile(fileName);
        entity = interviewSessionCvRepository.save(entity);
        return objectMapper.convertValue(entity, InterviewSessionCvResponse.class);
    }

    @Override
    public void updateNewFileAfterVO(Long id, String signedFile, String interviewReportFile) {
        InterviewSessionCvEntity entity = interviewSessionCvRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        entity.setSignedFile(signedFile);
        entity.setInterviewReportFile(interviewReportFile);
        entity = interviewSessionCvRepository.save(entity);
    }

    @Override
    public FileDTO.FileResponse exportInterviewResult(ExportInterviewResultDTO input) {
        InterviewSessionCvResponse response = findOneById(input.id);
        CvDTO.CvResponse cvResponse = response.cvEntity;
        response.cvEntity = null;
        response.interviewSessionEntity = null;
        response.lstAttachment = null;
//        Map<String, Object> map = objectMapper.convertValue(response, Map.class);
        Map<String, String> map = CustomMapper.convert(response);
        Map<String, String> cvMap = CustomMapper.convert(cvResponse);
        map.putAll(cvMap);
        map.put("currentDate", java.time.LocalDate.now().toString());
        //<editor-fold desc="gan gia tri 0 neu null">
        if (response.experienceGrade == null) {
            response.experienceGrade = 0F;
        }
        if (response.styleGrade == null) {
            response.styleGrade = 0F;
        }
        if (response.culturalAppropriationGrade == null) {
            response.culturalAppropriationGrade = 0F;
        }
        if (response.technologyFamiliarityGrade == null) {
            response.technologyFamiliarityGrade = 0F;
        }
        Float totalPoint = (response.experienceGrade + response.styleGrade + response.culturalAppropriationGrade + response.technologyFamiliarityGrade);
        //</editor-fold>
        map.put("totalScore", totalPoint.toString());
        HashMap<String, String> hm = new HashMap<>();
        for (String key : map.keySet()) {
            if (map.get(key) != null) {
                hm.put(key, map.get(key));
            }
        }
        try {
            String pathStore = folderExtension.getUploadFolder();
            File file = new ClassPathResource("template/KetQuaTuyenDung.docx").getFile();
            String path = file.getPath();
            HashMap<String, List<InterviewResultDTO>> resultMap = new HashMap<>();
            resultMap.put("overview", input.list);
            ExportWord<InterviewResultDTO> exportWord = new ExportWord<>();
            String resultPath = exportWord.exportV2(path, pathStore, "KetQuaTuyenDung", hm, ExportFileExtension.PDF, resultMap, "signature");
            file = new File(resultPath);
            String contentType = Files.probeContentType(file.toPath());
            String fileName = folderExtension.getFileName(resultPath);
            //Cap nhat file ket qua tuyen dung
            updateInterviewReportFile(fileName, input.id);
            byte[] data = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = file.getName();
            fileDTO.filePath = fileName;
            fileDTO.size = file.length();
            fileDTO.type = contentType;
            fileDTO.data = new String(data, StandardCharsets.US_ASCII);
            return fileDTO;
        } catch (Exception ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
        }
        return null;
    }

    /**
     * Tai xuong file bao cao tuyen dung nhan su
     *
     * @param id
     * @return
     */
    @Override
    public FileDTO.FileResponse downloadInterviewResult(Long id) {
        InterviewSessionCvEntity entity = interviewSessionCvRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        if (entity.getInterviewReportFile() != null && entity.getInterviewReportFile().trim().length() > 0) {
            Resource export = new ClassPathResource("/export");
            try {
                String pathStore = folderExtension.getUploadFolder();
                File file = new File(pathStore + File.separator + entity.getInterviewReportFile());
                String fileName = file.getName();
                byte[] data = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
                String contentType = Files.probeContentType(file.toPath());
                FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
                fileDTO.fileName = fileName;
                fileDTO.size = file.length();
                fileDTO.type = contentType;
                fileDTO.data = new String(data, StandardCharsets.US_ASCII);
                return fileDTO;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    private static String fmt(float d) {
        if (d == (long) d) return String.format("%d", (long) d);
        else return String.format("%s", d);
    }

    private InterviewSessionCvResponse convertEntity2Response(InterviewSessionCvEntity entity) {
        InterviewSessionCvResponse response = new InterviewSessionCvResponse();
        response = objectMapper.convertValue(entity, InterviewSessionCvResponse.class);
        response.interviewDate = entity.getInterviewSessionEntity().getStartDate().toLocalDate();
        response.cvId = entity.getCvEntity().getCvId();
        response.interviewSessionId = entity.getInterviewSessionEntity().getInterviewSessionId();
        response.lstAttachment = fileAttachmentRepository.findByFileItemIdAndFileType(entity.getInterviewSessionCvId(), FileTypeConstant.InterviewCv).stream().map(obj -> this.objectMapper.convertValue(obj, FileAttachmentDTO.FileAttachmentResponse.class)).collect(Collectors.toList());
        return response;
    }
}
