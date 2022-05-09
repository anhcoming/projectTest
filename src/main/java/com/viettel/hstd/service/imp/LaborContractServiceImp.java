package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.hstd.constant.*;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.constant.ConstantConfig;
import com.viettel.hstd.core.constant.ExportFileExtension;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.*;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.CollaboratorContractDTO;
import com.viettel.hstd.dto.hstd.ContractDTO;
import com.viettel.hstd.dto.hstd.ContractDTO.*;
import com.viettel.hstd.dto.hstd.EmployerInfoDTO;
import com.viettel.hstd.dto.hstd.LaborContractDTO.*;
import com.viettel.hstd.entity.hstd.*;
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

import static com.viettel.hstd.constant.FileTemplateConstant.LABOR_CONTRACT_OUTPUT_NAME;

@Service
@Slf4j
@Transactional
public class LaborContractServiceImp extends BaseService implements LaborContractService {

    @Autowired
    private LaborContractRepository laborContractRepository;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    Message message;
    @Autowired
    MapUtils mapUtils;

    @Autowired
    SysConfigRepository sysConfigRepository;

    @Autowired
    HSTDFilter hstdFilter;

    @Autowired
    VhrFutureOrganizationRepository organizationRepository;

    @Autowired
    EmployerInfoRepository employerInfoRepository;

    @Autowired
    FolderExtension folderExtension;

    @Autowired
    FileService fileService;

    @Autowired
    ContractService contractService;

    @Autowired
    private EmployeeVhrRepository employeeVhrRepository;

    @Autowired
    ResignSessionContractRepository resignSessionContractRepository;

    @Autowired
    ResignSessionContractService resignSessionContractService;

    @Autowired
    private VhrFutureOrganizationRepository vhrFutureOrganizationRepository;

    @Override
    public Page<LaborContractResponse> findPage(SearchDTO searchRequest) {
        hstdFilter.unitDepartmentFilter(searchRequest, "unitId");

        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<LaborContractEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<LaborContractEntity> list;
        if (searchRequest.pagedFlag) {
            list = laborContractRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = laborContractRepository.findAll(p);
        }

        return list.map(this::convertEntityToResponse);
    }

    @Override
    public LaborContractResponse findOneById(Long id) {
        LaborContractEntity entity = laborContractRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return convertEntityToResponse(entity);
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        LaborContractEntity laborContractEntity = laborContractRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hợp đồng không tìm thấy trên hệ thống"));

        if (laborContractEntity.getTransCode() != null) {
            throw new BadRequestException("Hợp đồng đang trong trạng thái trình ký không được xóa");
        }

        List<ResignSessionContractEntity> resignSessionContractEntities = resignSessionContractRepository.findByContractId(id);
        if (!resignSessionContractEntities.isEmpty()) {
            ResignSessionContractEntity resignSessionContractEntity = resignSessionContractEntities.get(0);
            ContractEntity newContract = contractRepository.findByContractId(resignSessionContractEntity.newContractId).orElse(null);
            if (newContract != null) {
                throw new BadRequestException("Đang có 1 hợp đồng mới được tạo ra từ hợp đồng này , nên không thể xóa");
            }

            resignSessionContractRepository.deleteAll(resignSessionContractEntities);
        }

        laborContractRepository.softDelete(id);
        super.addLog("LABOR_CONTRACT", "DELETE", id.toString());
        return true;
    }

    @Override
    public LaborContractResponse create(LaborContractRequest request) {
        LaborContractEntity entity = objectMapper.convertValue(request, LaborContractEntity.class);
        entity = laborContractRepository.save(entity);
        super.addLog("LABOR_CONTRACT", "CREATE", new Gson().toJson(request));
        return convertEntityToResponse(entity);
    }

    @Override
    public LaborContractResponse update(Long id, LaborContractRequest request) {
        LaborContractEntity entity = laborContractRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        LaborContractEntity newE = objectMapper.convertValue(request, LaborContractEntity.class);
        mapUtils.customMap(newE, entity);
        entity.setContractId(id);
        entity = laborContractRepository.save(entity);
        super.addLog("LABOR_CONTRACT", "UPDATE", new Gson().toJson(request));
        return convertEntityToResponse(entity);
    }

