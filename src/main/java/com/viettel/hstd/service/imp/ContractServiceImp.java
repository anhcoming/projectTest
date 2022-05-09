package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.constant.NewContractStatus;
import com.viettel.hstd.constant.ResignPassStatus;
import com.viettel.hstd.constant.ResignStatus;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.*;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ContractDTO.*;
import com.viettel.hstd.dto.hstd.ContractImportDTO;
import com.viettel.hstd.dto.hstd.ResignSessionDTO;
import com.viettel.hstd.entity.hstd.*;
import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.filter.HSTDFilter;
import com.viettel.hstd.repository.hstd.*;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
import com.viettel.hstd.service.inf.ContractService;
import com.viettel.hstd.service.inf.FileService;
import com.viettel.hstd.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.viettel.hstd.constant.HSDTConstant.DANH_SACH_NHAN_SU_PATH;
import static com.viettel.hstd.constant.HSDTConstant.DEFAULT_COMPENSATION;

@Service
@Slf4j
public class ContractServiceImp extends BaseService implements ContractService {

    @Autowired
    ContractRepository contractRepository;

    @Autowired
    Message message;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MapUtils mapUtils;

    @Autowired
    VhrFutureOrganizationRepository organizationRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    LaborContractRepository laborContractRepository;

    @Autowired
    EmployerInfoRepository employerInfoRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private ExcelUtil excelUtil;

    @Autowired
    private InterviewSessionCvRepository interviewSessionCvRepository;

    @Autowired
    HSTDFilter hstdFilter;

    @Autowired
    VhrFutureOrganizationRepository vhrFutureOrganizationRepository;

    @Autowired
    ResignSessionRepository resignSessionRepository;

    @Override
    public ContractResponse findOneById(Long id) {
        ContractEntity contractEntity = contractRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return convertEntityToResponse(contractEntity);
    }

    @Override
    public Boolean delete(Long id) {
        if (!contractRepository.existsById(id))
            throw new NotFoundException(message.getMessage("message.not_found"));
        contractRepository.softDelete(id);
        addLog("CONTRACT_DELETE");
        return true;
    }

    @Override
    public List<ContractResponse> findAll() {
        return ContractService.super.findAll();
    }

    @Override
    public Page<ContractResponse> findPage(SearchDTO searchRequest) {
        hstdFilter.unitDepartmentFilter(searchRequest, "unitId");
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<ContractEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<ContractEntity> list;
        if (searchRequest.pagedFlag) {
            list = contractRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = contractRepository.findAll(p);
        }

        return list.map(this::convertEntityToResponse);
    }

    @Override
    public ContractResponse update(Long id, ContractRequest request) {
        ContractEntity entity = contractRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        ContractEntity newE = objectMapper.convertValue(request, ContractEntity.class);
        entity.setContractId(id);
        entity = contractRepository.save(entity);
        addLog("UPDATE");
        return convertEntityToResponse(entity);
    }

    @Override
//    @PreAuthorize("hasPermission('DELETE CONTRACT_MANAGEMENT', '')")
    public void updateVofficeCalled(Long id, ContractRequest request) {
        ContractEntity entity = contractRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        entity.setIsCallVoffice(request.isCallVoffice);
        entity.setTransCode(request.transCode);
        entity.setSignedFile(request.signedFile);
        if (request.contractFile != null && request.contractFile.trim().length() > 0) {
            entity.setContractFile(request.contractFile);
        }
        entity = contractRepository.save(entity);
    }

    @Override
    public ContractResponse findByTransCode(String transCode) {
        ContractEntity contractEntity = contractRepository.findByTransCode(transCode);
        if (contractEntity == null) {
            return null;
        }
        return convertEntityToResponse(contractEntity);
    }

    @Override
    public void updateSignedFile(String filePath, Long id) {
        ContractEntity entity = contractRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        entity.setSignedFileEncodePath(filePath);
        entity = contractRepository.save(entity);
    }

