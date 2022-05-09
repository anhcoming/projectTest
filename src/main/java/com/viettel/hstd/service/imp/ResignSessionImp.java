package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.viettel.hstd.constant.*;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ContractDTO.*;
import com.viettel.hstd.dto.hstd.EmployeeMonthlyReviewDTO;
import com.viettel.hstd.dto.hstd.ResignSessionContractDTO.*;
import com.viettel.hstd.dto.hstd.ResignSessionDTO.*;
import com.viettel.hstd.dto.vps.VhrFutureOrganizationDTO;
import com.viettel.hstd.entity.hstd.*;
import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.filter.HSTDFilter;
import com.viettel.hstd.repository.hstd.ContractRepository;
import com.viettel.hstd.repository.hstd.LaborContractRepository;
//import com.viettel.hstd.repository.hstd.ResignSessionEmployeeRepository;
import com.viettel.hstd.repository.hstd.ResignSessionContractRepository;
import com.viettel.hstd.repository.hstd.ResignSessionRepository;
import com.viettel.hstd.repository.vps.DomainDataRepository;
import com.viettel.hstd.repository.vps.EmployeeVhrRepository;
import com.viettel.hstd.repository.vps.PositionRepository;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.service.inf.*;
import com.viettel.hstd.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tomcat.jni.Local;
import org.modelmapper.ModelMapper;
//import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.viettel.hstd.constant.HSDTConstant.BM07_PATH;
import static com.viettel.hstd.constant.HSDTConstant.BM08_PATH;

@Service
@Slf4j
public class ResignSessionImp extends BaseService implements ResignSessionService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private Message message;

    @Autowired
    private ResignSessionRepository resignSessionRepository;

    @Autowired
    private EmployeeVhrRepository employeeVhrRepository;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private HSTDFilter hstdFilter;

    @Autowired
    private LaborContractRepository laborContractRepository;

    @Autowired
    private LaborContractService laborContractService;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private DomainDataRepository domainDataRepository;

    @Autowired
    private VhrFutureOrganizationRepository organizationRepository;

    @Autowired
    private VhrFutureOrganizationService organizationService;

    @Autowired
    private ResignSessionContractRepository resignSessionContractRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private MapUtils mapUtils;

    @Autowired
    private ResignSessionContractService resignSessionContractService;

    @Autowired
    private ExcelUtil excelUtil;