    @Override
    public Page<LaborContractResponse> findAboutToExpiredContract(SearchDTO searchDTO) {
        try {
            List<SearchDTO.SearchCriteria> oldCriteria = searchDTO.criteriaList;
            List<SearchDTO.SearchCriteria> newCriteria = new ArrayList<>();

            SysConfigEntity sysConfigEntity = sysConfigRepository.findFirstByConfigKey(ConstantConfig.EXPIRED_CONTRACT_LENGTH_CONFIG_CODE).orElseThrow(() -> new NotFoundException("Không tìm thấy cấu hình hợp đồng hết hạn"));
            Long numOfExpiredMonth = Long.parseLong(sysConfigEntity.getConfigValue());

            SearchDTO.SearchCriteria numMonthCriteria = new SearchDTO.SearchCriteria();
            numMonthCriteria.setField("expiredDate");
            numMonthCriteria.setOperation(Operation.LESS_EQUAL);
            numMonthCriteria.setValue(objectMapper.writeValueAsString(LocalDate.now().plusMonths(numOfExpiredMonth)).replace("\"", ""));
            numMonthCriteria.setType(SearchType.DATE);

            newCriteria.add(numMonthCriteria);
            newCriteria.addAll(oldCriteria);
            searchDTO.criteriaList = newCriteria;

            hstdFilter.unitDepartmentFilter(searchDTO, "unitId");

        } catch (JsonProcessingException e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            log.error(stackTrace);
        }

        return findPage(searchDTO);
    }

