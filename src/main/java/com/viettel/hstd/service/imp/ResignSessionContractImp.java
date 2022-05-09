package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.hstd.constant.*;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.constant.ExportFileExtension;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.*;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ContractDTO.*;
import com.viettel.hstd.dto.hstd.EmployeeMonthlyReviewDTO;
import com.viettel.hstd.dto.hstd.ResignSessionDTO.*;
import com.viettel.hstd.dto.hstd.ResignSessionContractDTO.*;
import com.viettel.hstd.dto.vps.EmployeeVhrDTO;
import com.viettel.hstd.dto.vps.VhrFutureOrganizationDTO;
import com.viettel.hstd.entity.hstd.*;
import com.viettel.hstd.entity.vps.EmployeeVhrEntity;
import com.viettel.hstd.entity.vps.PositionEntity;
import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.filter.HSTDFilter;
import com.viettel.hstd.repository.hstd.ContractRepository;
import com.viettel.hstd.repository.hstd.EmployeeMonthlyReviewRepository;
import com.viettel.hstd.repository.hstd.ResignSessionContractRepository;
import com.viettel.hstd.repository.hstd.ResignSessionRepository;
import com.viettel.hstd.repository.vps.EmployeeVhrRepository;
import com.viettel.hstd.repository.vps.PositionRepository;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
import com.viettel.hstd.service.inf.*;
import com.viettel.hstd.util.ExcelUtil;
import com.viettel.hstd.util.FolderExtension;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.viettel.hstd.constant.FileTemplateConstant.*;
import static com.viettel.hstd.constant.HSDTConstant.*;

@Service
@Slf4j
public class ResignSessionContractImp extends BaseService implements ResignSessionContractService {

    @Autowired
    private ResignSessionContractRepository resignSessionContractRepository;
    @Autowired
    private Message message;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MapUtils mapUtils;
    @Autowired
    private EmployeeVhrRepository employeeVhrRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private VhrFutureOrganizationRepository organizationRepository;
    @Autowired
    private VhrFutureOrganizationService organizationService;
    @Autowired
    private EmployeeMonthlyReviewRepository employeeMonthlyReviewRepository;
    @Autowired
    private ResignSessionRepository resignSessionRepository;
    @Autowired
    private FolderExtension folderExtension;
    @Autowired
    private LaborContractService laborContractService;
    @Autowired
    private ExcelUtil excelUtil;
    @Autowired
    private ResignSessionService resignSessionService;
    @Autowired
    private HSTDFilter hstdFilter;
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private EmployeeVhrService employeeVhrService;
    @Autowired
    private PDFUtil pdfUtil;
    @Autowired
    private FileService fileService;

    @Override
    public ResignSessionContractResponse findOneById(Long id) {
        ResignSessionContractEntity resignSessionContractEntity = resignSessionContractRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return convertEntity2Response(resignSessionContractEntity);
    }

    @Override
    public Boolean delete(Long id) {
        ResignSessionContractEntity resignSessionContractEntity = resignSessionContractRepository.findById(id).
                orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        if (!resignSessionContractEntity.getResignStatus().equals(ResignStatus.NOT_IN_RESIGN_SESSION)) {
            throw new BadRequestException("Hợp đồng đang trong đợt tái ký ko thể xóa");
        }
        resignSessionContractRepository.softDelete(id);
        addLog("DELETE_RESIGN_SESSION_CONTRACT");
        return true;
    }

    @Override
    public Page<ResignSessionContractResponse> findPage(SearchDTO searchRequest) {
        hstdFilter.unitDepartmentFilter(searchRequest, "contractEntity.unitId");

        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<ResignSessionContractEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<ResignSessionContractEntity> list;
        if (searchRequest.pagedFlag) {
            list = resignSessionContractRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = resignSessionContractRepository.findAll(p);
        }

        return list.map(this::convertEntity2Response);
    }

    @Override
    public ResignSessionContractResponse create(ResignSessionContractRequest request) {
        ResignSessionContractEntity resignSessionContractEntity = objectMapper.convertValue(request, ResignSessionContractEntity.class);
        resignSessionContractEntity = resignSessionContractRepository.save(resignSessionContractEntity);
        addLog("CREATE");
        return convertEntity2Response(resignSessionContractEntity);
    }

