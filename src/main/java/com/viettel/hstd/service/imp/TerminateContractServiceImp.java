package com.viettel.hstd.service.imp;

import com.aspose.words.Document;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.itextpdf.text.DocumentException;
import com.viettel.hstd.constant.*;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.constant.ExportFileExtension;
import com.viettel.hstd.core.constant.FileTypeConstant;
import com.viettel.hstd.core.constant.VOfficeSignConstant;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.*;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.FileAttachmentDTO;
import com.viettel.hstd.dto.hstd.InterviewResultDTO;
import com.viettel.hstd.dto.hstd.TerminateContractDTO.*;
import com.viettel.hstd.entity.hstd.*;
import com.viettel.hstd.entity.vps.EmployeeVhrEntity;
import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import com.viettel.hstd.exception.AccessDeniedException;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.filter.HSTDFilter;
import com.viettel.hstd.repository.hstd.*;
import com.viettel.hstd.repository.vps.EmployeeVhrRepository;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.EmployeeVhrService;
import com.viettel.hstd.service.inf.FileService;
import com.viettel.hstd.service.inf.SmsMessageService;
import com.viettel.hstd.service.inf.TerminateContractService;
import com.viettel.hstd.util.DateUtils;
import com.viettel.hstd.util.FileUtils;
import com.viettel.hstd.util.FolderExtension;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.XmlException;
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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.viettel.hstd.constant.FileTemplateConstant.LABOR_CONTRACT_OUTPUT_NAME;

@Service
@Slf4j
@Transactional
public class TerminateContractServiceImp extends BaseService implements TerminateContractService {

    // <editor-fold desc="Khai bao">
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    Message message;
    @Autowired
    MapUtils mapUtils;
    @Autowired
    private TerminateContractRepository terminateContractRepository;
    @Autowired
    private FileAttachmentRepository fileAttachmentRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private EmployeeVhrRepository employeeVhrRepository;

    @Autowired
    private VhrFutureOrganizationRepository vhrFutureOrganizationRepository;
    @Autowired
    private FolderExtension folderExtension;
    @Autowired
    AuthenticationFacade authenticationFacade;
    @Autowired
    private EmployerInfoRepository employerInfoRepository;
    @Autowired
    private EmployeeVhrService employeeVhrService;
    @Autowired
    private ResignSessionContractRepository resignSessionContractRepository;

    @Autowired
    private SmsMessageService smsMessageService;

    @Autowired
    private HSTDFilter hstdFilter;

    @Autowired
    private FileService fileService;

    // @Autowired
    // private RecruiteeAccountRepository recruiteeAccountRepository;
    // </editor-fold>

    @Override
    public Page<TerminateContractResponse> findPage(SearchDTO searchRequest) {
        hstdFilter.unitDepartmentFilter(searchRequest, "organizationId");
        Pageable pageable = SearchUtils.getPageable(searchRequest);

        Page<TerminateContractEntity> list;
        // <editor-fold desc="tim kiem theo nguoi quan ly truc tiep">
        if (searchRequest != null && searchRequest.criteriaList.stream().anyMatch(x -> x.getField().equalsIgnoreCase("managerId"))) {
            searchRequest.criteriaList = searchRequest.criteriaList.stream().filter(x -> !x.getField().equalsIgnoreCase("managerId")).collect(Collectors.toList());
            SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
            SearchDTO.SearchCriteria manager = new SearchDTO.SearchCriteria();
            manager.setField("managerId");
            manager.setOperation(Operation.EQUAL);
            manager.setAndFlag(true);
            manager.setValue(sSoResponse.getEmployeeId());
            manager.setType(SearchType.NUMBER);
            searchRequest.criteriaList.add(manager);
        }
        // </editor-fold>
        Specification<TerminateContractEntity> specs = SearchUtils.getSpecifications(searchRequest);
        if (searchRequest.pagedFlag) {
            list = terminateContractRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = terminateContractRepository.findAll(p);
        }
        return list.map(obj -> this.objectMapper.convertValue(obj, TerminateContractResponse.class));
    }

    @Override
    public TerminateContractResponse findOneById(Long id) {
        TerminateContractEntity entity = terminateContractRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        TerminateContractResponse response = objectMapper.convertValue(entity, TerminateContractResponse.class);
        if (entity.getContractFile() == null) {
            Optional<ContractEntity> contract = contractRepository.findById(entity.getContractId());
            if (contract.isPresent()) {
                response.contractFile = contract.get().getContractFile();
                entity.setContractFile(response.contractFile);
                terminateContractRepository.save(entity);
            }
        }
        response.lstAttachment = fileAttachmentRepository.findByFileItemIdAndFileType(id, FileTypeConstant.TerminateContract).stream().map(obj -> this.objectMapper.convertValue(obj, FileAttachmentDTO.FileAttachmentResponse.class)).collect(Collectors.toList());
        return response;
    }

    @Override
    public Boolean delete(Long id) {
        TerminateContractEntity terminateContractEntity = terminateContractRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        ContractEntity contractEntity = this.contractRepository.findById(terminateContractEntity.getContractId()).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        contractEntity.setIsTerminate(null);
        contractRepository.save(contractEntity);
        terminateContractRepository.softDelete(id);
        return true;
    }