    private LaborContractResponse convertEntityToResponse(LaborContractEntity entity) {
        LaborContractResponse response = objectMapper.convertValue(entity, LaborContractResponse.class);

        VhrFutureOrganizationEntity departmentEntity = organizationRepository.findById(entity.getDepartmentId()).orElseThrow(() -> new NotFoundException("Không tìm thấy phòng ban"));

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

    public void updateContractResignStatus(Set<Long> contractIds, ResignStatus resignStatus) {
        laborContractRepository.updateResignStatus(contractIds, resignStatus.getValue());
    }

    @Autowired
    EmployerInforService employerInfoService;

    public FileDTO.FileResponse exportContract(Long contractId) {
        try {
            LaborContractResponse contract = findOneById(contractId);
            Map<String, String> map = CustomMapper.convert(contract);
            //<editor-fold desc="Them thong tin nguoi ky hop dong">

            if (contract.unitId == null)
                throw new NotFoundException("Không tìm thấy thông tin đơn vị của hợp đồng " + contract.employeeName);
            Long unitId = contract.unitId;

            // Nếu người nhập là KCQ TCT thì sẽ lấy đơn vị là TCT
            SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
            Long unitOfUser = sSoResponse.getUnitId();
            if (unitOfUser.equals(VPSConstant.ALL_PROVINCE_UNIT_ID)) {
                unitId = unitOfUser;
            }

            if (unitId > 0) {
                EmployerInfoEntity employer = employerInfoRepository.findByUnitId(unitId);
                EmployerInfoDTO.EmployerInfoResponseForExport employerResponse = employerInfoService.convertEntityToExportResponse(employer);
                if (employer != null) {
                    Map<String, String> employerMap = CustomMapper.convert(employerResponse);
                    map.putAll(employerMap);
                    String managerSign = contractService.getManagerSign(unitId, employer.getUnitName());
                    map.put("managerSign", managerSign);
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
            File file = new ClassPathResource(FileTemplateConstant.LABOR_CONTRACT_DOCX).getFile();
            String path = file.getPath();
            ExportWord<CollaboratorContractDTO> exportWord = new ExportWord<>();
            String realPath = exportWord.export(path, pathStore, LABOR_CONTRACT_OUTPUT_NAME + " " + FileUtils.stripAccents(contract.employeeName), hm, ExportFileExtension.PDF, null, FileTemplateConstant.SIGNAL_LABOR_ADD_NOTE);
            file = new File(realPath);
            String contentType = Files.probeContentType(file.toPath());

            // Handle db
            contractService.updateContractFileAndContractFileEncodePath(file.getName(), fileService.encodePath(realPath), contractId);

            byte[] data = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = file.getName();
            fileDTO.filePath = fileService.encodePath(realPath);
            fileDTO.encodePath = fileService.encodePath(realPath);
            fileDTO.size = file.length();
            fileDTO.type = contentType;
            fileDTO.data = new String(data, StandardCharsets.US_ASCII);
            return fileDTO;
        } catch (Exception ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
            throw new BadRequestException("Không thể tạo được file hợp đồng lao động với id = " + contractId);
        }
    }

    @Override
    @Transactional
    public FileDTO.FileResponse createTempContract(ContractDTO.ContractExportRequest contractExportRequest) {
        FileDTO.FileResponse fileResponse = exportContract(contractExportRequest.contractId);
        contractService.updateResignStatus(contractExportRequest.contractId, ResignStatus.HR_UPDATED_RESIGN_CONTRACT);
        return fileResponse;
    }

    @Override
    @Transactional
    public FileDTO.FileResponse createOnlyTempContract(ContractEntity contractEntity) {
        try {
            if (contractEntity == null || contractEntity.getContractId() == null) {
                throw new NotFoundException("Không tìm thấy hợp đồng");
            }
            LaborContractResponse laborContractResponse = objectMapper.convertValue(contractEntity, LaborContractResponse.class);
            laborContractResponse.contractDurationVietnamese = contractEntity.getContractDuration().getVietnameseStringValue();
            laborContractResponse.personalIdIssuedPlace = contractEntity.getPersonalIdIssuedPlace();
            laborContractResponse.personalIdIssuedDate = contractEntity.getPersonalIdIssuedDate();
            laborContractResponse.signedDate = contractEntity.getSignedDate();
            Map<String, String> map = CustomMapper.convert(laborContractResponse);
            if (contractEntity.getUnitId() == null) {
                throw new NotFoundException("Không tìm thấy thông tin đơn vị của hợp đồng " + contractEntity.getEmployeeName());
            }
            Long unitId = contractEntity.getUnitId();
            // Nếu người nhập là KCQ TCT thì sẽ lấy đơn vị là TCT 
            SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
            Long unitOfUser = sSoResponse.getUnitId();
            if (unitOfUser.equals(VPSConstant.ALL_PROVINCE_UNIT_ID)) {
                unitId = unitOfUser;
            }

            if (unitId > 0) {
                EmployerInfoEntity employer = employerInfoRepository.findByUnitId(unitId);
                EmployerInfoDTO.EmployerInfoResponseForExport employerResponse = employerInfoService.convertEntityToExportResponse(employer);
                if (employer != null) {
                    Map<String, String> employerMap = CustomMapper.convert(employerResponse);
                    map.putAll(employerMap);
                    String managerSign = contractService.getManagerSign(unitId, employer.getUnitName());
                    map.put("managerSign", managerSign);
                }
            }

            HashMap<String, String> hm = new HashMap<>();
            for (String key : map.keySet()) {
                if (map.get(key) != null) {
                    hm.put(key, map.get(key).toString());
                }
            }

            hm = convertSalary(hm);

            String pathStore = folderExtension.getUploadFolder();
            File file = new ClassPathResource(FileTemplateConstant.LABOR_CONTRACT_DOCX).getFile();
            String path = file.getPath();
            ExportWord<CollaboratorContractDTO> exportWord = new ExportWord<>();
            String realPath = exportWord.export(path, pathStore, LABOR_CONTRACT_OUTPUT_NAME + " " + FileUtils.stripAccents(contractEntity.getEmployeeName()), hm, ExportFileExtension.PDF, null, FileTemplateConstant.SIGNAL_LABOR_ADD_NOTE);
            file = new File(realPath);
            String contentType = Files.probeContentType(file.toPath());
            byte[] data = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = file.getName();
            fileDTO.filePath = fileService.encodePath(realPath);
            fileDTO.encodePath = fileService.encodePath(realPath);
            fileDTO.size = file.length();
            fileDTO.type = contentType;
            fileDTO.data = new String(data, StandardCharsets.US_ASCII);
            return fileDTO;

        } catch (Exception ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
            throw new BadRequestException("Không thể tạo được file hợp đồng lao động với mã nhân viên là:  " + contractEntity.getEmployeeCode());
        }
    }

    @Autowired
    private SmsMessageService smsMessageService;

    @Autowired
    private ContractRepository contractRepository;

    @Override
    public FileDTO.FileResponse sendContractToEmployeeInResign(Long contractId) {
        // Tao file
        FileDTO.FileResponse fileResponse = new FileDTO.FileResponse();
//        FileDTO.FileResponse fileResponse = exportContract(contractId);
        String phoneNumber = contractRepository.getPhoneNumberByContractId(contractId).orElse(null);
        if (phoneNumber == null) {
            throw new NotFoundException("Hợp đồng không có số điện thoại");
        }
        smsMessageService.sendSmsMessage(phoneNumber, "Ban co mot hop dong can ky dien tu. Vui long truy cap phan mem Ho so dien tu de ky hop dong.");

        // Thay doi trang thai
        contractService.updateResignStatus(contractId, ResignStatus.HR_CREATED_FILE_4_EMPLOYEE_2_SIGN);

        return fileResponse;
    }


    public FileDTO.FileResponse addEmployeeSignature(ContractAddEmployeeSignature request) {
        if (request == null) {
            throw new NotFoundException("Yêu cầu không chính xác");
        }
        LaborContractEntity entity = laborContractRepository.findById(request.contractId).orElseThrow(() -> new NotFoundException("Không tìm thấy hợp đồng"));
        //<editor-fold desc="Them thong tin nguoi ky hop dong">

        //</editor-fold>

        try {
            String pathStore = folderExtension.getUploadFolder();

            if (entity.getContractFileEncodePath() == null) {
                throw new NotFoundException("Không tìm thấy file mềm hợp đồng của " + entity.getEmployeeName());
            }

            File contractFile = new File(fileService.decodePath(entity.getContractFileEncodePath()));
            if (request.signatureFileEncodePath != null) {
                entity.setSignatureName(request.signatureFileName);
                entity.setSignatureFileEncodePath(request.signatureFileEncodePath);
            }
            //Them chu ky trong file
            if (entity.getSignatureFileEncodePath() != null && entity.getSignatureFileEncodePath().trim().length() > 0) {
                File imgPath = new File(fileService.decodePath(request.signatureFileEncodePath));
                try {
                    LocalDateTime now = LocalDateTime.now();
                    entity.setSignedDate(now.toLocalDate());
                    PdfAnnotations.addImagePdf(contractFile.getPath(), imgPath.getPath(), "(Người lao động)", HSDTConstant.LABOR_CONTRACT_DOCUMENT_TYPE, now);
                    contractFile = new File(fileService.decodePath(entity.getContractFileEncodePath()));
                } catch (Exception e) {
                    String stackTrace = ExceptionUtils.getStackTrace(e);
                    log.error(stackTrace);
                    throw new BadRequestException("Không thể thêm chữ ký vào hợp đồng");
                }
            }

            String contentType = Files.probeContentType(contractFile.toPath());
            String fileName = folderExtension.getFileName(contractFile.getAbsolutePath());


            entity.setSignatureName(request.signatureFileName);
            entity.setSignatureFileEncodePath(request.signatureFileEncodePath);
            // TODO: Kha vo dung vi chu ky duoc luu o file cu, bo di khi xong
            entity.setEmployeeSignedFile(entity.getContractFile());
            entity.setEmployeeSignedFileEncodePath(entity.getContractFileEncodePath());
            entity.setSignedFile(entity.getContractFile());
            entity.setSignedFileEncodePath(entity.getContractFileEncodePath());

            laborContractRepository.save(entity);
            byte[] data = Base64.getEncoder().encode(Files.readAllBytes(contractFile.toPath()));
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = contractFile.getName();
            fileDTO.filePath = entity.getContractFileEncodePath();
            fileDTO.encodePath = entity.getContractFileEncodePath();
            fileDTO.size = contractFile.length();
            fileDTO.type = contentType;
            fileDTO.data = new String(data, StandardCharsets.US_ASCII);
            return fileDTO;
        } catch (Exception ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
        }
        return null;
    }

    @Override
    public FileDTO.FileResponse addEmployeeSignatureInResign(ContractAddEmployeeSignature request) {
        FileDTO.FileResponse response = addEmployeeSignature(request);
        contractService.updateResignStatus(request.contractId, ResignStatus.EMPLOYEE_SIGN_RESIGN_CONTRACT);

        return response;
    }

    @Override
    public boolean updateResignStatus(Long contractId, ResignStatus resignStatus) {
        laborContractRepository.updateResignStatus(contractId, resignStatus);
        return true;
    }

    @Override
    public void turnTempContractToActualContract(Set<Long> contractIdSet) {
        laborContractRepository.updateIsActiveTrue(contractIdSet);
        contractService.updateResignStatus(contractIdSet, ResignStatus.NOT_IN_RESIGN_SESSION);

    }

    //Convert salary
    private HashMap<String, String> convertSalary(HashMap<String, String> hashMap) {
        //Convert format money
        String basicSalary = hashMap.get("basicSalary");
        if (org.apache.commons.lang3.StringUtils.isEmpty(basicSalary)) {
            return hashMap;
        }
        String basicSalaryFomart = StringUtils.numberCurrency(new Locale("vi", "VN"), Float.parseFloat(basicSalary));
        hashMap.put("basicSalary", basicSalaryFomart);
        return hashMap;
    }

}
