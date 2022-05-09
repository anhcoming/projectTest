package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.hstd.constant.FileTemplateConstant;
import com.viettel.hstd.constant.HSDTConstant;
import com.viettel.hstd.constant.NewContractStatus;
import com.viettel.hstd.constant.VPSConstant;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.constant.ExportFileExtension;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.*;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.CollaboratorContractDTO;
import com.viettel.hstd.dto.hstd.ContractDTO.*;
import com.viettel.hstd.dto.hstd.EmployerInfoDTO;
import com.viettel.hstd.dto.hstd.ProbationaryContractDTO;
import com.viettel.hstd.dto.hstd.ProbationaryContractDTO.*;
import com.viettel.hstd.entity.hstd.EmployerInfoEntity;
import com.viettel.hstd.entity.hstd.InterviewSessionCvEntity;
import com.viettel.hstd.entity.hstd.InterviewSessionEntity;
import com.viettel.hstd.entity.hstd.ProbationaryContractEntity;
import com.viettel.hstd.entity.vps.EmployeeVhrEntity;
import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.filter.HSTDFilter;
import com.viettel.hstd.repository.hstd.*;
import com.viettel.hstd.repository.vps.EmployeeVhrRepository;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.*;
import com.viettel.hstd.util.FileUtils;
import com.viettel.hstd.util.FolderExtension;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProbationaryContractServiceImp extends BaseService implements ProbationaryContractService {

    @Autowired
    private ProbationaryContractRepository probationaryContractRepository;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    Message message;
    @Autowired
    MapUtils mapUtils;

    @Autowired
    EmployerInfoRepository employerInfoRepository;

    @Autowired
    FolderExtension folderExtension;

    @Autowired
    FileService fileService;

    @Autowired
    VhrFutureOrganizationRepository organizationRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private SmsMessageService smsMessageService;
    @Autowired
    private VhrFutureOrganizationService vhrFutureOrganizationService;
    @Autowired
    private EmployerInforService employerInfoService;
    @Autowired
    private InterviewSessionCvRepository interviewSessionCvRepository;
    @Autowired
    private InterviewSessionRepository interviewSessionRepository;

    @Autowired
    private HSTDFilter hstdFilter;
    @Autowired
    private ContractService contractService;

    @Autowired
    private EmployeeVhrRepository employeeVhrRepository;

    @Autowired
    private VhrFutureOrganizationRepository vhrFutureOrganizationRepository;

    @Override
    public Page<ProbationaryContractResponse> findPage(SearchDTO searchRequest) {
        hstdFilter.unitDepartmentFilter(searchRequest, "unitId");
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<ProbationaryContractEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<ProbationaryContractEntity> list;
        if (searchRequest.pagedFlag) {
            list = probationaryContractRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = probationaryContractRepository.findAll(p);
        }

        return list.map(this::convertEntityToResponse);

    }

    @Override
    public ProbationaryContractResponse findOneById(Long id) {
        ProbationaryContractEntity entity = probationaryContractRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(entity, ProbationaryContractResponse.class);
    }

    @Override
    public Boolean delete(Long id) {
        ProbationaryContractEntity probationaryContractEntity = probationaryContractRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hợp đồng không tìm thấy trên hệ thống"));
        if (probationaryContractEntity.getTransCode() != null) {
            throw new BadRequestException("Hợp đồng đang trong trạng thái trình ký không được xóa");
        }
        probationaryContractRepository.softDelete(id);
        super.addLog("PROBATIONARY_CONTRACT", "DELETE", id.toString());
        return true;
    }

    @Override
    public ProbationaryContractResponse create(ProbationaryContractRequest request) {
        ProbationaryContractEntity entity = objectMapper.convertValue(request, ProbationaryContractEntity.class);
        entity = probationaryContractRepository.save(entity);
        super.addLog("PROBATIONARY_CONTRACT", "CREATE", new Gson().toJson(request));
        return objectMapper.convertValue(entity, ProbationaryContractResponse.class);
    }

    @Override
    public ProbationaryContractResponse update(Long id, ProbationaryContractRequest request) {
        ProbationaryContractEntity entity = probationaryContractRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        String employeeCode = request.employeeCode;
        if (!employeeCode.equals(entity.getEmployeeCode())) {
            EmployeeVhrEntity employeeVhrEntity = employeeVhrRepository.
                    findByEmployeeCode(employeeCode).orElseThrow(() -> new NotFoundException("Mã nhân viên không tìm thấy trên trên hệ thống"));
            ProbationaryContractEntity newE = objectMapper.convertValue(request, ProbationaryContractEntity.class);
            mapUtils.customMap(newE, entity);
            entity.setEmployeeId(employeeVhrEntity.getEmployeeId());
            entity.setEmployeeCode(employeeCode);
        } else {
            ProbationaryContractEntity newE = objectMapper.convertValue(request, ProbationaryContractEntity.class);
            mapUtils.customMap(newE, entity);
        }
        entity.setContractId(id);
        entity = probationaryContractRepository.save(entity);
        super.addLog("PROBATIONARY_CONTRACT", "UPDATE", new Gson().toJson(request));
        return objectMapper.convertValue(entity, ProbationaryContractResponse.class);
    }

    private ProbationaryContractResponse convertEntityToResponse(ProbationaryContractEntity entity) {
        ProbationaryContractResponse response = objectMapper.convertValue(entity, ProbationaryContractResponse.class);

        VhrFutureOrganizationEntity departmentEntity = organizationRepository
                .findById(entity.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy phòng ban"));

        EmployeeVhrEntity employeeVhrEntity = employeeVhrRepository.findByEmployeeCode(entity.getEmployeeCode()).orElse(null);
        if (employeeVhrEntity != null) {
            response.currentAddress = employeeVhrEntity.getCurrentAddress();
        }
        response.unitName = departmentEntity.getOrgNameLevel2();
        response.departmentName = departmentEntity.getOrgNameLevel3();
        response.timeLeft = TimeUtil.intervalBetweenLocalDates(LocalDate.now(), entity.getExpiredDate());
        response.contractDurationVietnamese = entity.getContractDuration().getVietnameseStringValue();
        return response;
    }

    @Override
    @Transactional
    public FileDTO.FileResponse addEmployeeSignatureToContract(ContractAddEmployeeSignature request) {
        if (request == null) {
            throw new NotFoundException("Yêu cầu không chính xác");
        }
        ProbationaryContractEntity entity = probationaryContractRepository
                .findById(request.contractId)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        //<editor-fold desc="Them thong tin nguoi ky hop dong">
        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();

        //</editor-fold>

        try {
            String pathStore = folderExtension.getUploadFolder();

            File contractFile = new File(fileService.decodePath(entity.getContractFileEncodePath()));
            if (request.signatureFileEncodePath != null) {
                entity.setSignatureName(request.signatureFileName);
                entity.setSignatureFileEncodePath(request.signatureFileEncodePath);
            }
//            probationaryContractRepository.save(entity);
            //Them chu ky trong file
            if (entity.getSignatureFileEncodePath() != null && entity.getSignatureFileEncodePath().trim().length() > 0) {
                File imgPath = new File(fileService.decodePath(request.signatureFileEncodePath));
                try {
                    LocalDateTime now = LocalDateTime.now();
                    entity.setSignedDate(now.toLocalDate());
                    PdfAnnotations.addImagePdf(contractFile.getPath(), imgPath.getPath(), "(Người lao động)", HSDTConstant.PROBATIONARY_CONTRACT_DOCUMENT_TYPE, now);
                    contractFile = new File(fileService.decodePath(entity.getContractFileEncodePath()));
                } catch (Exception e) {
                    String stackTrace = ExceptionUtils.getStackTrace(e);
                    log.error(stackTrace);
                    throw new BadRequestException("Không thể thêm chữ ký vào hợp đồng");
                }
            }

            String contentType = Files.probeContentType(contractFile.toPath());
            String fileName = folderExtension.getFileName(contractFile.getAbsolutePath());

            entity.setNewContractStatus(NewContractStatus.EMPLOYEE_SIGNED_CONTRACT);
            entity.setEmployeeSignedFile(entity.getContractFile());
            entity.setEmployeeSignedFileEncodePath(entity.getContractFileEncodePath());
            entity.setSignedFile(entity.getContractFile());
            entity.setSignedFileEncodePath(entity.getContractFileEncodePath());

            probationaryContractRepository.save(entity);
            byte[] data = Base64.getEncoder().encode(Files.readAllBytes(contractFile.toPath()));
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = contractFile.getName();
            fileDTO.filePath = fileName;
            fileDTO.size = contractFile.length();
            fileDTO.type = contentType;
            fileDTO.data = new String(data, StandardCharsets.US_ASCII);
            return fileDTO;
        } catch (Exception ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
            throw new BadRequestException("Không thể thêm chữ ký vào hợp đồng");
        }
    }

    private void updateContractFile(String fileName, String encodePath, Long id) {
        ProbationaryContractEntity entity = probationaryContractRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("Không tìm thấy hợp đồng")));
        entity.setContractFile(fileName);
        entity.setContractFileEncodePath(encodePath);
        entity.setNewContractStatus(NewContractStatus.HR_CREATED_WORD_FILE);
        entity = probationaryContractRepository.save(entity);
    }

    @Override
    public boolean updateNewContractStatus(ProbationaryContractDTO.ProbationaryNewStatusRequest request) {
        probationaryContractRepository.updateNewContractStatus(request.contractId, request.newContractStatus);
        return true;
    }

    @Override
    public FileDTO.FileResponse sendContractToEmployeeInNewContract(Long contractId) {
        // Tao file
        FileDTO.FileResponse fileResponse = new FileDTO.FileResponse();
        String phoneNumber = contractRepository.getPhoneNumberByContractId(contractId).orElse(null);
        if (phoneNumber == null) {
            throw new NotFoundException("Hợp đồng không có số điện thoại");
        }
        smsMessageService.sendSmsMessage(phoneNumber, "Ban co mot hop dong can ky dien tu. Vui long truy cap phan mem Ho so dien tu de ky hop dong.");

        // Thay doi trang thai
        probationaryContractRepository.updateNewContractStatus(contractId, NewContractStatus.HR_SENT_CONTRACT_EMPLOYEE);

        return fileResponse;
    }

    @Override
    @Transactional
    public FileDTO.FileResponse exportContract(ProbationaryExportContractRequest request) {
        try {
            ProbationaryContractResponse contract = findOneById(request.contractId);
            Map<String, String> map = CustomMapper.convert(contract);
            //<editor-fold desc="Them thong tin nguoi ky hop dong">
//            SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
//            Long unitId = 0L;
//            if (sSoResponse.getOrganizationId() != null && sSoResponse.getOrganizationId() > 0) {
//                unitId = sSoResponse.getOrganizationId();
//                if (unitId > 0) {
//                    EmployerInfoEntity employer = employerInfoRepository.findByUnitId(unitId);
//                    if (employer != null) {
//                        Map<String, String> employerMap = CustomMapper.convert(employer);
//                        map.putAll(employerMap);
//                    }
//                }
//            }
            if (contract.unitId == null)
                throw new NotFoundException("Không tìm thấy thông tin đơn vị của hợp đồng " + contract.employeeName);

            Long unitId = contract.unitId;

            // Nếu người nhập là KCQ TCT thì sẽ lấy đơn vị là TCT
            SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
            Long unitOfUser = sSoResponse.getUnitId();
            if (unitOfUser.equals(VPSConstant.ALL_PROVINCE_UNIT_ID)) {
                unitId = unitOfUser;
            }

            Boolean isBranch = false;
            if (unitId > 0) {
                EmployerInfoEntity employer = employerInfoRepository.findByUnitId(unitId);
                EmployerInfoDTO.EmployerInfoResponseForExport employerResponse = employerInfoService.convertEntityToExportResponse(employer);
                if (employer != null) {
                    Map<String, String> employerMap = CustomMapper.convert(employerResponse);
                    map.putAll(employerMap);
                    String managerSign = contractService.getManagerSign(unitId, employer.getUnitName());
                    map.put("managerSign", managerSign);
                    isBranch = isBranch(employer.getUnitName());
                }
            }
            //</editor-fold>
            HashMap<String, String> hm = new HashMap<>();
            for (String key : map.keySet()) {
                if (map.get(key) != null) {
                    hm.put(key, map.get(key).toString());
                }
            }
            hm = convertSalary(hm);
            String pathStore = folderExtension.getUploadFolder();
            File file = new ClassPathResource(isBranch ? FileTemplateConstant.PROBATIONARY_CONTRACT_BRANCH_DOCX : FileTemplateConstant.PROBATIONARY_CONTRACT_DOCX).getFile();
            String path = file.getPath();
            ExportWord<CollaboratorContractDTO> exportWord = new ExportWord<>();
            String realPath = exportWord.export(path, pathStore, "Hop dong thu viec " + FileUtils.stripAccents(contract.employeeName),
                    hm, ExportFileExtension.PDF, null, FileTemplateConstant.SIGNAL_PROBATION_ADD_NOTE);
            file = new File(realPath);
            String contentType = Files.probeContentType(file.toPath());
//            updateContractFile(file.getName(), fileService.encodePathFromRealPath(realPath), request.contractId);

            ProbationaryContractEntity entity = probationaryContractRepository
                    .findById(request.contractId)
                    .orElseThrow(() -> new NotFoundException(message.getMessage("Không tìm thấy hợp đồng")));
            entity.setContractFile(file.getName());
            entity.setContractFileEncodePath(fileService.encodePath(realPath));
            entity.setNewContractStatus(NewContractStatus.HR_CREATED_WORD_FILE);
            entity = probationaryContractRepository.save(entity);

            byte[] data = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = file.getName();
            fileDTO.filePath = realPath;
            fileDTO.encodePath = fileService.encodePath(realPath);
            fileDTO.size = file.length();
            fileDTO.type = contentType;
            fileDTO.data = new String(data, StandardCharsets.US_ASCII);

            //Lock lại ứng viên sau khi tạo file hợp đồng
            lockInterviewSession(request.contractId);
            return fileDTO;
        } catch (Exception ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
        }
        return null;
    }

    @Transactional
    public FileDTO.FileResponse onlyExportContract(ProbationaryContractEntity contract) {
        if (contract == null || contract.getContractId() == null) {
            throw new NotFoundException("Không tìm thấy hợp đồng");
        }
        try {
            //<editor-fold desc="Them thong tin nguoi ky hop dong">
            if (contract.getUnitId() == null)
                throw new NotFoundException("Không tìm thấy thông tin đơn vị của hợp đồng " + contract.getEmployeeName());

            Long unitId = contract.getUnitId();
            Map<String, String> map = CustomMapper.convert(convertEntityToResponse(contract));

            // Nếu người nhập là KCQ TCT thì sẽ lấy đơn vị là TCT
            SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
            Long unitOfUser = sSoResponse.getUnitId();
            if (unitOfUser.equals(VPSConstant.ALL_PROVINCE_UNIT_ID)) {
                unitId = unitOfUser;
            }
            Boolean isBranch = false;
            if (unitId > 0) {
                EmployerInfoEntity employer = employerInfoRepository.findByUnitId(unitId);
                EmployerInfoDTO.EmployerInfoResponseForExport employerResponse = employerInfoService.convertEntityToExportResponse(employer);
                if (employer != null) {
                    Map<String, String> employerMap = CustomMapper.convert(employerResponse);
                    map.putAll(employerMap);
                    String managerSign = contractService.getManagerSign(unitId, employer.getUnitName());
                    map.put("managerSign", managerSign);
                    isBranch = isBranch(employer.getUnitName());
                }
            }
            //</editor-fold>
            HashMap<String, String> hm = new HashMap<>();
            for (String key : map.keySet()) {
                if (map.get(key) != null) {
                    hm.put(key, map.get(key).toString());
                }
            }
            hm = convertSalary(hm);
//            hm.putAll(CustomMapper.convert(contract));

            String pathStore = folderExtension.getUploadFolder();
            File file = new ClassPathResource(isBranch ? FileTemplateConstant.PROBATIONARY_CONTRACT_BRANCH_DOCX : FileTemplateConstant.PROBATIONARY_CONTRACT_DOCX).getFile();
            String path = file.getPath();
            ExportWord<CollaboratorContractDTO> exportWord = new ExportWord<>();
            String realPath = exportWord.export(path, pathStore, "Hop dong thu viec " + " " + FileUtils.stripAccents(contract.getEmployeeName()),
                    hm, ExportFileExtension.PDF, null, FileTemplateConstant.SIGNAL_PROBATION_ADD_NOTE);
            file = new File(realPath);
            String contentType = Files.probeContentType(file.toPath());
//            updateContractFile(file.getName(), fileService.encodePathFromRealPath(realPath), request.contractId);
//            entity.setContractFile(file.getName());
//            entity.setContractFileEncodePath(fileService.encodePath(realPath));
//            entity.setSignedFile(fileService.encodePath(realPath));
//            entity = probationaryContractRepository.save(entity);

            byte[] data = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = file.getName();
            fileDTO.filePath = realPath;
            fileDTO.encodePath = fileService.encodePath(realPath);
            fileDTO.size = file.length();
            fileDTO.type = contentType;
            fileDTO.data = new String(data, StandardCharsets.US_ASCII);
            return fileDTO;
        } catch (Exception ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
            throw new BadRequestException("Không tạo được file hợp đồng thử việc");
        }
    }

    @Override
    public FileDTO.FileResponse createContractFileFromEntity(ContractExportRequest request) {
        ProbationaryContractEntity contractEntity = probationaryContractRepository.findById(request.contractId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hợp đồng có id: " + request.contractId));

        return onlyExportContract(contractEntity);
    }

    //Convert salary
    private HashMap<String, String> convertSalary(HashMap<String, String> hashMap) {
        //Convert format money
        String negotiateSalary = hashMap.get("negotiateSalary");
        if (org.apache.commons.lang3.StringUtils.isEmpty(negotiateSalary)) {
            return hashMap;
        }
        String negotiateSalaryFomart = StringUtils.numberCurrency(new Locale("vi", "VN"), Float.parseFloat(negotiateSalary));
        hashMap.put("negotiateSalary", negotiateSalaryFomart);
        return hashMap;
    }

    private void lockInterviewSession(Long contractId) {
        List<InterviewSessionCvEntity> interviewSessionCvEntities = interviewSessionCvRepository.getFromContract(contractId);
        List<InterviewSessionCvEntity> newInterviewSessionCv = interviewSessionCvEntities.stream().map(item -> {
            item.setIsLock(true);
            return item;
        }).collect(Collectors.toList());
        List<InterviewSessionEntity> interviewSessionEntities = interviewSessionCvEntities.stream().map(item -> {
            item.getInterviewSessionEntity().setIsLock(true);
            return item.getInterviewSessionEntity();
        }).collect(Collectors.toList());
        interviewSessionCvRepository.saveAll(newInterviewSessionCv);
        interviewSessionRepository.saveAll(interviewSessionEntities);
    }

    private Boolean isBranch(String unitName) {
        boolean result = false;
        if (unitName != null && unitName.toLowerCase().contains("chi nhánh")) {
            result = true;
        }
        return result;
    }
}