    @Override
    @Transactional
    public TerminateContractResponse create(TerminateContractRequest request) {
        SSoResponse ssoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        ContractEntity contractEntity = contractRepository.findById(request.contractId).orElseThrow(() -> new NotFoundException("Kh??ng t??m th???y h???p ?????ng"));
        request.contractDuration = contractEntity.getContractDuration();
        TerminateContractEntity entity = objectMapper.convertValue(request, TerminateContractEntity.class);
        entity.setStatus(TerminateStatusConstant.draft);
        if (request.submitDate == null) {
            entity.setSubmitDate(LocalDate.now());
        }
        if (request.type != null && request.type == 0) {
            // Khi hr ket thuc hop dong
            entity.setStatus(TerminateStatusConstant.managerApproval);
            entity.setIsTerminateByHr(true);
            entity.setApprovalDate(LocalDate.now().plusDays(1));
            entity.setConfirmDate(LocalDate.now().plusDays(1));
        } else {
            entity.setIsTerminateByHr(false);

            if (request.expectedDate == null || LocalDate.now().isAfter(request.expectedDate)) {
                throw new BadRequestException("Ng??y d??? ki???n ngh??? vi???c ph???i l???n h??n ng??y hi???n t???i");
            }

            if (request.managerId == null) {
                throw new NotFoundException("B???n ch??a ??i???n ng?????i qu???n l??");
            }
            if (ssoResponse.getEmployeeId().equals(request.managerId)) {
                throw new BadRequestException("Kh??ng th??? ch???n ng?????i qu???n l?? tr??ng v???i b???n ???????c");
            }
            EmployeeVhrEntity empTemp = employeeVhrRepository.findById(request.managerId).orElseThrow(() -> new NotFoundException("Kh??ng t??m th???y ng?????i qu???n l??"));
            request.managerName = empTemp.getEmptypeName();

            if (request.receiverHandoverId == null) {
                throw new NotFoundException("B???n ch??a ??i???n ng?????i ti???p qu???n c??ng vi???c");
            }

            if (ssoResponse.getEmployeeId().equals(request.receiverHandoverId)) {
                throw new BadRequestException("Kh??ng th??? ch???n ng?????i ti???p qu???n c??ng vi???c tr??ng v???i b???n ???????c");
            }

            EmployeeVhrEntity receiverEmployee = employeeVhrRepository.findById(request.receiverHandoverId).orElseThrow(() -> new NotFoundException("Kh??ng t??m th???y ng?????i ti???p qu???n c??ng vi???c"));
            request.receiverHandover = receiverEmployee.getEmptypeName();

        }

        if (request.contractId == null || request.contractId == 0) {
            SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
            ArrayList<ContractEntity> lstContract = null;
            // Neu nguoi dung dang nhap bang phan mem
            if (sSoResponse.getIsHsdtAccount()) {
                lstContract = contractRepository.findByAccountIdAndIsActiveTrue(sSoResponse.getId());
            } else {
                lstContract = contractRepository.findByEmployeeCodeAndIsActiveTrue(sSoResponse.getStaffCode());
            }
            if (lstContract != null && lstContract.size() > 0) {
                for (ContractEntity item : lstContract) {
                    if (item.getEffectiveDate() != null && item.getEffectiveDate().compareTo(LocalDate.now()) <= 0 && item.getExpiredDate() != null && item.getExpiredDate().compareTo(LocalDate.now()) >= 0 && !item.getIsTerminate()) {
                        if (item.getIsTerminate() == null || !item.getIsTerminate()) {
                            request.contractId = item.getContractId();
                            break;
                        }
                    }
                }
            }
        }
        if (request.contractId == null) {
            throw new BadRequestException("Kh??ng t??m th???y h???p ?????ng");
        }
        ContractEntity contract = null;
        if (request.contractId > 0) {
            // Validate neu da cham dut hop dong thi bao loi
            TerminateContractEntity terminate = terminateContractRepository.findByContractId(request.contractId);
            if (terminate != null) {
                throw new BadRequestException("H???p ?????ng ???? ch???m d???t");
            }

            contract = contractRepository.findById(request.contractId).orElseThrow(() -> new NotFoundException("Kh??ng t??m th???y h???p ?????ng v???i id: " + request.contractId));

            if (contract.getResignStatus().getValue() > ResignStatus.RECEIVED_VOFFICE2_AND_SUCCESS.getValue()) {
                List<ResignSessionContractEntity> resignSessionContractEntities = resignSessionContractRepository.findByContractId(contract.getContractId());
                if (!resignSessionContractEntities.isEmpty()) {
                    ResignSessionContractEntity resignSessionContractEntity = resignSessionContractEntities.get(0);
                    ContractEntity newContract = contractRepository.findByContractId(resignSessionContractEntity.newContractId).orElse(null);
                    if (newContract != null && newContract.getResignStatus().getValue() >= ResignStatus.TEMP_CONTRACT_CREATE.getValue()) {
                        throw new BadRequestException("Nh??n vi??n c??  1 h???p ?????ng t??i k?? t???m th???i, li??n h??? HR ????? x??a b??? h???p ?????ng t??i k?? t???m th???i r???i th??? l???i");
                    }
                }

            }
        }
        // Gan ngay tuyen dung
        if (contract.getEmployeeId() != null) {
            EmployeeVhrEntity employee = employeeVhrRepository.findById(contract.getEmployeeId()).orElse(null);
            if (employee == null) {
                employee = objectMapper.convertValue(employeeVhrService.findOneByIdCombineDb(contract.getEmployeeId()), EmployeeVhrEntity.class);
                if (employee.getJoinCompanyDate() == null) {
                    List<ContractEntity> contractEntityList = contractRepository.findByEmployeeCode(employee.getEmployeeCode());
                    if (contractEntityList.size() > 0) {
                        LocalDate joinDate = null;
                        for (ContractEntity c : contractEntityList) {
                            joinDate = (joinDate == null) ? c.getEffectiveDate() : (joinDate.isBefore(c.getEffectiveDate()) ? joinDate : c.getEffectiveDate());
                        }
                        entity.setJoinCompanyDate(joinDate);
                    }
                }
            }

            if (employee != null && employee.getJoinCompanyDate() != null) {
                // DateTimeFormatter formatter =
                // DateTimeFormatter.ofPattern(FormatConstant.LOCAL_DATE_FORMAT);
                try {
                    LocalDate joinCompanyDate = employee.getJoinCompanyDate();
                    entity.setJoinCompanyDate(joinCompanyDate);
                } catch (Exception ex) {
                    String stackTrace = ExceptionUtils.getStackTrace(ex);
                    log.error(stackTrace);
                }
            }
            // Update unit and department form contract
            entity.setOrganizationId(contract.getUnitId());
            if (contract.getUnitId() != null) {
                VhrFutureOrganizationEntity unit = vhrFutureOrganizationRepository.findById(contract.getUnitId()).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
                entity.setOrganizationName(unit.getName());
                if (request.realUnitId == null) {
                    entity.setRealUnitId(contract.getUnitId());
                    entity.setRealUnitName(unit.getName());
                }
            }
            entity.setDepartmentId(contract.getDepartmentId());
            if (contract.getDepartmentId() != null) {
                VhrFutureOrganizationEntity department = vhrFutureOrganizationRepository.findById(contract.getDepartmentId()).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
                entity.setDepartmentName(department.getName());
            }

        }
        // <editor-fold desc="Gan du lieu tu hop dong">
        entity.setEmployeeCode(contract.getEmployeeCode());
        entity.setEmployeeId(contract.getEmployeeId());
        entity.setEmployeeName(contract.getEmployeeName());
        entity.setEffectiveDate(contract.getEffectiveDate());
        entity.setExpiredDate(contract.getExpiredDate());
        entity.setBirthDate(contract.getBirthDate());
        entity.setPlaceOfBirth(contract.getPlaceOfBirth());
        entity.setGender(contract.getGender());
        entity.setTrainingLevel(contract.getTrainingLevel());
        entity.setTrainingSpeciality(contract.getTrainingSpeciality());
        entity.setPermanentAddress(contract.getPermanentAddress());
        entity.setPersonalIdIssuedDate(contract.getPersonalIdIssuedDate());
        entity.setPersonalIdIssuedPlace(contract.getPersonalIdIssuedPlace());
        entity.setPersonalIdNumber(contract.getPersonalIdNumber());
        entity.setMobileNumber(contract.getMobileNumber());
        entity.setPositionCode(contract.getPositionCode());
        entity.setPositionId(contract.getPositionId());
        entity.setPositionName(contract.getPositionName());
        entity.setCurrentAddress(contract.getCurrentAddress());
        entity.setContractType(contract.getContractType());
        entity.setSignedDate(contract.getSignedDate());
        entity.setTransCode(null);

        if (contract.getSignedFileEncodePath() != null && contract.getTransCode() != null) {
            entity.setContractFile(contract.getSignedFileEncodePath());
            entity.setSignedFile(contract.getSignedFileEncodePath());
        } else {
            entity.setContractFile(contract.getContractFileEncodePath());
        }

        entity.setAccountId(contract.getAccountId());
        entity.setContractNumber(contract.getContractNumber());
        entity.setTransCodeContract(contract.getTransCode());
        entity.setAccountId(contract.getAccountId());
        entity.setContractNumber(contract.getContractNumber());


        if (request.signaturePath == null && request.type == 0) {
            if (contract.getSignatureFileEncodePath() != null && contract.getSignatureName() != null) {
                entity.setSignaturePath(contract.getSignatureFileEncodePath());
                entity.setSignatureName(contract.getSignatureName());
            }
        }

        // </editor-fold>

        initFile(entity);

        entity = terminateContractRepository.save(entity);

        addLog("TERMINATE_CONTRACT", "CREATE", new Gson().toJson(request));
        TerminateContractResponse response = objectMapper.convertValue(entity, TerminateContractResponse.class);
        response.lstAttachment = new ArrayList<>();
        // <editor-fold desc="Them don nghi viec, xac nhan cong no...">
        if (request.lstAttachment != null && request.lstAttachment.size() > 0) {
            for (FileAttachmentDTO.FileAttachmentRequest item : request.lstAttachment) {
                if (item.fileName != null && item.filePath != null && item.filePath.trim().length() > 0) {
                    FileAttachmentEntity attachment = objectMapper.convertValue(item, FileAttachmentEntity.class);
                    attachment.setFileItemId(entity.getTerminateContractId());
                    attachment.setFileType(FileTypeConstant.TerminateContract);
                    attachment = fileAttachmentRepository.save(attachment);
                    response.lstAttachment.add(objectMapper.convertValue(attachment, FileAttachmentDTO.FileAttachmentResponse.class));
                }
            }
        }

        // Save contract
        contract.setIsTerminate(true);
        contractRepository.save(contract);

        // G???i tin nh???n
        // String phoneNumber =
        // employeeVhrRepository.getPhoneNumberByEmployeeId(entity.getManagerId());
        // smsMessageService.sendSmsMessage(phoneNumber, "Nhan vien " +
        // StringUtils.unAccent(request.employeeName) + "voi ma NV " +
        // request.employeeCode + " dang muon xin nghi viec. Xin moi vao phan mem Ho so
        // dien tu de kiem tra");

        // </editor-fold>
        return response;
    }