//    @Autowired
//    private Scheduler scheduler;

    @Override
    public Page<ResignSessionResponse> findPage(SearchDTO searchRequest) {
        hstdFilter.unitDepartmentFilter(searchRequest, "unitId");

        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<ResignSessionEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<ResignSessionEntity> list;
        if (searchRequest.pagedFlag) {
            list = resignSessionRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = resignSessionRepository.findAll(p);
        }

        return list.map(this::convertEntity2Response);
    }

    @Override
    public ResignSessionResponse findOneById(Long id) {
        ResignSessionEntity resignSessionEntity = resignSessionRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));

        return convertEntity2Response(resignSessionEntity);
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        ResignSessionEntity resignSessionEntity = resignSessionRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy đợt tái ký"));
        Set<Long> contractIdSet = resignSessionEntity.getResignSessionContractEntities().stream().map(resignContract -> resignContract.getContractEntity().getContractId()).collect(Collectors.toSet());
        contractService.updateResignStatus(contractIdSet, ResignStatus.NOT_IN_RESIGN_SESSION);

        List<Long> resignSessionContractIds = resignSessionEntity.getResignSessionContractEntities().stream().map(ResignSessionContractEntity::getResignSessionContractId).collect(Collectors.toList());
        resignSessionContractRepository.softDeleteAll(resignSessionContractIds);

        resignSessionEntity.getResignSessionContractEntities().clear();
        resignSessionRepository.save(resignSessionEntity);
        resignSessionRepository.delete(resignSessionEntity);
        addLog("RESIGN_SESSION", "DELETE", id.toString());
        return true;
    }

    @Override
    public ResignSessionResponse create(ResignSessionRequest request) {
        ResignSessionEntity resignSessionEntity = objectMapper.convertValue(request, ResignSessionEntity.class);

        if (request.resignType.equals(ResignType.LABOR)) {
            int quarter = request.quarter;
            int year = request.year;
            LocalDate startDate = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
            LocalDate endDate = startDate.plusMonths(3).minusDays(1);
            request.startDate = startDate;
            request.endDate = endDate;
        }

        resignSessionRepository.save(resignSessionEntity);
        addLog("RESIGN_SESSION", "CREATE", new Gson().toJson(request));
        return convertEntity2Response(resignSessionEntity);
    }

    @Override
    @Transactional
    public ResignSessionResponse update(Long id, ResignSessionRequest request) {
        ResignSessionEntity resignSessionEntity = resignSessionRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));

        if (resignSessionEntity.getResignStatus().getValue() > ResignStatus.IN_EVALUATION.getValue()) {
            throw new BadRequestException("Đợt tái ký đang được thực hiện nên không thể sửa thông tin");
        }

        ResignSessionEntity newResignSessionEntity = objectMapper.convertValue(request, ResignSessionEntity.class);
        mapUtils.customMap(newResignSessionEntity, resignSessionEntity);
        resignSessionEntity.setResignSessionId(id);

        List<ResignSessionContractEntity> commonResignSessionContractEntities = new ArrayList<>();
        List<ResignSessionContractEntity> need2BeDeletedResignSessionContractEntities = new ArrayList<>();
        List<ResignSessionContractEntity> need2BeAddedResignSessionContractEntities = new ArrayList<>();

        resignSessionEntity.getResignSessionContractEntities().forEach(bridgeEntity -> {
            if (request.contractIds.contains(bridgeEntity.contractEntity.getContractId())) {
                commonResignSessionContractEntities.add(bridgeEntity);
            } else {
                need2BeDeletedResignSessionContractEntities.add(bridgeEntity);
            }
        });

        List<Long> resignSessionContractIds = need2BeDeletedResignSessionContractEntities.stream().
                map(ResignSessionContractEntity::getResignSessionContractId).collect(Collectors.toList());

        List<Long> commonContractId = commonResignSessionContractEntities
                .stream()
                .map(obj -> obj.getContractEntity().getContractId())
                .collect(Collectors.toList());

        ResignSessionEntity finalResignSessionEntity = resignSessionEntity;
        need2BeAddedResignSessionContractEntities = request.contractIds.stream().filter(obj -> !commonContractId.contains(obj)).map(contractId -> {
            ResignSessionContractEntity entity = new ResignSessionContractEntity();

            ContractEntity contractEntity = contractRepository.findById(contractId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy Hợp đồng"));
            contractEntity.setResignStatus(ResignStatus.IN_EVALUATION);
            entity.setContractEntity(contractEntity);
            if (contractEntity.getContractType().equals(ContractType.PROBATIONARY_CONTRACT)) {
                entity.setContractDuration(ContractDuration.ONE_YEAR);
            } else if (contractEntity.getContractType().equals(ContractType.LABOR_CONTRACT)) {
                if (contractEntity.getContractDuration().equals(ContractDuration.ONE_YEAR)) {
                    entity.setContractDuration(ContractDuration.TWO_YEAR);
                } else {
                    entity.setContractDuration(ContractDuration.PERMANENCE);
                }
            }
            entity.setNewContractEffectiveDate(contractEntity.getExpiredDate().plusDays(1));
            if (!entity.getContractDuration().equals(ContractDuration.PERMANENCE)) {
                entity.setNewContractExpiredDate(contractEntity.getExpiredDate().plusDays(1).plusMonths(entity.getContractDuration().getValue()));
            }

            entity.setResignSessionEntity(finalResignSessionEntity);
            entity.setResignStatus(ResignStatus.IN_EVALUATION);
            entity.setResignType(finalResignSessionEntity.getResignType());
            return entity;
        }).collect(Collectors.toList());

        resignSessionEntity.getResignSessionContractEntities().clear();
        resignSessionEntity.getResignSessionContractEntities().addAll(commonResignSessionContractEntities);
        resignSessionEntity.getResignSessionContractEntities().addAll(need2BeAddedResignSessionContractEntities);

        need2BeDeletedResignSessionContractEntities.forEach(obj -> obj.contractEntity.setResignStatus(ResignStatus.NOT_IN_RESIGN_SESSION));

        resignSessionContractRepository.saveAll(need2BeDeletedResignSessionContractEntities);

        resignSessionContractRepository.softDeleteAll(resignSessionContractIds);
        resignSessionContractRepository.saveAll(need2BeAddedResignSessionContractEntities);
        //</editor-fold>

        resignSessionEntity = resignSessionRepository.save(resignSessionEntity);

        contractService.updateContractResignStatus(id, ResignStatus.IN_EVALUATION);

        addLog("RESIGN_SESSION", "UPDATE", new Gson().toJson(request));
        return convertEntity2Response(resignSessionEntity);
    }

    private ResignSessionResponse convertEntity2Response(ResignSessionEntity resignSessionEntity) {
        ResignSessionResponse response = objectMapper.convertValue(resignSessionEntity, ResignSessionResponse.class);

        List<ResignSessionContractIdAndContractId> bridgeList = resignSessionContractRepository
                .getAllResignContractFromResign(resignSessionEntity.getResignSessionId());

        response.resignSessionContractResponses = bridgeList.stream().map(bridge -> {
            ResignSessionContractResponse resignSessionContractResponse = new ResignSessionContractResponse();
            resignSessionContractResponse.resignSessionContractId = bridge.resignSessionContractId;
            resignSessionContractResponse.contractResponse = new ContractResponse();
            resignSessionContractResponse.contractResponse.contractId = bridge.contractId;
            return resignSessionContractResponse;
        }).collect(Collectors.toList());

//        response.resignSessionContractResponses = resignSessionEntity
//                .getResignSessionContractEntities()
//                .stream().map(obj -> {
//                    ResignSessionContractResponse bridgeResponse = objectMapper.convertValue(obj, ResignSessionContractResponse.class);
////                    bridgeResponse.contractResponse = objectMapper.convertValue(obj.contractEntity, ContractResponse.class);
////                    return bridgeResponse;
//                    ResignSessionContractResponse shortBridgeResponse = new ResignSessionContractResponse();
//                    shortBridgeResponse.resignSessionContractId = bridgeResponse.resignSessionContractId;
//                    shortBridgeResponse.contractResponse = new ContractResponse();
//                    shortBridgeResponse.contractResponse.contractId = obj.contractEntity.getContractId();
//                    return shortBridgeResponse;
//
//                })
//                .collect(Collectors.toList());

//        VhrFutureOrganizationEntity organizationEntity = organizationRepository.findById(resignSessionEntity.getUnitId()).orElse(new VhrFutureOrganizationEntity());
//        response.unitName = organizationEntity.getName();
        VhrFutureOrganizationDTO.DepartmentUnitResponse departmentUnitResponse = organizationService.getOnlyUnitFromOrganization(resignSessionEntity.getUnitId());
        response.unitName = departmentUnitResponse.unitName;

        return response;
    }

    @Override
    public boolean updateResignVofficeStatus(Long resignStatusId, ResignVofficeStatus resignVofficeStatus) {
        Integer result = resignSessionRepository.updateResignVofficeStatus(resignStatusId, resignVofficeStatus.getValue());

        return result != null;
    }

    @Override
    public void updateBMUnitFile(Long resignStatusId, String bmUnitEncodePath) {
        Set<Long> resignStatusIdSet = new HashSet<>();
        resignStatusIdSet.add(resignStatusId);
        resignSessionRepository.updateBMUnitFilePathByResignIdSet(resignStatusIdSet, bmUnitEncodePath);
    }

    @Override
    public boolean updateResignStatus(Long resignSessionId, ResignStatus resignStatus) {
        ResignSessionEntity resignSessionEntity = resignSessionRepository.findById(resignSessionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đợt tái ký"));

        Set<Long> resignContractIdSet = resignSessionEntity.getResignSessionContractEntities()
                .stream().map(ResignSessionContractEntity::getResignSessionContractId)
                .collect(Collectors.toSet());

        resignSessionRepository.updateResignStatusByResignId(resignSessionId, resignStatus);
        resignSessionContractRepository.updateResignStatusByResignContractSet(resignContractIdSet, resignStatus);

        return true;
    }

    @Override
    public boolean addContractToVoffice2(ResignContractAddToVofficeLaborRequest request) {
        Set<Long> resignIds = new HashSet<>();
        if (request.isAll) {
            Set<Long> resignEntitySet = resignSessionRepository.getAllResignIdByQuarterYearResignStatusAndType(request.quarter, request.year, ResignStatus.RECEIVED_BM_09_VOFFICE_AND_SUCCESS, ResignType.LABOR);
            resignIds.addAll(resignEntitySet);
        } else {
            resignIds.addAll(request.resignIdList);
        }

        resignSessionRepository.updateResignStatusByResignIdSet(resignIds, ResignStatus.HR_TCT_ADD_EMPLOYEE_TO_VOFFICE2);
        resignSessionContractRepository.updateResignStatusWithResignSession(resignIds, ResignStatus.HR_TCT_ADD_EMPLOYEE_TO_VOFFICE2);

        return true;
    }

    @Override
    public boolean addContractToVoffice2(ResignContractAddToVofficeProbationaryRequest request) {
        Set<Long> resignIds = new HashSet<>();
        if (request.isAll) {
            Set<Long> resignEntitySet = resignSessionRepository.getAllResignIdByQuarterYearResignStatusAndType(request.startDate, request.endDate, ResignStatus.RECEIVED_BM_09_VOFFICE_AND_SUCCESS, ResignType.PROBATIONARY);
            resignIds.addAll(resignEntitySet);
        } else {
            resignIds.addAll(request.resignIdList);
        }

        resignSessionRepository.updateResignStatusByResignIdSet(resignIds, ResignStatus.HR_TCT_ADD_EMPLOYEE_TO_VOFFICE2);
        resignSessionContractRepository.updateResignStatusWithResignSession(resignIds, ResignStatus.HR_TCT_ADD_EMPLOYEE_TO_VOFFICE2);

        return true;
    }


    @Override
    public void updateResignStatusAndRelated(Long resignSessionId, ResignStatus resignStatus) {
        ResignSessionEntity resignSessionEntity = resignSessionRepository.findById(resignSessionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đợt tái ký"));

        List<ResignSessionContractEntity> list = resignSessionEntity.getResignSessionContractEntities();

        Set<Long> contractIds = resignSessionEntity.getResignSessionContractEntities()
                .stream()
                .map(obj -> obj.getContractEntity().getContractId())
                .collect(Collectors.toSet());

        resignSessionEntity.getResignSessionContractEntities().forEach(resignContract -> {
            resignContract.setResignStatus(resignStatus);
        });

//        resignSessionRepository.updateResignStatusByResignId(resignSessionId, resignStatus);
        resignSessionEntity.setResignStatus(resignStatus);
//        resignSessionContractRepository.updateResignStatusByResignId(resignSessionId, resignStatus);
        contractRepository.updateResignStatusByContractIdSet(contractIds, resignStatus);

        resignSessionRepository.save(resignSessionEntity);
    }

    @Override
    public void updateResignStatusAndRelated(Set<Long> resignSessionIdSet, ResignStatus resignStatus) {
        resignSessionIdSet.forEach(resignSessionId -> {
            updateResignStatusAndRelated(resignSessionId, resignStatus);
        });
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, noRollbackFor = Exception.class)
    // Chi dung khi trinh ky TCT
    public void updateResignStatusAndRelated(int quarter, int year, ResignType resignType, ResignStatus startResignStatus, ResignStatus endResignStatus, ResignStatus resultResignStatus) {
        resignSessionRepository.getAllResignIdByQuarterYearResignType(quarter, year, resignType, startResignStatus, endResignStatus).forEach(resignSessionId -> {
            updateResignStatusAndRelated(resignSessionId, resultResignStatus);
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, noRollbackFor = Exception.class)
    public void updateResignStatusAndRelated(LocalDate startDate, LocalDate endDate, ResignType resignType, ResignStatus resignStatus) {
        resignSessionRepository.getAllResignIdByStartDateEndDateResignType(startDate, endDate, resignType).forEach(resignSessionId -> {
            updateResignStatusAndRelated(resignSessionId, resignStatus);
        });
    }

    @Override
    public void updateTranscodeAndRelated(Long resignSessionId, String transcode) {
        resignSessionRepository.updateTranscodeByResignId(resignSessionId, transcode);
    }

    @Override
    public void updateTranscodeAndRelated(Set<Long> resignSessionIdSet, String transcode) {
        resignSessionRepository.updateTranscodeByResignIdSet(resignSessionIdSet, transcode);
    }

    @Override
    public void updateTranscodeAndRelated(int quarter, int year, ResignType resignType, String transcode) {
        resignSessionRepository.updateTranscodeByResignIdSet(quarter, year, resignType, transcode);
    }

    @Override
    public void updateTranscodeAndRelated(LocalDate startDate, LocalDate endDate, ResignType resignType, String transcode) {
        resignSessionRepository.updateTranscodeByResignIdSet(startDate, endDate, resignType, transcode);
    }

//    @EventListener(ApplicationReadyEvent.class)
//    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, noRollbackFor = Exception.class)
//    public void test(ApplicationReadyEvent event) {
//        List<ResignSessionEntity> resignEntityList = new ArrayList<>();
//        ResignSessionEntity resignSessionEntity = resignSessionRepository.findById(657l)
//                .orElseThrow(() -> new NotFoundException("Noooo"));
//        resignEntityList.add(resignSessionEntity);
//        createTempContractFile(resignEntityList);
//    }

    @Override
    public void createTempContractFile(List<ResignSessionEntity> resignEntityList) {
        List<ContractEntity> oldContractList = new ArrayList<>();
        List<ContractEntity> newContractList = new ArrayList<>();
        List<ResignSessionContractEntity> resignContractList = new ArrayList<>();

        resignEntityList.forEach(resign -> {
            int numberOfContractInResign = resign.getResignSessionContractEntities().size();
            List<Long> seqList = contractRepository.getSequence(numberOfContractInResign);
            resign.getResignSessionContractEntities().forEach(resignContract -> {
                if (resignContract.getPassStatus().equals(ResignPassStatus.PASS)) {
                    oldContractList.add(getOldContract(resignContract));
                    newContractList.add(getNewContract(resignContract, seqList.remove(0)));
                    resignContractList.add(resignContract);
                }
            });

        });

        List<ContractEntity> newNewContractList = contractRepository.saveAll(newContractList);
        contractRepository.saveAll(oldContractList);
        newNewContractList.forEach(newContract -> {
            resignContractList.forEach(resignContract -> {
                if (resignContract.getContractEntity().getEmployeeCode().equals(newContract.getEmployeeCode())) {
                    resignContract.getContractEntity().setContractId(newContract.getContractId());
                }
            });
        });
        resignSessionContractRepository.saveAll(resignContractList);
    }

    private ContractEntity getOldContract(ResignSessionContractEntity resignContract) {
        ContractEntity oldContract = resignContract.contractEntity;
        oldContract.setResignStatus(ResignStatus.RECEIVED_VOFFICE2_AND_SUCCESS);
        return oldContract;
    }

    private ContractEntity getNewContract(ResignSessionContractEntity resignContract, Long newContractId) {
        ContractEntity newContract = new ContractEntity();

        modelMapper.map(resignContract.contractEntity, newContract);
        newContract.setContractId(newContractId);
        newContract.setEffectiveDate(resignContract.getNewContractEffectiveDate());
        newContract.setExpiredDate(resignContract.getNewContractExpiredDate());
        newContract.setContractDuration(resignContract.getContractDuration());
        newContract.setResignStatus(ResignStatus.TEMP_CONTRACT_CREATE);
        newContract.setIsActive(false);
        newContract.setContractType(ContractType.LABOR_CONTRACT);

        resignContract.getContractEntity().setContractId(newContractId);

        return newContract;
    }

    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public void addAllSuitableContract(Long resignId) {
        ResignSessionEntity resignSessionEntity = resignSessionRepository.findById(resignId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đợt tái ký"));

        switch (resignSessionEntity.getResignType()) {
            case LABOR: {
                System.out.println("hi");

                break;
            }
            case PROBATIONARY: {
                break;
            }
        }
    }

//    private void addAllSuitableContractForLaborResign(ResignSessionEntity resignSessionEntity) {
//        resignSessionEntity.getStartDate()
//    }

    @Override
    public Set<Long> getResignIdSetForVO2(LocalDate startDate, LocalDate endDate) {
        Set<ResignStatus> resignStatusSet = new HashSet<>();
        resignStatusSet.add(ResignStatus.RECEIVED_BM_09_VOFFICE_AND_SUCCESS);
        resignStatusSet.add(ResignStatus.HR_TCT_ADD_EMPLOYEE_TO_VOFFICE2);
        resignStatusSet.add(ResignStatus.HR_TCT_CREATED_BMTCT_FILE);
        return resignSessionRepository.getAllResignByStartDateEndDateResignStatus(startDate, endDate, resignStatusSet);
    }

    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public FileDTO.FileResponse exportBm07(Long resignId) {
        ResignSessionEntity resignSessionEntity = resignSessionRepository.findById(resignId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đợt tái ký"));

        return exportBM07(resignSessionEntity);
    }

    private FileDTO.FileResponse exportBM07(ResignSessionEntity resignSessionEntity) {
        FileDTO.FileResponse fileResponse = new FileDTO.FileResponse();

        String input = BM07_PATH;
        String fileName = "testBm07.xlsx";

        String outputFolder = fileService.getUploadFolder();
        fileService.createFolder(fileService.encodePath(outputFolder));
        String output = outputFolder + fileService.getFilePrefix() + fileName;

        List<ResignBm07Response> resignBm07Responses = resignSessionEntity.getResignSessionContractEntities()
                .stream()
                .map(obj -> resignSessionContractService.convertEntityToBm07Response(obj))
                .collect(Collectors.toList());

        if (resignBm07Responses.size() == 0) throw new NotFoundException("Không thấy dữ liệu");

        for (int i = 0; i < resignBm07Responses.size(); i++) {
            resignBm07Responses.get(i).index = i + 1;
        }

        Bm07Metadata bm07Metadata = new Bm07Metadata();
        bm07Metadata.quarter = 4;
        bm07Metadata.year = 2021;
        bm07Metadata.monthlyReviewResponseList = resignBm07Responses.get(0).listMonthlyReview;

        excelUtil.exportExcel(input, output, bm07Metadata, resignBm07Responses);
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

    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public FileDTO.FileResponse exportBm08(Long resignId) {
        ResignSessionEntity resignSessionEntity = resignSessionRepository.findById(resignId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đợt tái ký"));

        return exportBM08(resignSessionEntity);
    }

    private FileDTO.FileResponse exportBM08(ResignSessionEntity resignSessionEntity) {
        FileDTO.FileResponse fileResponse = new FileDTO.FileResponse();

        String input = BM08_PATH;
        String fileName = "testBm08.xlsx";

        String outputFolder = fileService.getUploadFolder();
        fileService.createFolder(fileService.encodePath(outputFolder));
        String output = outputFolder + fileService.getFilePrefix() + fileName;

        List<ResignBm08Response> resignBm08Responses = resignSessionEntity.getResignSessionContractEntities()
                .stream()
                .map(obj -> resignSessionContractService.convertEntityToBm08Response(obj))
                .collect(Collectors.toList());

        if (resignBm08Responses.size() == 0) throw new NotFoundException("Không thấy dữ liệu");

        for (int i = 0; i < resignBm08Responses.size(); i++) {
            resignBm08Responses.get(i).index = i + 1;
        }

        Bm07Metadata bm07Metadata = new Bm07Metadata();
        bm07Metadata.quarter = 4;
        bm07Metadata.year = 2021;

        excelUtil.exportExcel(input, output, bm07Metadata, resignBm08Responses);
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

}