    @Override
    public ResignSessionContractResponse update(Long id, ResignSessionContractRequest request) {
        ResignSessionContractEntity resignSessionContractEntity = resignSessionContractRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        ResignSessionContractEntity newE = objectMapper.convertValue(request, ResignSessionContractEntity.class);

        // Don't update contract and sessionId at this method
//        ContractEntity contractEntity = new ContractEntity();
//        contractEntity.setContractId(resignSessionContractEntity.getContractEntity().getContractId());
//        ResignSessionEntity resignSessionEntity = new ResignSessionEntity();
//        resignSessionEntity.setResignSessionId(resignSessionContractEntity.getResignSessionEntity().getResignSessionId());

//        newE.setResignSessionEntity(resignSessionEntity);
//        newE.setContractEntity(contractEntity);
        // Contract vs Resign da duoc bo ko map o BeanConfig
        mapUtils.customMap(newE, resignSessionContractEntity);
        resignSessionContractEntity.setResignSessionContractId(id);
//        resignSessionContractEntity.setContractEntity(contractEntity);
//        resignSessionContractEntity.setResignSessionEntity(resignSessionEntity);

        try {
            log.info(objectMapper.writeValueAsString(resignSessionContractEntity));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        resignSessionContractEntity = resignSessionContractRepository.save(resignSessionContractEntity);
        addLog("UPDATE");
        return convertEntity2Response(resignSessionContractEntity);
    }

    private ResignSessionContractResponse convertEntity2Response(ResignSessionContractEntity entity) {
        ResignSessionContractResponse response = objectMapper.convertValue(entity, ResignSessionContractResponse.class);
        response.contractResponse = objectMapper.convertValue(entity.getContractEntity(), ContractResponse.class);
        response.resignSessionResponse = objectMapper.convertValue(entity.getResignSessionEntity(), ResignSessionResponse.class);
        return response;
    }

    @Override
    public Page<ResignBm07Response> findPageBm07(SearchDTO searchRequest) {
        hstdFilter.unitDepartmentFilter(searchRequest, "contractEntity.unitId");

        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<ResignSessionContractEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<ResignSessionContractEntity> list;
        if (searchRequest.pagedFlag) {
            list = resignSessionContractRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = resignSessionContractRepository.findAll(p);
        }

        return list.map(this::convertEntityToBm07Response);
    }

    public ResignBm07Response convertEntityToBm07Response(ResignSessionContractEntity entity) {
        ResignBm07Response response = objectMapper.convertValue(entity, ResignBm07Response.class);
        Long employeeId = entity.getContractEntity().getEmployeeId();
//        LocalDate finalMonthReview = LocalDate.now();
        LocalDate createdDate = entity.getCreatedAt().toLocalDate();
        EmployeeVhrEntity employeeVhrEntity = employeeVhrRepository.findById(employeeId).orElseThrow(() -> new NotFoundException("Không tìm thấy Id của Nhân viên: " + employeeId));

        response.employeeId = employeeVhrEntity.getEmployeeId();
        response.employeeCode = employeeVhrEntity.getEmployeeCode();
        response.employeeName = employeeVhrEntity.getFullname();
        response.birthYear = employeeVhrEntity.getDateOfBirth().getYear();
        response.gender = employeeVhrEntity.getGender();

        Long positionId = employeeVhrEntity.getPositionId();
        PositionEntity positionEntity = positionRepository.findById(positionId).orElseThrow(() -> new NotFoundException("Không tìm thấy chức danh"));
        response.positionId = positionEntity.getPositionId();
        response.positionName = positionEntity.getPositionName();

        Long organizationId = employeeVhrEntity.getOrganizationId();
        VhrFutureOrganizationEntity organizationEntity = organizationRepository.findById(organizationId).orElseThrow(() -> new NotFoundException("Không tìm thấy đơn vị của người dùng"));

        response.unitName = organizationEntity.getOrgNameLevel2();
        response.departmentName = organizationEntity.getOrgNameLevel3();
        response.trainingLevel = employeeVhrEntity.getTrainingLevel();
        response.trainingSpeciality = employeeVhrEntity.getTrainingSpeciality();

        ContractEntity contractEntity = entity.getContractEntity();
        response.contractEffectiveDate = contractEntity.getEffectiveDate();
        response.contractExpiredDate = contractEntity.getExpiredDate();

        LocalDate firstDayOfFirstReviewMonth = createdDate.minusMonths(12).withDayOfMonth(1);
        LocalDate firstDayOfLastReviewMonth = createdDate.withDayOfMonth(1).minusMonths(1);
        List<EmployeeMonthlyReviewEntity> employeeMonthlyReviewEntities = employeeMonthlyReviewRepository
                .getSpecial(employeeId, firstDayOfFirstReviewMonth, firstDayOfLastReviewMonth);

        response.listMonthlyReview = employeeMonthlyReviewEntities.stream()
                .map(obj -> objectMapper.convertValue(obj, EmployeeMonthlyReviewDTO.EmployeeMonthlyReviewResponse.class))
                .collect(Collectors.toList());
        response.kpiScore = KpiGrade.calculateAverageScore(employeeMonthlyReviewEntities.stream()
                .map(EmployeeMonthlyReviewEntity::getGrade)
                .collect(Collectors.toList()));

        response.resignSessionContractId = entity.getResignSessionContractId();

        return response;
    }

    @Override
    public Page<ResignBm08Response> findPageBm08(SearchDTO searchRequest) {
        hstdFilter.unitDepartmentFilter(searchRequest, "contractEntity.unitId");

        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<ResignSessionContractEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<ResignSessionContractEntity> list;
        if (searchRequest.pagedFlag) {
            list = resignSessionContractRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = resignSessionContractRepository.findAll(p);
        }

        return list.map(this::convertEntityToBm08Response);
    }

    @Override
    public ResignBm08Response convertEntityToBm08Response(ResignSessionContractEntity entity) {
        ResignBm08Response response = objectMapper.convertValue(entity, ResignBm08Response.class);

        EmployeeVhrEntity employeeEntity = employeeVhrRepository
                .findById(entity.getContractEntity().getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy mã nhân viên"));

        response.employeeId = employeeEntity.getEmployeeId();
        response.employeeCode = employeeEntity.getEmployeeCode();
        response.employeeName = employeeEntity.getFullname();
        response.birthYear = employeeEntity.getDateOfBirth().getYear();
        response.gender = employeeEntity.getGender();

        PositionEntity positionEntity = positionRepository.findById(employeeEntity.getPositionId()).orElseThrow(() -> new NotFoundException("Không tìm thấy chức danh"));
        response.positionId = positionEntity.getPositionId();
        response.positionName = positionEntity.getPositionName();

        VhrFutureOrganizationEntity organizationEntity = organizationRepository
                .findById(employeeEntity.getOrganizationId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn vị"));
        String unitIdString = StringUtils.dividedPathIntoArray(organizationEntity.getPath(), VPSConstant.UNIT_ORGANIZATION_LEVEL);
        try {
            assert unitIdString != null;
            response.unitId = Long.parseLong(unitIdString);
        } catch (NumberFormatException ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
        }
        response.unitName = organizationEntity.getOrgNameLevel2();

        ContractEntity contractEntity = entity.contractEntity;

        response.trainingLevel = employeeEntity.getTrainingLevel() == null ? (contractEntity != null ? contractEntity.getTrainingLevel() : null) : null;
        response.trainingSpeciality = employeeEntity.getTrainingSpeciality() == null ? (contractEntity != null ? contractEntity.getTrainingSpeciality() : null) : employeeEntity.getTrainingSpeciality();

        response.contractEffectiveDate = entity.getContractEntity().getEffectiveDate();
        response.contractExpiredDate = entity.getContractEntity().getExpiredDate();
        response.interviewScore = entity.getInterviewScore();
        response.interviewComment = entity.getInterviewComment();
        response.interviewNote = entity.getInterviewNote();

        response.resignSessionContractId = entity.getResignSessionContractId();

        return response;
    }

    @Override
    public Page<ExportBM09Response> findBM09Page(SearchDTO searchRequest) {
        hstdFilter.unitDepartmentFilter(searchRequest, "contractEntity.unitId");

        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<ResignSessionContractEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<ResignSessionContractEntity> list;
        if (searchRequest.pagedFlag) {
            list = resignSessionContractRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = resignSessionContractRepository.findAll(p);
        }

        Page<ExportBM09Response> resignFinalFormResponsePage = list.map(this::convertEntityToBm09Response);
        IntStream.range(0, resignFinalFormResponsePage.getContent().size()).forEach(index -> resignFinalFormResponsePage.getContent().get(index).index = index);

        return resignFinalFormResponsePage;
    }

    @Override
    public Page<ExportBM03Response> findBM03Page(SearchDTO searchRequest) {
        hstdFilter.unitDepartmentFilter(searchRequest, "contractEntity.unitId");

        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<ResignSessionContractEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<ResignSessionContractEntity> list;
        if (searchRequest.pagedFlag) {
            list = resignSessionContractRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = resignSessionContractRepository.findAll(p);
        }

        Page<ExportBM03Response> resignFinalFormResponsePage = list.map(this::convertEntityToBm03Response);
        IntStream.range(0, resignFinalFormResponsePage.getContent().size()).forEach(index -> resignFinalFormResponsePage.getContent().get(index).index = index);

        return resignFinalFormResponsePage;
    }

    @Override
    public FileDTO.FileResponse exportBm9(ExportBMRequest input) {
        ResignSessionEntity resignSessionEntity = resignSessionRepository
                .findById(input.resignSessionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đợt tái ký"));
        List<ResignSessionContractEntity> resignSessionContractEntities = resignSessionEntity.getResignSessionContractEntities().
                stream().filter(item -> item.getPassStatus().equals(ResignPassStatus.NOT_EVALUATE_YET)).collect(Collectors.toList());

        if (!resignSessionContractEntities.isEmpty()) {
            throw new BadRequestException("Có ít nhất một nhân viên chưa được đánh giá,Làm ơn kiểm tra lại");
        }

        List<ExportBM09Response> exportBM09ResponseList = resignSessionEntity
                .getResignSessionContractEntities()
                .stream().map(this::convertEntityToBm09Response)
                .collect(Collectors.toList());
        int stt = 0;
        List<ExportBM09Response> lstResponse = new ArrayList<>();
        for (ExportBM09Response item : exportBM09ResponseList) {
            stt++;
            item.stt = stt;
            lstResponse.add(item);
        }
        String pathStore = folderExtension.getUploadFolder();
        try {
            File file = new ClassPathResource(FileTemplateConstant.BM09_DOCX).getFile();
            String path = file.getPath();
            HashMap<String, List<ExportBM09Response>> tableHashMap = new HashMap<>();
            tableHashMap.put("tbl", lstResponse);
            ExportWord<ExportBM09Response> exportWord = new ExportWord<>();
            String resultPath = "";
            HashMap<String, String> normalField = new HashMap<>();
            normalField.put("quarter", resignSessionEntity.getQuarter() + "");
            normalField.put("year", resignSessionEntity.getYear() + "");
            if (lstResponse.size() > 30) {
                resultPath = exportWord.export(path, pathStore, BM09_OUTPUT_NAME + " quy " + resignSessionEntity.getQuarter() + " nam " + resignSessionEntity.getYear(),
                        normalField, ExportFileExtension.PDF, tableHashMap, "bm09");
            } else {
                resultPath = exportWord.exportV2(path, pathStore, BM09_OUTPUT_NAME + " quy " + resignSessionEntity.getQuarter() + " nam " + resignSessionEntity.getYear(),
                        normalField, ExportFileExtension.PDF, tableHashMap, "bm09");
            }
            file = new File(resultPath);
            String contentType = Files.probeContentType(file.toPath());
            String fileName = file.getName();
            //Luu file vao db

            resignSessionService.updateResignStatus(input.resignSessionId, ResignStatus.CREATED_BM_FILE);
            resignSessionService.updateBMUnitFile(input.resignSessionId, fileService.encodePath(resultPath));

            //Tra file cho client
            byte[] data = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = fileName;
            fileDTO.filePath = fileService.encodePath(resultPath);
            fileDTO.encodePath = fileService.encodePath(resultPath);
            fileDTO.size = file.length();
            fileDTO.type = contentType;
            fileDTO.data = new String(data, StandardCharsets.US_ASCII);
            return fileDTO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ExportBM09Response convertEntityToBm09Response(ResignSessionContractEntity entity) {
        ExportBM09Response response = objectMapper.convertValue(entity, ExportBM09Response.class);

        EmployeeVhrDTO.EmployeeVhrResponse employeeEntity = employeeVhrService.findOneByIdCombineDb(entity.getContractEntity().getEmployeeId());

        response.employeeId = employeeEntity.employeeId;
        response.employeeCode = employeeEntity.employeeCode;
        response.employeeName = employeeEntity.fullname;
        response.birthYear = employeeEntity.dateOfBirth.getYear();
        response.gender = employeeEntity.gender;

        PositionEntity positionEntity = positionRepository.findById(employeeEntity.positionId).orElseThrow(() -> new NotFoundException("Không tìm thấy chức danh"));
        response.positionId = positionEntity.getPositionId();
        response.positionName = positionEntity.getPositionName();

        VhrFutureOrganizationEntity organizationEntity = organizationRepository
                .findById(employeeEntity.organizationId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn vị"));
        String unitIdString = StringUtils.dividedPathIntoArray(organizationEntity.getPath(), VPSConstant.UNIT_ORGANIZATION_LEVEL);
        try {
            assert unitIdString != null;
            response.unitId = Long.parseLong(unitIdString);
        } catch (NumberFormatException ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
        }
        response.unitName = organizationEntity.getOrgNameLevel2();

        ContractEntity contractEntity = entity.contractEntity;

        response.trainingLevel = employeeEntity.trainingLevel == null ? (contractEntity != null ? contractEntity.getTrainingLevel() : null) : null;
        response.trainingSpeciality = employeeEntity.trainingSpeciality == null ? (contractEntity != null ? contractEntity.getTrainingSpeciality() : null) : null;

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        response.contractEffectiveDate = entity.getContractEntity().getEffectiveDate();
        if (entity.getContractEntity().getEffectiveDate() != null) {
            response.contractEffectiveDate_ddmmyyyy = dateTimeFormatter.format(entity.getContractEntity().getEffectiveDate());
        }


        response.contractExpiredDate = entity.getContractEntity().getExpiredDate();
        if (entity.getContractEntity().getExpiredDate() != null) {
            response.contractExpiredDate_ddmmyyyy = dateTimeFormatter.format(entity.getContractEntity().getExpiredDate());
        }


        response.kpiScore = employeeMonthlyReviewRepository
                .getAverageScore(employeeEntity.employeeId, LocalDate.now().withDayOfMonth(1))
                .orElseThrow(() -> new NotFoundException("Không lấy được đánh giá hàng tháng của nhân viên: " + employeeEntity.employeeCode));
        response.interviewScore = entity.getInterviewScore() == null ? 0 : entity.getInterviewScore();
        response.totalScore = response.kpiScore + response.interviewScore;
        response.attitude = entity.getAttitude();
        response.passStatus = entity.getPassStatus();
        response.contractDuration = entity.getContractDuration();
        response.newContractEffectiveDate = entity.getNewContractEffectiveDate();
        if (entity.getNewContractEffectiveDate() != null) {
            response.newContractStartDate_ddmmyyyy = dateTimeFormatter.format(entity.getNewContractEffectiveDate());
        } else {
            response.newContractStartDate_ddmmyyyy = "";
        }
        response.newContractExpiredDate = entity.getNewContractExpiredDate();
        if (entity.getNewContractExpiredDate() != null) {
            response.newContractEndDate_ddmmyyyy = dateTimeFormatter.format(entity.getNewContractExpiredDate());
        } else {
            response.newContractEndDate_ddmmyyyy = "";
        }
        response.resignNote = entity.getResignNote();
        response.resignSessionStatus = entity.getResignSessionEntity().getStatus();

        response.resignSessionContractId = entity.getResignSessionContractId();

        response.genderVietnamese = response.gender.getVietnameseStringValue();
        response.passStatusVietnamese = response.passStatus.getVietnameseStringValue();
        response.attitudeVietnamese = response.attitude.getVietnameseStringValue();
        response.contractDurationVietnamese = response.passStatus == ResignPassStatus.PASS ? response.contractDuration.getVietnameseStringValue() : null;

        return response;
    }

    @Override
    public FileDTO.FileResponse exportBm3(ExportBMRequest input) {
        ResignSessionEntity resignSessionEntity = resignSessionRepository
                .findById(input.resignSessionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đợt tái ký"));

        List<ResignSessionContractEntity> resignSessionContractEntities = resignSessionEntity.getResignSessionContractEntities().
                stream().filter(item -> item.getPassStatus().equals(ResignPassStatus.NOT_EVALUATE_YET)).collect(Collectors.toList());

        if (!resignSessionContractEntities.isEmpty()) {
            throw new BadRequestException("Có ít nhất một nhân viên chưa được đánh giá, Làm ơn kiểm tra lại");
        }

        List<ExportBM03Response> resignFinalFormResponseList = resignSessionEntity
                .getResignSessionContractEntities()
                .stream().map(this::convertEntityToBm03Response)
                .collect(Collectors.toList());
        int stt = 0;
        List<ExportBM03Response> lstResponse = new ArrayList<>();
        for (ExportBM03Response item : resignFinalFormResponseList) {
            stt++;
            item.stt = stt;
            lstResponse.add(item);
        }
        String pathStore = folderExtension.getUploadFolder();
        try {
            File file = new ClassPathResource(FileTemplateConstant.BM03_DOCX).getFile();
            String path = file.getPath();
            HashMap<String, List<ExportBM03Response>> tableHashMap = new HashMap<>();
            tableHashMap.put("tbl", lstResponse);
            ExportWord<ExportBM03Response> exportWord = new ExportWord<>();
            String resultPath = "";
            HashMap<String, String> normalField = new HashMap<>();
            normalField.put("startDate", resignSessionEntity.getStartDate() + "");
            normalField.put("endDate", resignSessionEntity.getEndDate() + "");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
            if (lstResponse.size() > 30) {
                resultPath = exportWord.export(path, pathStore, BM03_OUTPUT_NAME + " tu " + formatter.format(resignSessionEntity.getStartDate()) + " den " + formatter.format(resignSessionEntity.getEndDate()),
                        normalField, ExportFileExtension.PDF, tableHashMap, SIGNAL_BM03);
            } else {
                resultPath = exportWord.exportV2(path, pathStore, BM03_OUTPUT_NAME + " tu " + formatter.format(resignSessionEntity.getStartDate()) + " den " + formatter.format(resignSessionEntity.getEndDate()),
                        normalField, ExportFileExtension.PDF, tableHashMap, SIGNAL_BM03);
            }
            file = new File(resultPath);
            String contentType = Files.probeContentType(file.toPath());
            String fileName = file.getName();
            //Luu file vao db

            resignSessionService.updateResignStatus(input.resignSessionId, ResignStatus.CREATED_BM_FILE);
            resignSessionService.updateBMUnitFile(input.resignSessionId, fileService.encodePath(resultPath));

            //Tra file cho client
            byte[] data = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = fileName;
            fileDTO.filePath = fileService.encodePath(resultPath);
            fileDTO.encodePath = fileService.encodePath(resultPath);
            fileDTO.size = file.length();
            fileDTO.type = contentType;
            fileDTO.data = new String(data, StandardCharsets.US_ASCII);
            return fileDTO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ExportBM03Response convertEntityToBm03Response(ResignSessionContractEntity entity) {
        ExportBM03Response response = objectMapper.convertValue(entity, ExportBM03Response.class);

        EmployeeVhrDTO.EmployeeVhrResponse employeeEntity = employeeVhrService.findOneByIdCombineDb(entity.getContractEntity().getEmployeeId());

        response.employeeId = employeeEntity.employeeId;
        response.employeeCode = employeeEntity.employeeCode;
        response.employeeName = employeeEntity.fullname;
        response.birthYear = employeeEntity.dateOfBirth.getYear();
        response.gender = employeeEntity.gender;
        response.genderVietnamese = response.gender.getVietnameseStringValue();

        PositionEntity positionEntity = positionRepository.findById(employeeEntity.positionId).orElseThrow(() -> new NotFoundException("Không tìm thấy chức danh"));
        response.positionId = positionEntity.getPositionId();
        response.positionName = positionEntity.getPositionName();

//        VhrFutureOrganizationEntity organizationEntity = organizationRepository
//                .findById(employeeEntity.organizationId)
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn vị"));
//        String unitIdString = StringUtils.dividedPathIntoArray(organizationEntity.getPath(), VPSConstant.UNIT_ORGANIZATION_LEVEL);
        VhrFutureOrganizationDTO.DepartmentUnitResponse departmentUnitResponse = organizationService.getOnlyUnitFromOrganization(employeeEntity.organizationId);
        response.unitId = departmentUnitResponse.unitId;
        response.unitName = departmentUnitResponse.unitName;

        ContractEntity contractEntity = entity.contractEntity;
        response.trainingLevel = employeeEntity.trainingLevel == null ? (contractEntity != null ? contractEntity.getTrainingLevel() : null) : null;
        response.trainingSpeciality = employeeEntity.trainingSpeciality == null ? (contractEntity != null ? contractEntity.getTrainingSpeciality() : null) : null;

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        response.contractEffectiveDate = entity.getContractEntity().getEffectiveDate();
        if (entity.getContractEntity().getEffectiveDate() != null) {
            response.contractEffectiveDate_ddmmyyyy = dateTimeFormatter.format(entity.getContractEntity().getEffectiveDate());

        }
        response.contractExpiredDate = entity.getContractEntity().getExpiredDate();
        if (entity.getContractEntity().getExpiredDate() != null) {
            response.contractExpiredDate_ddmmyyyy = dateTimeFormatter.format(entity.getContractEntity().getExpiredDate());
        }

        response.reportScore = entity.getReportScore();
        response.specialityScore = entity.getSpecialityScore();
        response.attitudeScore = entity.getAttitudeScore();
        response.interviewScore = entity.getInterviewScore() == null ? 0 : entity.getInterviewScore();
        response.totalScore = response.reportScore + response.specialityScore + response.attitudeScore + response.interviewScore;
        response.passStatus = entity.getPassStatus();
        response.passStatusVietnamese = response.passStatus.getVietnameseStringValue();
        response.contractDuration = entity.getContractDuration();
        response.contractDurationVietnamese = response.contractDuration.getVietnameseStringValue();
        response.resignNote = entity.getResignNote();
        response.resignSessionStatus = entity.getResignSessionEntity().getStatus();

        response.resignSessionContractId = entity.getResignSessionContractId();

        return response;
    }

    private List<FileDTO.FileResponse> exportBm9Tct(List<ResignSessionEntity> resignSessionEntityList) {
        int quarter = 1;
        int year = 2021;
        if (resignSessionEntityList.size() > 0) {
            quarter = resignSessionEntityList.get(0).getQuarter();
            year = resignSessionEntityList.get(0).getYear();
        }
        List<ResignSessionContractEntity> resignSessionContractEntityList = new ArrayList<>();

        resignSessionEntityList.forEach(obj -> {
            resignSessionContractEntityList.addAll(obj.getResignSessionContractEntities());
        });

        List<ResignSessionContractEntity> resignSessionContractEntities = resignSessionContractEntityList.stream()
                .filter(item -> item.getPassStatus().equals(ResignPassStatus.NOT_EVALUATE_YET)).collect(Collectors.toList());

        if (!resignSessionContractEntities.isEmpty()) {
            throw new BadRequestException("Có ít nhất một nhân viên thuộc 1  đơn vị chưa được đánh giá,Làm ơn kiểm tra lại");
        }

        List<ExportBM09Response> exportBM09ResponseList = resignSessionContractEntityList
                .stream().map(this::convertEntityToBm09Response)
                .collect(Collectors.toList());
        int stt = 0;
        List<ExportBM09Response> lstResponse = new ArrayList<>();
        for (ExportBM09Response item : exportBM09ResponseList) {
            stt++;
            item.stt = stt;
            lstResponse.add(item);
        }
        String pathStore = folderExtension.getUploadFolder();
        try {
            File pdfFile = new ClassPathResource(FileTemplateConstant.BM09TCT_DOCX).getFile();
            String path = pdfFile.getPath();
            HashMap<String, List<ExportBM09Response>> tableHashMap = new HashMap<>();
            tableHashMap.put("tbl", lstResponse);
            ExportWord<ExportBM09Response> exportWord = new ExportWord<>();
            String pdfResultPath = "";
            String docxResultPath = "";
            HashMap<String, String> normalField = new HashMap<>();
            normalField.put("quarter", quarter + "");
            normalField.put("year", year + "");
            if (lstResponse.size() > 30) {
                pdfResultPath = exportWord.export(path, pathStore, BM09_TCT_OUTPUT_NAME,
                        normalField, ExportFileExtension.PDF, tableHashMap, SIGNAL_BM09_TCT);
            } else {
                pdfResultPath = exportWord.exportV2(path, pathStore, BM09_TCT_OUTPUT_NAME,
                        normalField, ExportFileExtension.PDF, tableHashMap, SIGNAL_BM09_TCT);
            }
            docxResultPath = pdfResultPath.replace(".pdf", ".docx");
            pdfFile = new File(pdfResultPath);
            File docxFile = new File(docxResultPath);
            String pdfContentType = Files.probeContentType(pdfFile.toPath());
            String docxContentType = Files.probeContentType(docxFile.toPath());
            String pdfFileName = pdfFile.getName();
            String docxFileName = docxFile.getName();

            //Luu file vao db
            Set<Long> resignIdSet = resignSessionEntityList.stream().map(ResignSessionEntity::getResignSessionId).collect(Collectors.toSet());

            resignSessionRepository.updateResignStatusByResignIdSet(resignIdSet, ResignStatus.HR_TCT_CREATED_BMTCT_FILE);

            resignSessionRepository.updateBMTCTFilePathByResignIdSet(
                    resignIdSet,
                    fileService.encodePath(pdfResultPath),
                    fileService.encodePath(docxResultPath)
            );

            //Tra file cho client
            List<FileDTO.FileResponse> fileList = new ArrayList<>();

            byte[] pdfData = Base64.getEncoder().encode(Files.readAllBytes(pdfFile.toPath()));
            FileDTO.FileResponse pdfFileDTO = new FileDTO.FileResponse();
            pdfFileDTO.fileName = pdfFileName;
            pdfFileDTO.filePath = fileService.encodePath(pdfResultPath);
            pdfFileDTO.size = pdfFile.length();
            pdfFileDTO.type = pdfContentType;
            pdfFileDTO.data = new String(pdfData, StandardCharsets.US_ASCII);
            fileList.add(pdfFileDTO);

            byte[] docxData = Base64.getEncoder().encode(Files.readAllBytes(docxFile.toPath()));
            FileDTO.FileResponse docxFileDTO = new FileDTO.FileResponse();
            docxFileDTO.fileName = docxFileName;
            docxFileDTO.filePath = fileService.encodePath(docxResultPath);
            docxFileDTO.size = docxFile.length();
            docxFileDTO.type = docxContentType;
            docxFileDTO.data = new String(docxData, StandardCharsets.US_ASCII);
            fileList.add(docxFileDTO);

            return fileList;
        } catch (IOException e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            log.error(stackTrace);
        }
        return new ArrayList<>();
    }

    private List<FileDTO.FileResponse> exportBM3TCT(List<ResignSessionEntity> resignSessionEntityList) {
        LocalDate startDate = resignSessionEntityList.get(0).getStartDate();
        LocalDate endDate = resignSessionEntityList.get(0).getEndDate();

        List<ResignSessionContractEntity> resignSessionContractEntityList = new ArrayList<>();

        resignSessionEntityList.forEach(obj -> resignSessionContractEntityList.addAll(obj.getResignSessionContractEntities()));

        List<ResignSessionContractEntity> resignSessionContractEntities = resignSessionContractEntityList.stream()
                .filter(item -> item.getPassStatus().equals(ResignPassStatus.NOT_EVALUATE_YET)).collect(Collectors.toList());

        if (!resignSessionContractEntities.isEmpty()) {
            throw new BadRequestException("Có ít nhất một nhân viên thuộc 1  đơn vị chưa được đánh giá,Làm ơn kiểm tra lại");
        }

        List<ExportBM03Response> resignFinalFormResponseList = resignSessionContractEntityList
                .stream().map(this::convertEntityToBm03Response)
                .collect(Collectors.toList());
        int stt = 0;
        List<ExportBM03Response> lstResponse = new ArrayList<>();
        for (ExportBM03Response item : resignFinalFormResponseList) {
            stt++;
            item.stt = stt;
            lstResponse.add(item);
        }
        String pathStore = folderExtension.getUploadFolder();
        try {
            File pdfFile = new ClassPathResource(FileTemplateConstant.BM03TCT_DOCX).getFile();
            String path = pdfFile.getPath();
            HashMap<String, List<ExportBM03Response>> tableHashMap = new HashMap<>();
            tableHashMap.put("tbl", lstResponse);
            ExportWord<ExportBM03Response> exportWord = new ExportWord<>();
            String pdfResultPath = "";
            String docxResultPath = "";
            HashMap<String, String> normalField = new HashMap<>();
            normalField.put("startDate", startDate + "");
            normalField.put("endDate", endDate + "");
            if (lstResponse.size() > 30) {
                pdfResultPath = exportWord.export(path, pathStore, BM03_TCT_OUTPUT_NAME,
                        normalField, ExportFileExtension.PDF, tableHashMap, "bm03TCT");
            } else {
                pdfResultPath = exportWord.exportV2(path, pathStore, BM03_TCT_OUTPUT_NAME,
                        normalField, ExportFileExtension.PDF, tableHashMap, "bm03TCT");
            }
            docxResultPath = pdfResultPath.replace(".pdf", ".docx");
            pdfFile = new File(pdfResultPath);
            File docxFile = new File(docxResultPath);
            String pdfContentType = Files.probeContentType(pdfFile.toPath());
            String docxContentType = Files.probeContentType(docxFile.toPath());
            String pdfFileName = pdfFile.getName();
            String docxFileName = docxFile.getName();

            //Luu file vao db
            Set<Long> resignIdSet = resignSessionEntityList.stream().map(ResignSessionEntity::getResignSessionId).collect(Collectors.toSet());

            resignSessionRepository.updateBMTCTFilePathByResignIdSet(
                    resignIdSet,
                    fileService.encodePath(pdfResultPath),
                    fileService.encodePath(docxResultPath)
            );

            //Tra file cho client
            List<FileDTO.FileResponse> fileList = new ArrayList<>();

            byte[] pdfData = Base64.getEncoder().encode(Files.readAllBytes(pdfFile.toPath()));
            FileDTO.FileResponse pdfFileDTO = new FileDTO.FileResponse();
            pdfFileDTO.fileName = pdfFileName;
            pdfFileDTO.filePath = fileService.encodePath(pdfResultPath);
            pdfFileDTO.size = pdfFile.length();
            pdfFileDTO.type = pdfContentType;
            pdfFileDTO.data = new String(pdfData, StandardCharsets.US_ASCII);
            Map<String, String> pdfAdditionalDateMap = new HashMap<>();
            pdfAdditionalDateMap.put(BM_TCT_ADDITIONAL_FIELD, BM_TCT_TYPE_CODE_BM_TCT_PDF);
            pdfFileDTO.additionalDataMap = pdfAdditionalDateMap;
            fileList.add(pdfFileDTO);

            byte[] docxData = Base64.getEncoder().encode(Files.readAllBytes(docxFile.toPath()));
            FileDTO.FileResponse docxFileDTO = new FileDTO.FileResponse();
            docxFileDTO.fileName = docxFileName;
            docxFileDTO.filePath = fileService.encodePath(docxResultPath);
            docxFileDTO.size = docxFile.length();
            docxFileDTO.type = docxContentType;
            docxFileDTO.data = new String(docxData, StandardCharsets.US_ASCII);
            Map<String, String> docxAdditionalDateMap = new HashMap<>();
            docxAdditionalDateMap.put(BM_TCT_ADDITIONAL_FIELD, BM_TCT_TYPE_CODE_BM_TCT_DOCX);
            docxFileDTO.additionalDataMap = docxAdditionalDateMap;
            fileList.add(docxFileDTO);

            return fileList;
        } catch (IOException e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            log.error(stackTrace);
        }
        return new ArrayList<>();
    }

    private FileDTO.FileResponse exportUnitBM(List<ResignSessionEntity> resignSessionEntityList) {
        try {
            List<String> bmPathSet = resignSessionEntityList.stream().map(resignEntity -> fileService.decodePath(resignEntity.getBm09EncodePath()))
                    .collect(Collectors.toList());
            String uploadFolder = fileService.getUploadFolder();
            String filePrefix = fileService.getFilePrefix();
            String fileOutput = uploadFolder + filePrefix + "Bao cao don vi.pdf";
            pdfUtil.mergePdf(bmPathSet, fileOutput);

            File file = new File(fileOutput);
            if (!file.exists()) throw new NotFoundException("Có vấn đề khi xuất file Báo cáo đơn vị");

            Set<Long> resignIdSet = resignSessionEntityList.stream().map(ResignSessionEntity::getResignSessionId).collect(Collectors.toSet());

            resignSessionRepository.updateBmListEncodePathByResignIdSet(
                    resignIdSet,
                    fileService.encodePath(fileOutput)
            );

            byte[] docxData = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));

            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = file.getName();
            fileDTO.filePath = fileService.encodePath(fileOutput);
            fileDTO.size = file.length();
            fileDTO.type = Files.probeContentType(file.toPath());
            fileDTO.data = new String(docxData, StandardCharsets.US_ASCII);
            fileDTO.downloadFileExtension = ".pdf";
            Map<String, String> additionalDateMap = new HashMap<>();
            additionalDateMap.put(BM_TCT_ADDITIONAL_FIELD, BM_TCT_TYPE_CODE_BM_UNIT_COLLECTION);
            fileDTO.additionalDataMap = additionalDateMap;
            return fileDTO;
        } catch (IOException e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            log.error(stackTrace);
        }

        return new FileDTO.FileResponse();
    }

    // Xuất file báo cáo hợp đồng lao động hết hạn
    private FileDTO.FileResponse exportLaborResignReport(List<ResignSessionEntity> resignSessionEntityList) {
        try {
            int quarter = resignSessionEntityList.get(0).getQuarter();
            int year = resignSessionEntityList.get(0).getYear();
            LaborResignReportResponse response = getLaborResignReportResponse(resignSessionEntityList);
            Map<String, String> map = CustomMapper.convert(response);

            HashMap<String, String> hm = new HashMap<>();
            for (String key : map.keySet()) {
                if (map.get(key) != null) {
                    hm.put(key, map.get(key).toString());
                }
            }
            String pathStore = folderExtension.getUploadFolder();
            File file = new ClassPathResource(FileTemplateConstant.LABOR_RESIGN_REPORT_DOCX).getFile();
            String path = file.getPath();
            ExportWord<LaborResignReportResponse> exportWord = new ExportWord<>();
            String realPath = exportWord.export(path, pathStore, "Bao cao danh gia het han HDLD quy " + quarter + " nam " + year,
                    hm, ExportFileExtension.PDF, null, SIGNAL_BM09_TCT);
            file = new File(realPath);
            String docxContentType = Files.probeContentType(file.toPath());

            // Handle db
            Set<Long> resignIdSet = resignSessionEntityList.stream().map(ResignSessionEntity::getResignSessionId).collect(Collectors.toSet());

            resignSessionRepository.updateReportPathByResignIdSet(
                    resignIdSet,
                    fileService.encodePath(realPath)
            );

            byte[] data = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = file.getName();
            fileDTO.filePath = fileService.encodePath(realPath);
            fileDTO.encodePath = fileService.encodePath(realPath);
            fileDTO.size = file.length();
            fileDTO.type = docxContentType;
            fileDTO.data = new String(data, StandardCharsets.US_ASCII);
            return fileDTO;
        } catch (Exception ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
        }
        return new FileDTO.FileResponse();
    }

    private LaborResignReportResponse getLaborResignReportResponse(List<ResignSessionEntity> resignSessionEntityList) {
        LaborResignReportResponse response = new LaborResignReportResponse();

        response.quarter = resignSessionEntityList.get(0).getQuarter();
        response.year = resignSessionEntityList.get(0).getYear();

        resignSessionEntityList.forEach(resignEntity -> {
            response.totalAboutToExpiredContract += resignEntity.getResignSessionContractEntities().size();
            resignEntity.getResignSessionContractEntities().forEach(resignContractEntity -> {
                if (resignContractEntity.getPassStatus().equals(ResignPassStatus.PASS)) {
                    response.passResignContract += 1;
                    if (resignContractEntity.getContractDuration().equals(ContractDuration.TWO_YEAR)) {
                        response.twoYearContract += 1;
                    } else if (resignContractEntity.getContractDuration().equals(ContractDuration.PERMANENCE)) {
                        response.infiniteContract += 1;
                    }

                } else {
                    response.failResignContract += 1;
                }
            });
        });

        response.percentPassResignContract = (float) response.passResignContract * 100 / response.totalAboutToExpiredContract;
        response.percentFailResignContract = (float) response.failResignContract * 100 / response.totalAboutToExpiredContract;

        return response;
    }

    // Xuất file báo cáo hợp đồng lao động hết hạn
    private FileDTO.FileResponse exportProbationaryResignReportResponse(List<ResignSessionEntity> resignSessionEntityList) {
        try {
            LocalDate startDate = resignSessionEntityList.get(0).getStartDate();
            LocalDate endDate = resignSessionEntityList.get(0).getEndDate();
            ProbationaryResignReportResponse response = getProbationaryResignReportResponse(resignSessionEntityList);
            Map<String, String> map = CustomMapper.convert(response);


            HashMap<String, String> hm = new HashMap<>();
            for (String key : map.keySet()) {
                if (map.get(key) != null) {
                    hm.put(key, map.get(key).toString());
                }
            }
            String pathStore = folderExtension.getUploadFolder();
            File file = new ClassPathResource(FileTemplateConstant.PROBATIONARY_RESIGN_REPORT_DOCX).getFile();
            String path = file.getPath();
            ExportWord<ProbationaryResignReportResponse> exportWord = new ExportWord<>();
            DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
            String realPath = exportWord.export(path, pathStore, "Bao cao danh gia het han HDTV tu " + fileNameFormatter.format(startDate) + " den " + fileNameFormatter.format(endDate),
                    hm, ExportFileExtension.PDF, null, SIGNAL_BM03_TCT);
            file = new File(realPath);
            String contentType = Files.probeContentType(file.toPath());

            // Handle db
            Set<Long> resignIdSet = resignSessionEntityList.stream().map(ResignSessionEntity::getResignSessionId).collect(Collectors.toSet());

            resignSessionRepository.updateReportPathByResignIdSet(
                    resignIdSet,
                    fileService.encodePath(fileService.encodePath(realPath))
            );

            byte[] data = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
            FileDTO.FileResponse fileDTO = new FileDTO.FileResponse();
            fileDTO.fileName = file.getName();
            fileDTO.filePath = fileService.encodePath(realPath);
            fileDTO.encodePath = fileService.encodePath(realPath);
            fileDTO.size = file.length();
            fileDTO.type = contentType;
            fileDTO.data = new String(data, StandardCharsets.US_ASCII);
            Map<String, String> additionalDateMap = new HashMap<>();
            additionalDateMap.put(BM_TCT_ADDITIONAL_FIELD, BM_TCT_TYPE_CODE_BM_UNIT_COLLECTION);
            fileDTO.additionalDataMap = additionalDateMap;
            return fileDTO;
        } catch (Exception ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            log.error(stackTrace);
        }
        return new FileDTO.FileResponse();
    }

    private ProbationaryResignReportResponse getProbationaryResignReportResponse(List<ResignSessionEntity> resignSessionEntityList) {
        ProbationaryResignReportResponse response = new ProbationaryResignReportResponse();

        response.startDate = resignSessionEntityList.get(0).getStartDate();
        response.endDate = resignSessionEntityList.get(0).getEndDate();

        resignSessionEntityList.forEach(resignEntity -> {
            response.totalAboutToExpiredContract += resignEntity.getResignSessionContractEntities().size();
            resignEntity.getResignSessionContractEntities().forEach(resignContractEntity -> {
                if (resignContractEntity.getPassStatus().equals(ResignPassStatus.PASS)) {
                    response.passResignContract += 1;
                } else if (resignContractEntity.getPassStatus().equals(ResignPassStatus.FAIL)) {
                    response.failResignContract += 1;
                } else {
                    response.notEvaluateResignContract += 1;
                }
            });
        });

        response.percentNotEvaluateResignContract = (float) response.notEvaluateResignContract * 100 / response.totalAboutToExpiredContract;
        response.percentPassResignContract = (float) response.passResignContract * 100 / response.totalAboutToExpiredContract;
        response.percentFailResignContract = (float) response.failResignContract * 100 / response.totalAboutToExpiredContract;

        return response;
    }

    @Override
    public List<FileDTO.FileResponse> exportFilesForLaborVoffice2(ResignContractAddToVofficeLaborRequest request) {
        List<FileDTO.FileResponse> fileResponseList = new ArrayList<>();
        Set<ResignStatus> resignStatusSet = new HashSet<>();
        resignStatusSet.add(ResignStatus.RECEIVED_BM_09_VOFFICE_AND_SUCCESS);
        resignStatusSet.add(ResignStatus.HR_TCT_ADD_EMPLOYEE_TO_VOFFICE2);
        resignStatusSet.add(ResignStatus.HR_TCT_CREATED_BMTCT_FILE);

        List<ResignSessionEntity> resignSessionEntityList = resignSessionRepository.getAllResignByStartEndDateResignStatusAndType(
                request.quarter,
                request.year,
                resignStatusSet,
                ResignType.LABOR);

        if (resignSessionEntityList.size() == 0) {
            throw new BadRequestException("Không tìm thấy đợt tái ký hoặc đã trình tái ký của quý " + request.quarter + " năm " + request.year);
        }

        fileResponseList.add(exportLaborResignReport(resignSessionEntityList));
        log.info("Done exportLaborResignReport");
        fileResponseList.addAll(exportBm9Tct(resignSessionEntityList));
        log.info("Done exportBM9TCT");
        fileResponseList.add(exportUnitBM(resignSessionEntityList));
        log.info("Done exportUnitBM");


        Set<Long> resignIdSet = resignSessionEntityList.stream().map(ResignSessionEntity::getResignSessionId).collect(Collectors.toSet());
        resignSessionContractRepository.updateResignStatusByResignIdSet(
                resignIdSet,
                ResignStatus.HR_TCT_CREATED_BMTCT_FILE);

        return fileResponseList;
    }

    @Override
    public List<FileDTO.FileResponse> exportFilesForProbationaryVoffice2(ResignContractAddToVofficeProbationaryRequest request) {
        List<FileDTO.FileResponse> fileResponseList = new ArrayList<>();
        Set<ResignStatus> resignStatusSet = new HashSet<>();
        resignStatusSet.add(ResignStatus.RECEIVED_BM_09_VOFFICE_AND_SUCCESS);
        resignStatusSet.add(ResignStatus.HR_TCT_ADD_EMPLOYEE_TO_VOFFICE2);
        resignStatusSet.add(ResignStatus.HR_TCT_CREATED_BMTCT_FILE);

        resignStatusSet.add(ResignStatus.TEMP_CONTRACT_CREATE);

        List<ResignSessionEntity> resignSessionEntityList = resignSessionRepository.getAllResignByStartEndDateResignStatusAndType(
                request.startDate,
                request.endDate,
                resignStatusSet,
                ResignType.PROBATIONARY);
        if (resignSessionEntityList.size() == 0) {
            throw new BadRequestException("Không tìm thấy đợt tái ký hoặc đã trình tái ký từ " + request.startDate + " đến " + request.endDate);
        }

        fileResponseList.add(exportProbationaryResignReportResponse(resignSessionEntityList));
        fileResponseList.addAll(exportBM3TCT(resignSessionEntityList));
        fileResponseList.add(exportUnitBM(resignSessionEntityList));

        Set<Long> resignIdSet = resignSessionEntityList.stream().map(ResignSessionEntity::getResignSessionId).collect(Collectors.toSet());
        resignSessionService.updateResignStatusAndRelated(resignIdSet, ResignStatus.HR_TCT_CREATED_BMTCT_FILE);


        return fileResponseList;
    }
}