    @Override
    public TerminateContractResponse update(Long id, TerminateContractRequest request) {
        if (request.contractId == null) {
            throw new NotFoundException("Kh??ng t??m th???y h???p ?????ng");
        }
        SSoResponse ssoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        ContractEntity contractEntity = contractRepository.findById(request.contractId).orElseThrow(() -> new NotFoundException("Kh??ng t??m th???y h???p ?????ng"));
        request.contractDuration = contractEntity.getContractDuration();
        TerminateContractEntity entity = terminateContractRepository.findById(id).orElseThrow(() -> new NotFoundException("Kh??ng t??m th???y d??? li???u ch???m d???t h???p ?????ng"));
        TerminateContractEntity newE = objectMapper.convertValue(request, TerminateContractEntity.class);
        mapUtils.customMap(newE, entity);

        if (entity.getIsTerminateByHr()) {
            throw new BadRequestException("Kh??ng th??? c???p nh???t ch???m d???t h???p ?????ng do HR ch???m d???t");
        }

        if (request.expectedDate == null || LocalDate.now().isAfter(request.expectedDate)) {
            throw new BadRequestException("Ng??y d??? ki???n ngh??? vi???c ph???i l???n h??n ng??y hi???n t???i");
        }

        if (request.managerId == null) {
            throw new NotFoundException("B???n ch??a ??i???n ng?????i qu???n l??");
        }

        if (ssoResponse.getEmployeeId().equals(request.managerId)) {
            throw new BadRequestException("Kh??ng th??? ch???n ng?????i qu???n l?? tr??ng v???i b???n ???????c");
        }

        if (request.receiverHandoverId == null) {
            throw new NotFoundException("B???n ch??a ??i???n ng?????i ti???p qu???n c??ng vi???c");
        }

        if (ssoResponse.getEmployeeId().equals(request.receiverHandoverId)) {
            throw new BadRequestException("Kh??ng th??? ch???n ng?????i ti???p qu???n c??ng vi???c tr??ng v???i b???n ???????c");
        }

        entity.setTerminateContractId(id);
        entity.setTransCodeContract(contractEntity.getContractCode());

        if (contractEntity.getSignedFileEncodePath() != null && contractEntity.getTransCode() != null) {
            entity.setContractFile(contractEntity.getSignedFileEncodePath());
        } else {
            entity.setContractFile(contractEntity.getContractFileEncodePath());
        }

        initFile(entity);
        entity = terminateContractRepository.save(entity);
        addLog("TERMINATE_CONTRACT", "UPDATE", new Gson().toJson(request));
        TerminateContractResponse response = objectMapper.convertValue(entity, TerminateContractResponse.class);
        response.lstAttachment = new ArrayList<>();
        // <editor-fold desc="Them don nghi viec, xac nhan cong no...">
        ArrayList<FileAttachmentEntity> lstAttachment = fileAttachmentRepository.findByFileItemIdAndFileType(id, FileTypeConstant.TerminateContract);
        if (request.lstAttachment == null) {
            request.lstAttachment = new ArrayList<>();
        }
        for (FileAttachmentEntity attachment : lstAttachment) {
            if (request.lstAttachment.stream().anyMatch(x -> x.filePath.equalsIgnoreCase(attachment.getFilePath()))) {
                request.lstAttachment = request.lstAttachment.stream().filter(x -> !x.filePath.equalsIgnoreCase(attachment.getFilePath())).collect(Collectors.toList());
                response.lstAttachment.add(objectMapper.convertValue(attachment, FileAttachmentDTO.FileAttachmentResponse.class));
            } else {
                fileAttachmentRepository.delete(attachment);
            }
        }
        if (request.lstAttachment.size() > 0) {
            for (FileAttachmentDTO.FileAttachmentRequest item : request.lstAttachment) {
                if (item.fileName != null && item.filePath != null && item.filePath.trim().length() > 0) {
                    FileAttachmentEntity attachment = objectMapper.convertValue(item, FileAttachmentEntity.class);
                    attachment.setFileItemId(entity.getTerminateContractId());
                    attachment.setFileType(FileTypeConstant.TerminateContract);
                    attachment = fileAttachmentRepository.save(attachment);
                    response.lstAttachment.add(objectMapper.convertValue(attachment, FileAttachmentDTO.FileAttachmentResponse.class));
                }
            }
        }
        // </editor-fold>
        return response;
    }