    private ContractResponse convertEntityToResponse(ContractEntity entity) {
        ContractResponse response = objectMapper.convertValue(entity, ContractResponse.class);

        VhrFutureOrganizationEntity departmentEntity = organizationRepository.findById(entity.getDepartmentId()).orElseThrow(() -> new NotFoundException("Không tìm thấy phòng ban"));

        response.unitName = departmentEntity.getOrgNameLevel2();
        response.departmentName = departmentEntity.getOrgNameLevel3();
        response.timeLeft = "Còn " + TimeUtil.intervalBetweenLocalDates(LocalDate.now(), entity.getExpiredDate());
        response.timeDiffStartDateAndSignDate = timeDiffSignedDateAndStartDate(entity.getSignedDate(), entity.getEffectiveDate());
        return response;
    }

    public void updateContractResignStatus(Long resignSessionId, ResignStatus resignStatus) {
        ResignSessionEntity resignSessionEntity = resignSessionRepository.findById(resignSessionId).orElseThrow(() -> new NotFoundException("Không tìm thấy đợt tái ký"));

        Set<Long> contractIds = resignSessionEntity.getResignSessionContractEntities().stream().filter(obj -> obj.getPassStatus() == ResignPassStatus.PASS).map(obj -> obj.getContractEntity().getContractId()).collect(Collectors.toSet());
        contractRepository.updateResignStatusByContractIdSet(contractIds, resignStatus);
    }

    @Override
    public boolean updateContractResignStatus(UpdateResignStatusRequest request) {
        Set<Long> contractIds = new HashSet<>(request.contractIdList);
        contractRepository.updateResignStatusByContractIdSet(contractIds, request.resignStatus);

        return true;
    }

    @Override
    public boolean updateTransCode(UpdateTransCodeRequest request) {
        contractRepository.updateTranscode(request.contractId, request.transCode);

        return true;
    }

    @Override
    public void updateTranscodeOfContractInResignSession(String transcode, Long resignSessionId) {
        ResignSessionEntity resignSessionEntity = resignSessionRepository.findById(resignSessionId).orElseThrow(() -> new NotFoundException("Không tìm thấy đợt tái ký"));

        resignSessionEntity.setTranscode(transcode);
        resignSessionRepository.save(resignSessionEntity);
    }

    @Override
    public ContractResponse updateContractFile(String fileName, Long id) {
        ContractEntity entity = contractRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        entity.setContractFile(fileName);
        entity = contractRepository.save(entity);
        return objectMapper.convertValue(entity, ContractResponse.class);
    }

//    @Override
//    public FileDTO.FileResponse createPDFContractAndSignByUser(CreatePDFContractAndSignedByUserRequest request) {
//        LaborContractEntity entity = laborContractRepository
//            .findById(request.contractId)
//            .orElseThrow(() -> new NotFoundException("Không tìm thấy hợp đồng"));
//        Map<String, String> map = CustomMapper.convert(entity);
//        //<editor-fold desc="Them thong tin nguoi ky hop dong">
//        SSoResponse sSoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
//        if (sSoResponse.getOrganizationId() != null && sSoResponse.getOrganizationId() > 0) {
//            EmployerInfoEntity employer = employerInfoRepository.findByUnitId(entity.getUnitId());
//            if (employer != null) {
//                Map<String, String> employerMap = CustomMapper.convert(employer);
//                map.putAll(employerMap);
//            }
//        }
//        //</editor-fold>
//        HashMap<String, String> hm = new HashMap<>();
//        for (String key : map.keySet()) {
//            if (map.get(key) != null) {
//                hm.put(key, map.get(key));
//            }
//        }
//        try {
//            String pathStore = folderExtension.getUploadFolder();
//
//            File file = new ClassPathResource("template/" + (request.type != null && request.type == 1 ? "DonXinChamDutHD_TCT" : "DonXinChamDutHD_DV") + ".docx").getFile();
//            String path = file.getPath();
//            ExportWord<InterviewResultDTO> exportWord = new ExportWord<>();
//            HashMap<String, List<InterviewResultDTO>> resultMap = new HashMap<>();
//            String resultPath = exportWord.exportV2(path, pathStore, "DonXinChamDutHD",
//                hm, ExportFileExtension.PDF, resultMap, request.type != null && request.type == 1 ? "ResignationLetter" : "ResignationLetter1");
//            file = new File(resultPath);
//            //Them chu ky trong file
//            if (entity.getSignaturePath() != null && entity.getSignaturePath().trim().length() > 0) {
//                File imgPath = new File(folderExtension.getUploadFolder() + "/" + entity.getSignaturePath().replace("-", "/"));
//                try {
//                    PdfAnnotations.addImagePdf(file.getAbsolutePath(), imgPath.getAbsolutePath(), "NGƯỜI LÀM ĐƠN");
//                    file = new File(resultPath);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            String contentType = Files.probeContentType(file.toPath());
//            String fileName = folderExtension.getFileName(resultPath);
////            if (entity.getFilePath() == null || entity.getFilePath().trim().length() == 0) {
//            entity.setFilePath(fileName);
//            entity.setFileName(fileName);
//            terminateContractRepository.save(entity);
////            }
//            byte[] data = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
//            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
//            fileDTO.fileName = file.getName();
//            fileDTO.filePath = fileName;
//            fileDTO.size = file.length();
//            fileDTO.type = contentType;
//            fileDTO.data = new String(data, StandardCharsets.US_ASCII);
//            return fileDTO;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }

    @Override
    public boolean updateNewContractStatus(ContractNewStatusRequest request) {
        contractRepository.updateNewContractStatus(request.contractId, request.newContractStatus);
        return true;
    }

    @Override
    public boolean updateSignedFile(ContractChangeSignedFileRequest request) {
        contractRepository.updateSignedFile(request.contractId, request.signedFile, request.signedFile);
        return true;
    }

    @Override
    public boolean updateResignStatus(Long contractId, ResignStatus resignStatus) {
        Set<Long> contractIdSet = new HashSet<>();
        contractIdSet.add(contractId);
        contractRepository.updateResignStatusByContractIdSet(contractIdSet, resignStatus);
        return true;
    }

    @Override
    public boolean updateResignStatus(Set<Long> contractIdSet, ResignStatus resignStatus) {
        contractRepository.updateResignStatusByContractIdSet(contractIdSet, resignStatus);
        return true;
    }

    @Override
    public void updateContractFileAndContractFileEncodePath(String fileName, String encodePath, Long id) {
        ContractEntity entity = contractRepository.findById(id).orElseThrow(() -> new NotFoundException(message.getMessage("Không tìm thấy hợp đồng")));
        entity.setContractFile(fileName);
        entity.setContractFileEncodePath(encodePath);
        entity = contractRepository.save(entity);
    }

    @Override
    public void updateContractFileEncodeToContractFile(Set<Long> contractIdSet) {
        contractRepository.updateContractFileEncodeToContractFileAndSignedFile(contractIdSet);
    }

    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public FileDTO.FileResponse exportDanhSachNhanSu() {
        Set<NewContractStatus> newContractStatusSet = new HashSet<>();
        newContractStatusSet.add(NewContractStatus.HR_SENT_CONTRACT_EMPLOYEE);
        newContractStatusSet.add(NewContractStatus.EMPLOYEE_SIGNED_CONTRACT);
        List<ContractEntity> contractEntityList = contractRepository.getContractByNewContractStatusSet(newContractStatusSet);

        return exportDanhSachNhanSu(contractEntityList);
    }

    @Override
    public String getManagerSign(Long unitId, String unitName) {
        String result = null;
        VhrFutureOrganizationEntity vhrFutureOrganizationEntity = vhrFutureOrganizationRepository.findByOrganizationId(unitId);
        if (vhrFutureOrganizationEntity == null) {
            throw new NotFoundException("Không tìm thấy đơn vị " + unitName);
        }
        if (unitName.toLowerCase().contains("chi nhánh")) {
            result = "GIÁM ĐỐC";
        } else if (unitName.toLowerCase().contains("trung tâm")) {
            String code = vhrFutureOrganizationEntity.getCode();
            String onlyCodeUnit = code.replaceAll("TCTCTTT", "");
            result = "TUQ. TỔNG GIÁM ĐỐC \n GIÁM ĐỐC TT " + onlyCodeUnit;
        } else {
            result = "KT.TỔNG GIÁM ĐỐC \n PHÓ TỔNG GIÁM ĐỐC";
        }
        return result;
    }