    /**
     * Cap nhat trang thai cham dut hop dong (lan cuoi trinh ky voffice)
     *
     * @param transCode
     * @param id
     * @param status
     */
    @Override
    public void updateStatus(String transCode, Long id, Integer status, String filePath, String debtFile, String agrFile) {
        TerminateContractEntity entity = terminateContractRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("Kh??ng t??m th???y ch???m d???t h???p ?????ng")));
        entity.setStatus(status);
        if (Objects.equals(TerminateStatusConstant.vofficePending, status)) {
            if (filePath == null) {
                throw new BadRequestException("???? c?? l???i x???y ra h??y ki???m tra l???i");
            }
            entity.setSignedFile("");
            entity.setFilePath(filePath);
            entity.setFileName(folderExtension.getFileName(filePath));
            entity.setTransCode(transCode);
        }
        if (debtFile != null && debtFile.trim().length() > 0) {
            entity.setDebtFile(debtFile);
            entity.setTransCode(transCode);
        }
        if (agrFile != null && agrFile.trim().length() > 0) {
            entity.setAgrFileName(agrFile);
            entity.setTransCode(transCode);
        } else if (Objects.equals(TerminateStatusConstant.sevPending, status)) {
            entity.setSignedSevAllowancePath("");
            entity.setTransCodeSevAllowance(transCode);
            if (filePath != null && filePath.trim().length() > 0) {
                entity.setSevAllowancePath(filePath);
            }
        }
        terminateContractRepository.save(entity);
    }

    /**
     * Tim hop dong theo ma trinh ky voffice
     *
     * @param transCode
     * @return
     */
    @Override
    public TerminateContractResponse findByTransCode(String transCode) {
        TerminateContractEntity entity = terminateContractRepository.findByTransCode(transCode);
        if (entity == null) {
            return null;
        }
        return objectMapper.convertValue(entity, TerminateContractResponse.class);
    }

    /**
     * Cap nhat file don xin nghi viec
     *
     * @param fileName
     * @param id
     */
    @Override
    public void updateSignedFile(String fileName, Long id, Integer status, String documentCode, String debtFile, String agrFile) {
        TerminateContractEntity entity = terminateContractRepository.findById(id).orElseThrow(() -> new NotFoundException("Kh??ng t??m th???y ch???m d???t h???p ?????ng"));

        if (Objects.equals(TerminateStatusConstant.vofficeApproval, status) || Objects.equals(TerminateStatusConstant.vofficeReject, status)) {
            entity.setSignedFile(fileName);
            entity.setSignedDebtFile(debtFile);
            entity.setSignedAgrFileName(agrFile);
        } else if (Objects.equals(TerminateStatusConstant.sevApproval, status) || Objects.equals(TerminateStatusConstant.sevReject, status)) {
            entity.setSignedSevAllowancePath(fileName);
            if (Objects.equals(TerminateStatusConstant.sevApproval, status)) {
                entity.setQuitDate(LocalDate.now());
                entity.setDocumentCode(documentCode);
            }
        }
        entity.setStatus(status);
        terminateContractRepository.save(entity);

        if (TerminateStatusConstant.vofficeApproval.equals(status)) {
            this.sendMessageRequestTerminate(entity.getTerminateContractId(), SmsTerminateContractStatus.SIGNED_REQUEST);
        }
    }

    /**
     * Cap nhat quyet dinh cham dut hop dong lao dong
     *
     * @param request
     * @return
     */
    @Override
    public TerminateContractResponse updateDecision(TerminateContractRequest request) {
        if (request == null || request.terminateContractId == null) {
            throw new NotFoundException("Kh??ng c?? th??ng tin ch???m d???t h???p ?????ng");
        }
        TerminateContractEntity entity = terminateContractRepository.findById(request.terminateContractId).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        entity.setStartDate(request.startDate);
        entity.setEndDate(request.endDate);
        entity.setSevAllStartDate(request.sevAllStartDate);
        entity.setSevAllEndDate(request.sevAllEndDate);
        if (request.salary != null) {
            entity.setSalary(request.salary);
        }
        entity.setInsuranceTime(request.insuranceTime);
        if (request.sevAllTotalYear != null) {
            entity.setSevAllTotalYear(request.sevAllTotalYear);
        } else {
            if (request.sevAllStartDate != null && request.sevAllEndDate != null) {
                Period diff = Period.between(request.sevAllStartDate.withDayOfMonth(1), request.sevAllEndDate);
                int months = diff.getMonths();
                int years = diff.getYears();
                int days = diff.getDays();
                if (days > 15) {
                    months += 1;
                }
                if (months >= 6) {
                    years += 1;
                }
                entity.setSevAllTotalYear(years);
            }
        }
        if (entity.getSevAllTotalYear() != null) {
            double sevAllowance = 0.5 * entity.getSalary() * entity.getSevAllTotalYear();
            if (sevAllowance > 0) {
                DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                String str = NumberToVietnamese.convertToText(decimalFormat.format(sevAllowance));
                str = str.substring(0, 1).toUpperCase() + str.substring(1);
                entity.setSevAllowanceText(str);
                entity.setSevAllowance(sevAllowance);
            }
        }
        entity = terminateContractRepository.save(entity);
        return objectMapper.convertValue(entity, TerminateContractResponse.class);
    }

    @Override
    public FileDTO.FileResponse createSevAllowance(TerminateContractRequest request) {
        if (request == null || request.terminateContractId == null || request.terminateContractId < 0) {
            throw new NotFoundException("Kh??ng c?? th??ng tin ch???m d???t h???p ?????ng");
        }
        TerminateContractEntity entity = terminateContractRepository.findById(request.terminateContractId).orElseThrow(() -> new NotFoundException(message.getMessage("Kh??ng t??m th???y ch???m d???t h???p ?????ng")));
        // <editor-fold desc="tinh toan nam huong tro cap that nghiep">
        if (entity.getSevAllStartDate() != null && entity.getSevAllEndDate() != null) {
            Period diff = Period.between(entity.getSevAllStartDate().withDayOfMonth(1), entity.getSevAllEndDate());
            int months = diff.getMonths();
            int years = diff.getYears();
            int days = diff.getDays();
            if (days > 15) {
                months += 1;
            }
            if (months >= 6) {
                years += 1;
            }
            entity.setSevAllTotalYear(years);
        }
        String money = "";
        if (entity.getSevAllTotalYear() != null) {
            Double sevAllowance = (0.5 * entity.getSalary() * entity.getSevAllTotalYear());
            if (sevAllowance > 0) {
                // DecimalFormat decimalFormat = new DecimalFormat("#.0#");
                money = String.format("%.0f", sevAllowance);
                String str = NumberToVietnamese.convertToText(money);
                str = str.substring(0, 1).toUpperCase() + str.substring(1);
                entity.setSevAllowanceText(str);
                entity.setSevAllowance(sevAllowance);
            }
        }
        // </editor-fold>
        Map<String, String> map = CustomMapper.convert(entity);
        map = addNowToHashMap(map);
        HashMap<String, String> hm = new HashMap<>();
        for (String key : map.keySet()) {
            if (map.get(key) != null) {
                hm.put(key, map.get(key));
            }
        }
        boolean hasSevAllowance = false;
        if (request.type != null && request.type == 1) {
            hasSevAllowance = true;
            entity.setSevAllowanceType(request.type);
        } else {
            entity.setSevAllowanceType(0);
        }
        try {
            String pathStore = folderExtension.getUploadFolder();
            File file = new ClassPathResource("template/" + (hasSevAllowance ? "QDNghiViecCoTroCap" : "QDNghiViecKhongTroCap") + ".docx").getFile();
            String path = file.getPath();
            ExportWord<InterviewResultDTO> exportWord = new ExportWord<>();
            HashMap<String, List<InterviewResultDTO>> resultMap = new HashMap<>();
            String resultPath = exportWord.exportV2(path, pathStore, (hasSevAllowance ? HSDTConstant.SEV_ALLOWANCE_PREFIX : HSDTConstant.SEV_ALLOWANCE_NOT_PREFIX) + "_" + StringUtils.unAccent(entity.getEmployeeName()), hm, ExportFileExtension.PDF, resultMap, hasSevAllowance ? "SeveranceAllowance" : "NoSeveranceAllowance");
            file = new File(resultPath);
            String contentType = Files.probeContentType(file.toPath());
            String fileName = folderExtension.getFileName(resultPath);
            entity.setSevAllowancePath(fileName);
            terminateContractRepository.save(entity);
            byte[] data = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = file.getName();
            fileDTO.filePath = fileName;
            fileDTO.size = file.length();
            fileDTO.type = contentType;
            fileDTO.data = new String(data, StandardCharsets.US_ASCII);
            return fileDTO;
        } catch (Exception ex) {
            throw new BadRequestException("Kh??ng th??? t???o ???????c file");
        }
    }

    @Override
    public TerminateContractResponse confrimQuit(TerminateContractRequest request) {
        if (request == null || request.terminateContractId == null) {
            throw new NotFoundException("Kh??ng t??m th???y ch???m d???t h???p ?????ng");
        }
        if (!Objects.equals(request.status, TerminateStatusConstant.managerApproval) && !Objects.equals(request.status, TerminateStatusConstant.managerReject)) {
            throw new BadRequestException("B???n ch??a x??c nh???n cho ph??p nh??n vi??n ngh??? vi???c");
        }

        TerminateContractEntity entity = terminateContractRepository.findById(request.terminateContractId).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        if (sSoResponse.getEmployeeId() != null) {
            Optional<EmployeeVhrEntity> empTemp = employeeVhrRepository.findById(sSoResponse.getEmployeeId());
            EmployeeVhrEntity employeeVhrEntity = empTemp.orElse(null);
            if (employeeVhrEntity == null) {
                throw new NotFoundException("Kh??ng c?? th??ng tin nh??n vi??n tr??n h??? th???ng SSO");
            }
            if (!employeeVhrEntity.getEmployeeId().equals(entity.getManagerId())) {
                throw new BadRequestException("B???n kh??ng ph???i qu???n l?? c???a nh??n vi??n " + entity.getEmployeeName());
            }
        }
        entity.setStatus(request.status);
        entity.setNote(request.note);
        entity.setConfirmDate(LocalDate.now());
        entity = terminateContractRepository.save(entity);
        if (request.status.equals(TerminateStatusConstant.managerApproval)) {
            this.sendMessageRequestTerminate(entity.getTerminateContractId(), SmsTerminateContractStatus.SEND_REQUEST);
        }
        return objectMapper.convertValue(entity, TerminateContractResponse.class);
    }

    /**
     * Cap nhat file trinh ky voffice
     *
     * @param filePath duong dan file
     * @param id       ma doi tuong
     * @param type     kieu doi tuong
     */
    @Override
    public void updateFile(String filePath, Long id, Integer type) {
        TerminateContractEntity entity = terminateContractRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("Kh??ng t??m th???y ch???m d???t h???p ?????ng")));

        if (type == VOfficeSignConstant.Terminate) {
            entity.setFilePath(filePath);
        } else if (type == VOfficeSignConstant.SevAllowance) {
            entity.setSevAllowancePath(filePath);
        }
        terminateContractRepository.save(entity);
    }

    @Override
    public FileDTO.FileResponse exportExcel(SearchDTO searchRequest) {

        Pageable pageable = SearchUtils.getPageable(searchRequest);

        Page<TerminateContractEntity> list;
        // <editor-fold desc="tim kiem theo nguoi quan ly truc tiep">
        if (searchRequest != null && searchRequest.criteriaList.stream().anyMatch(x -> x.getField().equalsIgnoreCase("managerId"))) {
            searchRequest.criteriaList = searchRequest.criteriaList.stream().filter(x -> !x.getField().equalsIgnoreCase("managerId")).collect(Collectors.toList());
            SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
            SearchDTO.SearchCriteria manager = new SearchDTO.SearchCriteria();
            manager.setField("managerId");
            manager.setOperation(Operation.EQUAL);
            manager.setAndFlag(true);
            manager.setValue(sSoResponse.getEmployeeId());
            manager.setType(SearchType.NUMBER);
            searchRequest.criteriaList.add(manager);
        }
        // </editor-fold>
        Specification<TerminateContractEntity> specs = SearchUtils.getSpecifications(searchRequest);
        if (searchRequest.pagedFlag) {
            list = terminateContractRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = terminateContractRepository.findAll(p);
        }
        try {
            Resource resource = new ClassPathResource("template/TerminateContract.xlsx");
            InputStream inp = resource.getInputStream();
            Workbook workbook = new XSSFWorkbook(inp);
            Sheet sheet = workbook.getSheetAt(0);
            CellStyle borderCellStyle = workbook.createCellStyle();
            borderCellStyle.setBorderTop(BorderStyle.MEDIUM);
            borderCellStyle.setBorderBottom(BorderStyle.MEDIUM);
            borderCellStyle.setBorderLeft(BorderStyle.MEDIUM);
            borderCellStyle.setBorderRight(BorderStyle.MEDIUM);
            Row tempContentRow = sheet.getRow(3);
            Cell tempContentCell = tempContentRow.getCell(0);
            int size = list.getContent().size();
            Font font = sheet.getWorkbook().createFont();
            font.setFontName("Times New Roman");
            font.setFontHeightInPoints((short) 12);
            CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
            cellStyle.setFont(font);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setWrapText(true);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (int i = 0; i < size; i++) {
                TerminateContractEntity entity = list.getContent().get(i);
                Row rows = sheet.createRow(i + 3);
                Cell cell = rows.createCell(0);
                cell.setCellValue(i + 1);
                cell.setCellStyle(cellStyle);
                // Ma nv
                cell = rows.createCell(1);
                cell.setCellValue(entity.getEmployeeCode());
                cell.setCellStyle(cellStyle);
                // Ho ten
                cell = rows.createCell(2);
                cell.setCellValue(entity.getEmployeeName());
                cell.setCellStyle(cellStyle);
                // Chuc danh
                cell = rows.createCell(3);
                cell.setCellValue(entity.getPositionName());
                cell.setCellStyle(cellStyle);
                // phong ban
                cell = rows.createCell(4);
                cell.setCellValue(entity.getDepartmentName());
                cell.setCellStyle(cellStyle);
                // Don vi
                cell = rows.createCell(5);
                cell.setCellValue(entity.getRealUnitName());
                cell.setCellStyle(cellStyle);
                // noi dao tao
                cell = rows.createCell(6);
                cell.setCellValue(entity.getTypeObject() != null
                        ? (entity.getTypeObject() ? "Qu??n nh??n chuy??n nghi???p" : "H???p ?????ng lao ?????ng")
                        : "");
                cell.setCellStyle(cellStyle);
                // Ngay sinh
                cell = rows.createCell(7);
                cell.setCellValue(entity.getBirthDate() != null ? entity.getBirthDate().format(formatter) : "");
                cell.setCellStyle(cellStyle);
                // ly do
                cell = rows.createCell(8);
                cell.setCellValue(entity.getReason());
                cell.setCellStyle(cellStyle);
                // Ngay cham dut hop dong
                cell = rows.createCell(14);
                cell.setCellValue("Quy???t ?????nh c?? hi???u ban h??nh");
                cell.setCellStyle(cellStyle);
                // so quyet dinh
                cell = rows.createCell(15);
                cell.setCellValue(entity.getDocumentCode());
                cell.setCellStyle(cellStyle);
                // ngay ra quyet dinh nghi viec
                cell = rows.createCell(16);
                cell.setCellValue(entity.getQuitDate() != null ? entity.getQuitDate().format(formatter) : "");
                cell.setCellStyle(cellStyle);
                // Trinh do dao tao
                cell = rows.createCell(17);
                cell.setCellValue(entity.getTrainingLevel());
                cell.setCellStyle(cellStyle);
                // Chuyen nganh dao tao
                cell = rows.createCell(18);
                cell.setCellValue(entity.getTrainingSpeciality());
                cell.setCellStyle(cellStyle);
                // Cong no
                cell = rows.createCell(19);
                cell.setCellValue("Kh??ng");
                cell.setCellStyle(cellStyle);
                // Ngay nhan ho so
                cell = rows.createCell(20);
                cell.setCellValue(
                        entity.getAgrApprovalDate() != null ? entity.getAgrApprovalDate().format(formatter) : "");
                cell.setCellStyle(cellStyle);

                cell = rows.createCell(21);
                cell.setCellValue("");
                cell.setCellStyle(cellStyle);
                cell = rows.createCell(22);
                cell.setCellValue("");
                cell.setCellStyle(cellStyle);
                cell = rows.createCell(23);
                cell.setCellValue("");
                cell.setCellStyle(cellStyle);
                for (int j = 9; j < 14; j++) {
                    cell = rows.createCell(j);
                    cell.setCellValue("");
                    cell.setCellStyle(cellStyle);
                }
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);
            byteArrayOutputStream.close();
            workbook.close();
            byte[] data = Base64.getEncoder().encode(byteArrayOutputStream.toByteArray());
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = "DanhSachNghiViec.xlsx";
            fileDTO.size = (long) data.length;
            fileDTO.type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            fileDTO.data = new String(data, StandardCharsets.US_ASCII);

            return fileDTO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Danh sach cho nghi viec
     *
     * @param searchRequest
     * @return
     */
    @Override
    public FileDTO.FileResponse exportIdleTime(SearchDTO searchRequest) {

        Pageable pageable = SearchUtils.getPageable(searchRequest);

        Page<TerminateContractEntity> list;
        // <editor-fold desc="tim kiem theo nguoi quan ly truc tiep">
        if (searchRequest != null
                && searchRequest.criteriaList.stream().anyMatch(x -> x.getField().equalsIgnoreCase("managerId"))) {
            searchRequest.criteriaList = searchRequest.criteriaList.stream()
                    .filter(x -> !x.getField().equalsIgnoreCase("managerId")).collect(Collectors.toList());
            SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
            SearchDTO.SearchCriteria manager = new SearchDTO.SearchCriteria();
            manager.setField("managerId");
            manager.setOperation(Operation.EQUAL);
            manager.setAndFlag(true);
            manager.setValue(sSoResponse.getEmployeeId());
            manager.setType(SearchType.NUMBER);
            searchRequest.criteriaList.add(manager);
        }
        // </editor-fold>
        Specification<TerminateContractEntity> specs = SearchUtils.getSpecifications(searchRequest);
        if (searchRequest.pagedFlag) {
            list = terminateContractRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = terminateContractRepository.findAll(p);
        }
        try {
            Resource resource = new ClassPathResource("template/DanhSachChoNghi.xlsx");
            InputStream inp = resource.getInputStream();
            Workbook workbook = new XSSFWorkbook(inp);
            Sheet sheet = workbook.getSheetAt(0);
            CellStyle borderCellStyle = workbook.createCellStyle();
            borderCellStyle.setBorderTop(BorderStyle.MEDIUM);
            borderCellStyle.setBorderBottom(BorderStyle.MEDIUM);
            borderCellStyle.setBorderLeft(BorderStyle.MEDIUM);
            borderCellStyle.setBorderRight(BorderStyle.MEDIUM);
            int size = list.getContent().size();
            Font font = sheet.getWorkbook().createFont();
            font.setFontName("Times New Roman");
            font.setFontHeightInPoints((short) 12);
            CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
            cellStyle.setFont(font);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setWrapText(true);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/yyyy");
            for (int i = 0; i < size; i++) {
                TerminateContractEntity entity = list.getContent().get(i);
                Row rows = sheet.createRow(i + 3);
                Cell cell = rows.createCell(0);
                cell.setCellValue(i + 1);
                cell.setCellStyle(cellStyle);
                // Ma nv
                cell = rows.createCell(1);
                cell.setCellValue(entity.getEmployeeCode());
                cell.setCellStyle(cellStyle);
                // Ho ten
                cell = rows.createCell(2);
                cell.setCellValue(entity.getEmployeeName());
                cell.setCellStyle(cellStyle);
                // Nam sinh
                cell = rows.createCell(3);
                cell.setCellValue(entity.getBirthDate() != null ? String.valueOf(entity.getBirthDate().getYear()) : "");
                cell.setCellStyle(cellStyle);
                // Don vi
                cell = rows.createCell(4);
                cell.setCellValue(entity.getRealUnitName());
                cell.setCellStyle(cellStyle);
                // phong ban
                cell = rows.createCell(5);
                cell.setCellValue(entity.getDepartmentName());
                cell.setCellStyle(cellStyle);
                // Cum/doi
                cell = rows.createCell(6);
                cell.setCellValue("");
                cell.setCellStyle(cellStyle);
                // Chuc danh hien tai
                cell = rows.createCell(7);
                cell.setCellValue(entity.getPositionName());
                cell.setCellStyle(cellStyle);
                // doi tuong
                cell = rows.createCell(8);
                cell.setCellValue(entity.getTypeObject() != null
                        ? (true == entity.getTypeObject() ? "Qu??n nh??n chuy??n nghi???p" : "H???p ?????ng lao ?????ng")
                        : "");
                cell.setCellStyle(cellStyle);
                // Trinh do dao tao
                cell = rows.createCell(9);
                cell.setCellValue(entity.getTrainingLevel());
                cell.setCellStyle(cellStyle);
                // Chuyen nganh dao tao
                cell = rows.createCell(10);
                cell.setCellValue(entity.getTrainingSpeciality());
                cell.setCellStyle(cellStyle);
                // Ly do xin nghi
                cell = rows.createCell(11);
                cell.setCellValue(entity.getReason());
                cell.setCellStyle(cellStyle);
                // Ngay vao tct
                cell = rows.createCell(12);
                cell.setCellValue(entity.getStartDate() != null ? entity.getStartDate().format(formatter) : "");
                cell.setCellStyle(cellStyle);
                // Ngay cho nghi
                cell = rows.createCell(13);
                cell.setCellValue(entity.getConfirmDate() != null ? entity.getConfirmDate().format(formatter) : "");
                cell.setCellStyle(cellStyle);
                // Thang nghi
                cell = rows.createCell(16);
                cell.setCellValue(
                        entity.getConfirmDate() != null ? entity.getConfirmDate().format(monthFormatter) : "");
                cell.setCellStyle(cellStyle);

                cell = rows.createCell(14);
                cell.setCellValue("");
                cell.setCellStyle(cellStyle);
                cell = rows.createCell(15);
                cell.setCellValue("");
                cell.setCellStyle(cellStyle);
                cell = rows.createCell(17);
                cell.setCellValue("");
                cell.setCellStyle(cellStyle);

            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);
            byteArrayOutputStream.close();
            workbook.close();
            byte[] data = Base64.getEncoder().encode(byteArrayOutputStream.toByteArray());
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = "DanhSachChoNghiViec.xlsx";
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
    public TerminateContractResponse sendResignation(TerminateContractRequest request) {
        if (request == null || request.terminateContractId == null) {
            throw new NotFoundException("Kh??ng t??m th???y b???n ghi ch???m d???t h???p ?????ng");
        }

        TerminateContractEntity entity = terminateContractRepository.findById(request.terminateContractId).orElseThrow(() -> new NotFoundException("Kh??ng t??m th???y b???n ghi ch???m d???t h???p ?????ng"));
        if (!Objects.equals(entity.getStatus(), TerminateStatusConstant.draft)) {
            throw new NotFoundException("Y??u c???u ch???m d???t ???? ???????c ph?? duy???t");
        }
        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        if (sSoResponse.getEmployeeId() != null) {
            Optional<EmployeeVhrEntity> empTemp = employeeVhrRepository.findById(sSoResponse.getEmployeeId());
            EmployeeVhrEntity employeeVhrEntity = empTemp.orElse(null);
            if (employeeVhrEntity == null) {
                throw new AccessDeniedException("B???n kh??ng ph???i l?? nh??n vi??n tr??n h??? th???ng SSO");
            }
            if (!employeeVhrEntity.getEmployeeId().equals(entity.getEmployeeId())) {
                throw new BadRequestException("B???n kh??ng c?? quy???n g???i x??c nh???n xin ngh??? vi???c");
            }
        }

        EmployeeVhrEntity managerEntity = employeeVhrRepository.findById(entity.getManagerId()).orElse(null);

        if (managerEntity == null) {
            throw new NotFoundException("Kh??ng t??m th???y ng?????i qu???n l??");
        }

        String mobileNumber = managerEntity.getMobileNumber();
        if (StringUtils.isBlank(mobileNumber)) {
            throw new NotFoundException("Ng?????i qu???n l?? kh??ng c?? s??? ??i???n tho???i tr??n VPS");
        }

        smsMessageService.sendSmsMessage(mobileNumber, "Nhan vien " + StringUtils.unAccent(request.employeeName) + " voi ma NV " + request.employeeCode + " dang muon xin nghi viec. Xin moi vao phan mem Ho so dien tu de kiem tra");

        entity.setStatus(TerminateStatusConstant.pending);
        entity = terminateContractRepository.save(entity);

        return objectMapper.convertValue(entity, TerminateContractResponse.class);
    }

    @Override
    public void updateInsuranceStatus(Set<Long> terminateIds, InsuranceStatus insuranceStatus) {
        terminateContractRepository.updateInsuranceStatus(terminateIds, insuranceStatus);
    }

    @Override
    public TerminateContractResponse findByTransCodeSevAllowance(String transCode) {
        TerminateContractEntity entity = terminateContractRepository.findByTransCodeSevAllowance(transCode)
                .orElse(null);
        if (entity == null) {
            return null;
        }
        return objectMapper.convertValue(entity, TerminateContractResponse.class);
    }

    @Override
    public FileDTO.FileResponse mergeTerminateContract(List<Long> ids) {
        List<TerminateContractEntity> terminateContractEntities = terminateContractRepository.findByTerminateContractIdInAndHasSevAllowance(ids, TerminateStatusConstant.vofficeApproval);
        if (terminateContractEntities.isEmpty() || terminateContractEntities.size() != ids.size()) {
            throw new NotFoundException("Trong danh s??ch l???a ch???n c?? ??t nh???t 1 b???n ghi ch???m d???t h???p ?????ng ch??a c?? file quy???t ?????nh ch???m d???t h???p ?????ng ho???c tr???ng th??i ???? duy???t xin ngh??? vi???c");
        }

        List<File> files = new ArrayList<>();
        String newFilePathReal = fileService.createFilePathReal(HSDTConstant.SEV_ALLOWANCE_MERGE + ".docx");
        for (TerminateContractEntity terminateContract : terminateContractEntities) {
            String sevAllowanceFilePath = terminateContract.getSevAllowancePath();
            if (StringUtils.isBlank(sevAllowanceFilePath)) {
                throw new NotFoundException("Trong danh s??ch l???a ch???n c?? ??t nh???t 1 b???n ghi ch???m d???t h???p ?????ng ch??a c?? file quy???t ?????nh ch???m d???t h???p ?????ng");
            }
            String sevAllowanceFilePathDocx = sevAllowanceFilePath.replaceAll(".pdf", ".docx");

            try {
                Resource resource = fileService.downloadFileWithEncodePath(sevAllowanceFilePathDocx);
                files.add(resource.getFile());
            } catch (IOException e) {
                log.error("Kh??ng t??m th???y file " + sevAllowanceFilePathDocx);
                throw new NotFoundException("Kh??ng t??m th???y file " + sevAllowanceFilePathDocx);
            }

        }

        FileDTO.FileResponse fileResponse = new FileDTO.FileResponse();
        String pathStore = folderExtension.getUploadFolder();
        HashMap<String, String> hm = new HashMap<>();
        try {
            File mergeFileWord = FileUtils.mergeFileWord2(files, newFilePathReal);
            ExportWord<InterviewResultDTO> exportWord = new ExportWord<>();
            String pdfFilePathReal = exportWord.export(mergeFileWord.getPath(), pathStore, HSDTConstant.SEV_ALLOWANCE_MERGE, hm, ExportFileExtension.PDF, null, "SevAllowanceMulti");
            File mergeFilePdf = new File(pdfFilePathReal);

            fileResponse.fileName = fileService.getFileNameFromDecodePath(pdfFilePathReal);
            fileResponse.filePath = fileService.encodePath(pdfFilePathReal);
            fileResponse.encodePath = fileService.encodePath(pdfFilePathReal);
            fileResponse.size = mergeFilePdf.length();
            fileResponse.type = Files.probeContentType(mergeFilePdf.toPath());
            fileResponse.downloadFileExtension = pdfFilePathReal.replaceAll(".pdf", ".docx");
            return fileResponse;

        } catch (IOException e) {
            throw new NotFoundException("Kh??ng th??? t???o ???????c file ");
        }
    }

    @Override
    public FileDTO.FileResponse mergeTerminateContractOnlyPdf(List<Long> ids) {
        List<TerminateContractEntity> terminateContractEntities = terminateContractRepository.findByTerminateContractIdInAndHasSevAllowance(ids, TerminateStatusConstant.vofficeApproval);
        if (terminateContractEntities.isEmpty() || terminateContractEntities.size() != ids.size()) {
            throw new NotFoundException("Trong danh s??ch l???a ch???n c?? ??t nh???t 1 b???n ghi ch???m d???t h???p ?????ng ch??a c?? file quy???t ?????nh ch???m d???t h???p ?????ng ho???c tr???ng th??i ???? duy???t xin ngh??? vi???c");
        }

        List<File> files = new ArrayList<>();
        String newFilePathReal = fileService.createFilePathReal(HSDTConstant.SEV_ALLOWANCE_MERGE + ".pdf");
        for (TerminateContractEntity terminateContract : terminateContractEntities) {
            String sevAllowanceFilePath = terminateContract.getSevAllowancePath();
            if (StringUtils.isBlank(sevAllowanceFilePath)) {
                throw new NotFoundException("Trong danh s??ch l???a ch???n c?? ??t nh???t 1 b???n ghi ch???m d???t h???p ?????ng ch??a c?? file quy???t ?????nh ch???m d???t h???p ?????ng");
            }
            try {
                Resource resource = fileService.downloadFileWithEncodePath(sevAllowanceFilePath);
                files.add(resource.getFile());
            } catch (IOException e) {
                log.error("Kh??ng t??m th???y file " + sevAllowanceFilePath);
                throw new NotFoundException("Kh??ng t??m th???y file " + sevAllowanceFilePath);
            }
        }

        try {
            File file = PdfAnnotations.mergePDFs(files, newFilePathReal);

            FileDTO.FileResponse fileResponse = new FileDTO.FileResponse();
            fileResponse.fileName = fileService.getFileNameFromDecodePath(newFilePathReal);
            fileResponse.filePath = fileService.encodePath(newFilePathReal);
            fileResponse.encodePath = fileService.encodePath(newFilePathReal);
            fileResponse.size = file.length();
            fileResponse.type = Files.probeContentType(file.toPath());
            fileResponse.downloadFileExtension = null;
            return fileResponse;

        } catch (IOException e) {
            throw new NotFoundException("Kh??ng t???o ???????c file gh??p");
        } catch (DocumentException e) {
            throw new NotFoundException("Kh??ng ?????c ???????c m???t s??? file c???n gh??p");
        }
    }

    @Override
    public List<TerminateContractEntity> findBySevAllowanceMulti(String transCode) {
        if (transCode == null) return null;
        return terminateContractRepository.findByTransCodeMulti(transCode);
    }

    @Override
    public List<TerminateContractEntity> saveSevAllowanceMulti(boolean isSuccess, List<TerminateContractEntity> terminateContractEntities,
                                                               String filePath) {
        for (TerminateContractEntity terminateContract : terminateContractEntities) {
            if (isSuccess) {
                terminateContract.setSignedSevAllowanceMultiPath(filePath);
                terminateContract.setStatus(TerminateStatusConstant.sevApproval);
            } else {
                terminateContract.setStatus(TerminateStatusConstant.sevReject);
            }
        }
        return terminateContractRepository.saveAll(terminateContractEntities);
    }

    /**
     * Xuat file don xin nghi, thoa thuan cham dut hop dong, xac nhan cong no
     *
     * @param entity
     */
    private void initFile(TerminateContractEntity entity) {
        // <editor-fold desc="Xuat file">
        ExportWord<InterviewResultDTO> exportWord = new ExportWord<>();
        Map<String, String> map = CustomMapper.convert(entity);
        if (map.get("expiredDate_ddmmyyyy") != null) {
            map.put("expiredDate_ddmmyyyy", "?????n " + map.get("expiredDate_ddmmyyyy"));
        }
        map = addNowToHashMap(map);
        HashMap<String, String> hm = new HashMap<>();
        // <editor-fold desc="Them thong tin nguoi ky hop dong">
        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        if (sSoResponse.getOrganizationId() != null && sSoResponse.getOrganizationId() > 0) {
            Long unitId = entity.getRealUnitId();
            EmployerInfoEntity employer = employerInfoRepository.findByUnitId(unitId);
            if (employer != null) {
                Map<String, String> employerMap = CustomMapper.convert(employer);
                map.putAll(employerMap);
            }
        }
        // </editor-fold>
        for (String key : map.keySet()) {
            if (map.get(key) != null) {
                hm.put(key, map.get(key));
            }
        }
        switch (entity.getContractType()) {
            case CREATED_CONTRACT:
                hm.put("contractType", "H???p ?????ng nh??n vi??n m???i");
                break;
            case COLLABORATOR:
                hm.put("contractType", "Th???a thu???n h???p t??c");
                break;
            case FREELANCE_CONTRACT:
                hm.put("contractType", "H???p ?????ng giao kho??n");
                break;
            case LABOR_CONTRACT:
                hm.put("contractType", "H???p ?????ng lao ?????ng");
                break;
            case PROBATIONARY_CONTRACT:
                hm.put("contractType", "H???p ?????ng th??? vi???c");
                break;
            case SERVICE_CONTRACT:
                hm.put("contractType", "H???p ?????ng d???ch v???");
                break;
        }
        String pathStore = folderExtension.getUploadFolder();
        File file = null;
        try {
            // start file don xin nghi viec
            file = new ClassPathResource(
                    "template/" + (entity.getDebtType() != null && entity.getDebtType() == 1 ? "DonXinChamDutHD_TCT"
                            : "DonXinChamDutHD_DV") + ".docx")
                    .getFile();
            String path = file.getPath();
            HashMap<String, List<InterviewResultDTO>> resultMap = new HashMap<>();
            String resultPath = exportWord.exportV2(path, pathStore,
                    HSDTConstant.RESIGNATION_LETTER_PREFIX + "_" + StringUtils.unAccent(entity.getEmployeeName()), hm,
                    ExportFileExtension.PDF, resultMap,
                    entity.getDebtType() != null && entity.getDebtType() == 1 ? "ResignationLetter"
                            : "ResignationLetter1");
            file = new File(resultPath);
            if (entity.getSignaturePath() != null && entity.getSignaturePath().trim().length() > 0) {
                File imgPath = new File(
                        folderExtension.getUploadFolder() + "/" + entity.getSignaturePath().replace("-", "/"));
                try {
                    PdfAnnotations.addImagePdf(file.getAbsolutePath(), imgPath.getAbsolutePath(), "NG?????I L??M ????N",
                            "Agreement", null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String fileName = folderExtension.getFileName(resultPath);
            entity.setFilePath(fileName);
            entity.setFileName(fileName);
            // start file cham dut hop dong
            file = new ClassPathResource("template/ThoaThuanChamDutHD.docx").getFile();
            path = file.getPath();
            resultPath = exportWord.export(path, pathStore,
                    HSDTConstant.AGREEMENT_PREFIX + "_" + StringUtils.unAccent(entity.getEmployeeName()), hm,
                    ExportFileExtension.PDF, resultMap, "Agreement");
            file = new File(resultPath);
            if (entity.getSignaturePath() != null && entity.getSignaturePath().trim().length() > 0) {
                File imgPath = new File(
                        folderExtension.getUploadFolder() + "/" + entity.getSignaturePath().replace("-", "/"));
                try {
                    PdfAnnotations.addImagePdf(file.getAbsolutePath(), imgPath.getAbsolutePath(), "NG?????I LAO ?????NG",
                            "Agreement", null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            fileName = folderExtension.getFileName(resultPath);
            entity.setAgrFileName(fileName);

            // Start xac nhan cong no
            // boolean isForUnit = true;
            // if (entity.getDebtType() != null && entity.getDebtType() == 1) {
            // isForUnit = false;
            // entity.setDebtType(entity.getDebtType());
            // } else {
            // entity.setDebtType(0);
            // }
            file = new ClassPathResource("template/XacNhanCongNo.docx").getFile();
            path = file.getPath();
            resultPath = exportWord.exportV2(path, pathStore,
                    HSDTConstant.DEBT_PREFIX + "_" + StringUtils.unAccent(entity.getEmployeeName()), hm,
                    ExportFileExtension.PDF, resultMap, "debtOfCompany");
            file = new File(resultPath);
            if (entity.getSignaturePath() != null && entity.getSignaturePath().trim().length() > 0) {
                File imgPath = new File(
                        folderExtension.getUploadFolder() + "/" + entity.getSignaturePath().replace("-", "/"));
                try {
                    PdfAnnotations.addImagePdf(file.getAbsolutePath(), imgPath.getAbsolutePath(), "X??C NH???N C???A CBNV",
                            "Agreement", null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            fileName = folderExtension.getFileName(resultPath);
            entity.setDebtFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // </editor-fold>
    }

    private void sendMessageRequestTerminate(Long id, SmsTerminateContractStatus status) {
        TerminateContractEntity entity = terminateContractRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kh??ng t??m th???y b???n ghi "));
        String message = null;
        switch (status) {
            case SIGNED_REQUEST:
                message = "TCT CP Cong trinh Viettel thong bao: Quyet dinh cham dut HDLD cua d/c "
                        + StringUtils.unAccent(entity.getEmployeeName()) + " - "
                        + StringUtils.unAccent(entity.getOrganizationName()) + " da duoc TGD phe duyet. Tran trong !";
                break;

            case REQUEST_BHXH:
                message = "TCT CP Cong trinh Viettel thong bao: De nghi d/c "
                        + StringUtils.unAccent(entity.getEmployeeName()) + " - "
                        + StringUtils.unAccent(entity.getOrganizationName())
                        + " hoan thien ho so cap so/so BHXH de don vi chot so nghi viec. Tran trong !";
                break;

            default:
                message = StringUtils.unAccent(entity.getOrganizationName())
                        + " thong bao: Ho so cham dut HDLD cua d/c da duoc chuyen den phong To chuc Lao dong TCT giai quyet. Tran trong!";
                break;
        }
        smsMessageService.sendSmsMessage(entity.getMobileNumber(), message);
    }

    private Map<String, String> addNowToHashMap(Map<String, String> map) {
        LocalDate now = LocalDate.now();
        map.put("now_dd", String.valueOf(now.getDayOfMonth()));
        map.put("now_mm", String.valueOf(now.getMonthValue()));
        map.put("now_yyyy", String.valueOf(now.getYear()));
        return map;
    }

}