    private FileDTO.FileResponse exportDanhSachNhanSu(List<ContractEntity> contractEntityList) {
        FileDTO.FileResponse fileResponse = new FileDTO.FileResponse();

        String input = DANH_SACH_NHAN_SU_PATH;
        String fileName = "Danh sach nhan su.xlsx";

        String outputFolder = fileService.getUploadFolder();
        fileService.createFolder(fileService.encodePath(outputFolder));
        String output = outputFolder + fileService.getFilePrefix() + fileName;

        if (contractEntityList.size() == 0) throw new NotFoundException("Không thấy dữ liệu");

        List<DanhSachDeXuatNhanSuResponse> responseList = contractEntityList.stream().map(this::convertEntityToDachSachDeXuatNhanSu).collect(Collectors.toList());

        for (int i = 0; i < responseList.size(); i++) {
            responseList.get(i).index = i + 1;
        }

        ResignSessionDTO.Bm07Metadata bm07Metadata = new ResignSessionDTO.Bm07Metadata();
        bm07Metadata.quarter = 4;
        bm07Metadata.year = 2021;

        excelUtil.exportExcel(input, output, bm07Metadata, responseList);
        File file = new File(output);

        try {
            byte[] data = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
            fileResponse.filePath = fileService.encodePath(output);
            fileResponse.downloadFileExtension = ".xlsx";
            fileResponse.fileName = fileName;
            fileResponse.data = new String(data, StandardCharsets.US_ASCII);
            fileResponse.size = file.length();
        } catch (IOException ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
        }

        return fileResponse;
    }

    private DanhSachDeXuatNhanSuResponse convertEntityToDachSachDeXuatNhanSu(ContractEntity contractEntity) {
        DanhSachDeXuatNhanSuResponse response = objectMapper.convertValue(contractEntity, DanhSachDeXuatNhanSuResponse.class);

        response.fullName = contractEntity.getEmployeeName();
        response.birthYear = contractEntity.getBirthDate().getYear();

        List<InterviewSessionCvEntity> interviewCvList = interviewSessionCvRepository.getFromContract(contractEntity.getContractId());
        if (interviewCvList.size() == 0) {
            throw new NotFoundException("Không tìm thấy kết quả phỏng vấn ứng với hợp đồng nhân viên có mã: " + contractEntity.getEmployeeCode());
        } else if (interviewCvList.size() > 1) {
            throw new BadRequestException("Dữ liệu hợp đồng và kết quả phóng vấn đang không đồng bộ");
        }

        InterviewSessionCvEntity interviewCvEntity = interviewCvList.get(0);
        response.interviewScore = interviewCvEntity.getSumPoint();
        response.styleScore = interviewCvEntity.getStyleGrade();
        response.englishScore = interviewCvEntity.getEnglish();
        response.technologyFamiliarityGrade = interviewCvEntity.getTechnologyFamiliarityGrade();
        response.interviewResult = interviewCvEntity.getResult();

        response.compensation = DEFAULT_COMPENSATION;
        response.compensation = response.compensation.replace("{contractDuration_int}", contractEntity.getContractDuration().getValue() + "");

        return response;
    }

    private String timeDiffSignedDateAndStartDate(LocalDate signedDate, LocalDate startDate) {
        if (signedDate == null || startDate == null) return "Không xác định";
        String result = "";
        LocalDate fromDate = startDate.isAfter(signedDate) ? signedDate : startDate;

        LocalDate toDate = startDate.isBefore(signedDate) ? signedDate : startDate;

        result = result + (startDate.isAfter(signedDate) ? "+" : "-");
        long years = fromDate.until(toDate, ChronoUnit.YEARS);
        fromDate = fromDate.plusYears(years);

        long months = fromDate.until(toDate, ChronoUnit.MONTHS);
        fromDate = fromDate.plusMonths(months);

        long days = fromDate.until(toDate, ChronoUnit.DAYS);
        fromDate = fromDate.plusDays(days);

        if (years > 0) result += years + " năm ";
        if (months > 0) result += months + " tháng ";
        if (days > 0) result += days + " ngày";

        if (years == 0 && months == 0 && days == 0) {
            return "0 ngày";
        }

        return result;
    }
}
