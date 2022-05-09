package com.viettel.hstd;

import com.viettel.hstd.constant.*;
import com.viettel.hstd.controller.AuthController;
import com.viettel.hstd.core.dto.AccountLoginDTO;
import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.specification.EntityConfigSpec;
import com.viettel.hstd.dto.hstd.ContractDTO;
import com.viettel.hstd.dto.vps.VhrFutureOrganizationDTO;
import com.viettel.hstd.entity.hstd.*;
import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import com.viettel.hstd.repository.hstd.*;
import com.viettel.hstd.entity.vps.EmployeeVhrEntity;
import com.viettel.hstd.entity.vps.PositionEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.vps.EmployeeVhrRepository;
import com.viettel.hstd.repository.vps.PositionRepository;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
import com.viettel.hstd.security.JwtTokenUtils;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.ProbationaryContractService;
import com.viettel.hstd.service.inf.VhrFutureOrganizationService;
import com.viettel.hstd.util.VOConfig;
import com.viettel.security.PassTranformer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootTest
class HstdApplicationTests {

    @Autowired
    AuthController authController;
    @Autowired
    private ContractExpiredRepository contractExpiredRepository;
    @Autowired
    private EmployeeVhrRepository employeeVhrRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private ProbationaryContractService probationaryContractService;
    @Autowired
    private ProbationaryContractRepository probationaryContractRepository;

    @Test
    void contextLoads() {
        Assertions.assertTrue(true);
    }

    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private VhrFutureOrganizationService vhrFutureOrganizationService;
    @Autowired
    private ContractRenewalBm07Repository contractRenewalBm07Repository;
    @Autowired
    private ContractRenewalBm08Repository contractRenewalBm08Repository;
    @Autowired
    private ContractRenewalBm09Repository contractRenewalBm09Repository;
    @Autowired
    private VhrFutureOrganizationRepository organizationRepository;
    @Autowired
    private ResignSessionRepository resignSessionRepository;
    @Autowired
    private VOConfig voConfig;
    @Autowired
    private EmployeeMonthlyReviewRepository employeeMonthlyReviewRepository;
    @Autowired
    private LaborContractRepository laborContractRepository;
    @Autowired
    private ResignSessionContractRepository resignSessionContractRepository;

//    @Test
//    @DisplayName("checkLogin")
//    @EnabledIf(expression = "#{environment.acceptsProfiles('real-local')}", loadContext = true)
//    void checkLogin() {
//        AccountLoginDTO accountLoginDTO = new AccountLoginDTO();
//        accountLoginDTO.userName = "240410";
//        accountLoginDTO.password = "Tcld@246";
//
//
//        BaseResponse<String> baseResponse = authController.appLogin(accountLoginDTO);
//        SSoResponse soResponse = jwtTokenUtils.validateToken(baseResponse.result);
//
//        Assertions.assertNotNull(soResponse.getEmployeeCode());
//    }


//    @Test
//    @DisplayName("checkLogin")
//    @EnabledIf(expression = "#{environment.acceptsProfiles('dev')}", loadContext = true)
//    void checkLogin2() {
//        AccountLoginDTO accountLoginDTO = new AccountLoginDTO();
//        accountLoginDTO.userName = "142080";
//        accountLoginDTO.password = "123456";
//
//        PassTranformer.setInputKey(voConfig.ca_encrypt_key);
//        System.out.println(PassTranformer.encrypt("Kyanh1011@$$"));
//
//
//        BaseResponse<String> baseResponse = authController.appLogin(accountLoginDTO);
//        SSoResponse soResponse = jwtTokenUtils.validateToken(baseResponse.result);
//
//        Assertions.assertNotNull(soResponse.getEmployeeCode());
//    }



//    @Test
//    @DisplayName("create resign session from bm07")
//    void createResignSessionFromBm07() {
//        List<ContractRenewalBm07Entity> bm07EntityList = contractRenewalBm07Repository.findAll();
//
//        Set<String> employeeCodeSet = bm07EntityList
//                .stream()
//                .map(ContractRenewalBm07Entity::getEmployeeCode)
//                .collect(Collectors.toSet());
//        List<EmployeeVhrEntity> employeeVhrEntityList = employeeVhrRepository.findByEmployeeCodeIn(employeeCodeSet);
//
//        Assertions.assertEquals(bm07EntityList.size(), employeeCodeSet.size());
//
//        Set<Long> unitIdSet = employeeVhrEntityList.stream().map(obj -> {
//            VhrFutureOrganizationDTO.DepartmentUnitResponse response = vhrFutureOrganizationService.getOnlyUnitFromOrganization(obj.getOrganizationId());
//            return response.unitId;
//        }).collect(Collectors.toSet());
//
//
//        List<ResignSessionEntity> resignSessionEntityList = unitIdSet.stream().map(obj -> {
//            ResignSessionEntity resignSessionEntity = new ResignSessionEntity();
//            resignSessionEntity.setUnitId(obj);
//            resignSessionEntity.setStatus(ResignVofficeStatus.NOT_SEND_YET);
//            resignSessionEntity.setCreatedAt(LocalDateTime.now().withMonth(8));
//            resignSessionEntity.setYear(2021);
//            resignSessionEntity.setQuarter(4);
//            resignSessionEntity.setStartDate(LocalDate.of(2021, 10, 1));
//            resignSessionEntity.setEndDate(LocalDate.of(2021, 12, 31));
//            resignSessionEntity.setResignType(ResignType.LABOR);
//            resignSessionEntity.setResignStatus(ResignStatus.IN_EVALUATION);
//
//            return resignSessionEntity;
//        }).collect(Collectors.toList());
//
////        resignSessionRepository.saveAll(resignSessionEntityList);
//
//        Assertions.assertEquals(resignSessionEntityList.size(), unitIdSet.size());
//    }

//    @Test
//    @DisplayName("Add contract from bm07")
//    void addContractFromBm07() {
//        List<ContractRenewalBm07Entity> bm07EntityList = contractRenewalBm07Repository.findAll();
//
//        List<ContractEntity> contractEntityList = bm07EntityList.stream().map(this::convertContractRenewalbm07ToContract)
//                .collect(Collectors.toList());
//
//        contractRepository.saveAll(contractEntityList);
//
//        Assertions.assertEquals(bm07EntityList.size(), contractEntityList.size());
//    }
//
//    private ContractEntity convertContractRenewalbm07ToContract(ContractRenewalBm07Entity contractRenewalBm07Entity) {
//        ContractEntity contractEntity = new ContractEntity();
//
//        contractEntity.setContractType(ContractType.LABOR_CONTRACT);
//        contractEntity.setContractNumber("Contract Number");
//        contractEntity.setSignedPlace("Signed Place");
//        contractEntity.setEffectiveDate(contractRenewalBm07Entity.getEffectiveDate());
//        contractEntity.setExpiredDate(contractRenewalBm07Entity.getExpiredDate());
//        contractEntity.setSignedDate(contractRenewalBm07Entity.getEffectiveDate());
//
//        EmployeeVhrEntity employeeVhrEntity = employeeVhrRepository
//                .findFirstByEmployeeCode(contractRenewalBm07Entity.getEmployeeCode())
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên " + contractRenewalBm07Entity.getEmployeeCode()));
//
//        contractEntity.setEmployeeId(employeeVhrEntity.getEmployeeId());
//        contractEntity.setEmployeeCode(employeeVhrEntity.getEmployeeCode());
//        contractEntity.setEmployeeName(employeeVhrEntity.getFullname());
//        contractEntity.setNationality("Việt Nam");
//        contractEntity.setBirthDate(employeeVhrEntity.getDateOfBirth());
//        contractEntity.setPlaceOfBirth(employeeVhrEntity.getPlaceOfBirth());
//        contractEntity.setGender(employeeVhrEntity.getGender());
//        contractEntity.setTrainingLevel(contractRenewalBm07Entity.getTrainingLevel());
//        contractEntity.setTrainingSpeciality(contractRenewalBm07Entity.getTrainingSpeciality());
//        contractEntity.setPersonalIdNumber(employeeVhrEntity.getPersonalIdNumber());
//        contractEntity.setPersonalIdIssuedDate(employeeVhrEntity.getPersonalIdIssuedDate());
//        contractEntity.setPersonalIdIssuedPlace(employeeVhrEntity.getPersonalIdIssuedPlace());
//        contractEntity.setPermanentAddress(employeeVhrEntity.getPermanentAddress());
//        contractEntity.setMobileNumber(employeeVhrEntity.getMobileNumber());
//
//        PositionEntity positionEntity = positionRepository.findById(employeeVhrEntity.getPositionId())
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy chức danh " + employeeVhrEntity.getPositionId() + "của nhân viên " + employeeVhrEntity.getEmployeeCode()));
//
//        contractEntity.setPositionId(positionEntity.getPositionId());
//        contractEntity.setPositionCode(positionEntity.getPositionCode());
//        contractEntity.setPositionName(positionEntity.getPositionName());
//        long period = ChronoUnit.MONTHS
//                .between(
//                        contractRenewalBm07Entity.getEffectiveDate().withDayOfMonth(1),
//                        contractRenewalBm07Entity.getExpiredDate().withDayOfMonth(1));
//        if (period == 11) period = 12;
//        if (period == 23) period = 24;
//        ContractDuration contractDuration = ContractDuration.of((int) period);
//        contractEntity.setContractDuration(contractDuration);
//
//        VhrFutureOrganizationDTO.DepartmentUnitResponse departmentUnitResponse = vhrFutureOrganizationService.getDepartmentAndUnitFromOrganization(employeeVhrEntity.getOrganizationId());
//
//        contractEntity.setDepartmentId(departmentUnitResponse.departmentId);
//        contractEntity.setUnitId(departmentUnitResponse.unitId);
//
//        return contractEntity;
//    }
//
//    @Test
//    @DisplayName("Add contract")
//    @EnabledIf(expression = "#{environment.acceptsProfiles('dev')}", loadContext = true)
//    void addContract() {
//        List<ContractExpiredEntity> contractExpiredEntityList = contractExpiredRepository.findAll();
//        List<ContractEntity> contractEntityList = new ArrayList<>();
//
//        contractEntityList = contractExpiredEntityList
//                .stream()
//                .map(this::convertContractExpiredToContract)
//                .collect(Collectors.toList());
//
////        contractRepository.saveAll(contractEntityList);
//
//        org.junit.jupiter.api.Assertions.fail("Not Implemented");
//    }
//
//    private ContractEntity convertContractExpiredToContract(ContractExpiredEntity contractExpiredEntity) {
//        ContractEntity contractEntity = new ContractEntity();
//
//        contractEntity.setContractType(ContractType.PROBATIONARY_CONTRACT);
//        contractEntity.setContractNumber("Contract Number");
//        contractEntity.setSignedPlace("Signed Place");
//        contractEntity.setEffectiveDate(contractExpiredEntity.getEffectiveDate());
//        contractEntity.setExpiredDate(contractExpiredEntity.getExpiredDate());
//        contractEntity.setSignedDate(contractExpiredEntity.getEffectiveDate());
//
//        EmployeeVhrEntity employeeVhrEntity = employeeVhrRepository
//                .findFirstByEmployeeCode(contractExpiredEntity.getEmployeeCode())
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên " + contractExpiredEntity.getEmployeeCode()));
//
//        contractEntity.setEmployeeId(employeeVhrEntity.getEmployeeId());
//        contractEntity.setEmployeeCode(employeeVhrEntity.getEmployeeCode());
//        contractEntity.setEmployeeName(employeeVhrEntity.getFullname());
//        contractEntity.setNationality("Việt Nam");
//        contractEntity.setBirthDate(employeeVhrEntity.getDateOfBirth());
//        contractEntity.setPlaceOfBirth(employeeVhrEntity.getPlaceOfBirth());
//        contractEntity.setGender(employeeVhrEntity.getGender());
//        contractEntity.setTrainingLevel(contractExpiredEntity.getTrainingLevel());
//        contractEntity.setTrainingSpeciality(contractExpiredEntity.getTrainingSpeciality());
//        contractEntity.setPersonalIdNumber(employeeVhrEntity.getPersonalIdNumber());
//        contractEntity.setPersonalIdIssuedDate(employeeVhrEntity.getPersonalIdIssuedDate());
//        contractEntity.setPersonalIdIssuedPlace(employeeVhrEntity.getPersonalIdIssuedPlace());
//        contractEntity.setPermanentAddress(employeeVhrEntity.getPermanentAddress());
//        contractEntity.setMobileNumber(employeeVhrEntity.getMobileNumber());
//
//        PositionEntity positionEntity = positionRepository.findById(employeeVhrEntity.getPositionId())
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy chức danh " + employeeVhrEntity.getPositionId() + "của nhân viên " + employeeVhrEntity.getEmployeeCode()));
//
//        contractEntity.setPositionId(positionEntity.getPositionId());
//        contractEntity.setPositionCode(positionEntity.getPositionCode());
//        contractEntity.setPositionName(positionEntity.getPositionName());
//        contractEntity.setContractDuration(contractExpiredEntity.getContractDuration());
//
//        VhrFutureOrganizationDTO.DepartmentUnitResponse departmentUnitResponse = vhrFutureOrganizationService.getDepartmentAndUnitFromOrganization(employeeVhrEntity.getOrganizationId());
//
//        contractEntity.setDepartmentId(departmentUnitResponse.departmentId);
//        contractEntity.setUnitId(departmentUnitResponse.unitId);
//
//        return contractEntity;
//    }
//
//    @Test
//    @DisplayName("add bm07 to monthly review")
//    void addBm07ToMonthlyReview() {
//        List<ContractRenewalBm07Entity> bm07EntityList = contractRenewalBm07Repository.findAll();
//
//        Set<String> employeeCodeSet = bm07EntityList
//                .stream()
//                .map(ContractRenewalBm07Entity::getEmployeeCode)
//                .collect(Collectors.toSet());
//        List<EmployeeVhrEntity> employeeVhrEntityList = employeeVhrRepository.findByEmployeeCodeIn(employeeCodeSet);
//        Map<String, Long> employeeMap = employeeVhrEntityList
//                .stream()
//                .collect(Collectors.toMap(EmployeeVhrEntity::getEmployeeCode, EmployeeVhrEntity::getEmployeeId));
//
//        Assertions.assertEquals(bm07EntityList.size(), employeeCodeSet.size());
//
//        List<EmployeeMonthlyReviewEntity> employeeMonthlyReviewEntityList = new ArrayList<>();
//        bm07EntityList.forEach(obj -> {
//            List<EmployeeMonthlyReviewEntity> employeeMonthlyReviewEntityMiniList = new ArrayList<>();
//            LocalDate startMonth = LocalDate.of(2020, 8, 1);
//            for (int i = 0; i < 12; i++) {
//                EmployeeMonthlyReviewEntity employeeMonthlyReviewEntity1 = new EmployeeMonthlyReviewEntity();
//                employeeMonthlyReviewEntity1.setMonth(startMonth.plusMonths(i));
//
//                employeeMonthlyReviewEntity1.setEmployeeId(employeeMap.get(obj.getEmployeeCode()));
//                switch (i) {
//                    case 0: {
//                        employeeMonthlyReviewEntity1.setGrade(obj.getKpiGrade1());
//                        break;
//                    }
//                    case 1: {
//                        employeeMonthlyReviewEntity1.setGrade(obj.getKpiGrade2());
//                        break;
//                    }
//                    case 2: {
//                        employeeMonthlyReviewEntity1.setGrade(obj.getKpiGrade3());
//                        break;
//                    }
//                    case 3: {
//                        employeeMonthlyReviewEntity1.setGrade(obj.getKpiGrade4());
//                        break;
//                    }
//                    case 4: {
//                        employeeMonthlyReviewEntity1.setGrade(obj.getKpiGrade5());
//                        break;
//                    }
//                    case 5: {
//                        employeeMonthlyReviewEntity1.setGrade(obj.getKpiGrade6());
//                        break;
//                    }
//                    case 6: {
//                        employeeMonthlyReviewEntity1.setGrade(obj.getKpiGrade7());
//                        break;
//                    }
//                    case 7: {
//                        employeeMonthlyReviewEntity1.setGrade(obj.getKpiGrade8());
//                        break;
//                    }
//                    case 8: {
//                        employeeMonthlyReviewEntity1.setGrade(obj.getKpiGrade9());
//                        break;
//                    }
//                    case 9: {
//                        employeeMonthlyReviewEntity1.setGrade(obj.getKpiGrade10());
//                        break;
//                    }
//                    case 10: {
//                        employeeMonthlyReviewEntity1.setGrade(obj.getKpiGrade11());
//                        break;
//                    }
//                    case 11: {
//                        employeeMonthlyReviewEntity1.setGrade(obj.getKpiGrade12());
//                        break;
//                    }
//                }
//
//                if (employeeMonthlyReviewEntity1.getGrade() != null && !employeeMonthlyReviewEntity1.getGrade().equals(KpiGrade.UNKNOWN)) {
//                    employeeMonthlyReviewEntityMiniList.add(employeeMonthlyReviewEntity1);
//                }
//            }
//            employeeMonthlyReviewEntityList.addAll(employeeMonthlyReviewEntityMiniList);
//
//        });
//
//        employeeMonthlyReviewRepository.saveAll(employeeMonthlyReviewEntityList);
//
//        Assertions.assertEquals(employeeMonthlyReviewEntityList.size(), bm07EntityList.size() * 12);
//    }

//    @Test
//    @DisplayName("Add Contract To Resign Session")
//    void addContractToResignSession() {
//
//        List<ContractRenewalBm07Entity> bm07EntityList = contractRenewalBm07Repository.findAll();
//        List<ContractRenewalBm08Entity> bm08EntityList = contractRenewalBm08Repository.findAll();
//        Map<String, ContractRenewalBm08Entity> employeeCodeBm08Map = bm08EntityList.stream()
//                .collect(Collectors.toMap(ContractRenewalBm08Entity::getEmployeeCode, Function.identity()));
//        List<ContractRenewalBm09Entity> bm09EntityList = contractRenewalBm09Repository.findAll();
//        Map<String, ContractRenewalBm09Entity> employeeCodeBm09Map = bm09EntityList.stream()
//                .collect(Collectors.toMap(ContractRenewalBm09Entity::getEmployeeCode, Function.identity()));
//        List<ResignSessionContractEntity> resignContractList = new ArrayList<>();
//
//        Set<String> employeeCodeSet = bm07EntityList
//                .stream()
//                .map(ContractRenewalBm07Entity::getEmployeeCode)
//                .collect(Collectors.toSet());
//        List<EmployeeVhrEntity> employeeVhrEntityList = employeeVhrRepository.findByEmployeeCodeIn(employeeCodeSet);
//        Map<String, Long> employeeMap = employeeVhrEntityList
//                .stream()
//                .collect(Collectors.toMap(EmployeeVhrEntity::getEmployeeCode, EmployeeVhrEntity::getEmployeeId));
//
//        List<ResignSessionEntity> resignSessionEntityList = resignSessionRepository.findAll(new EntityConfigSpec<ResignSessionEntity>(
//                new SearchDTO.SearchCriteria("name",
//                        Operation.LIKE,
//                        "Đợt tái ký năm 2021 quý 4 HĐLĐ của đơn vị ",
//                        SearchType.STRING,
//                        true)));
//        Map<Long, ResignSessionEntity> unitResignMap = resignSessionEntityList.stream().collect(Collectors.toMap(ResignSessionEntity::getUnitId, Function.identity()));
//
//        bm07EntityList.forEach(bm07 -> {
//            ContractEntity contractEntity = contractRepository.findFirstByEmployeeCode(bm07.getEmployeeCode())
//                    .orElseThrow(() -> new NotFoundException("Không tìm thấy hợp đồng của nhân viên " + bm07.getEmployeeCode() + ""));
//            ResignSessionContractEntity resignSessionContractEntity = new ResignSessionContractEntity();
//
//            ContractRenewalBm08Entity bm08 = employeeCodeBm08Map.get(contractEntity.getEmployeeCode());
//            ContractRenewalBm09Entity bm09 = employeeCodeBm09Map.get(contractEntity.getEmployeeCode());
//
//            resignSessionContractEntity.setResignSessionEntity(unitResignMap.get(contractEntity.getUnitId()));
//            resignSessionContractEntity.setContractEntity(contractEntity);
//            resignSessionContractEntity.setReviewDate(LocalDate.now().withMonth(8).withDayOfMonth(1));
//
//            resignSessionContractEntity.setInterviewScore(bm08.getInterviewScore());
//            resignSessionContractEntity.setInterviewComment(bm08.getCouncilReview());
//
//            resignSessionContractEntity.setAttitude(bm09.getAttitude());
//            resignSessionContractEntity.setPassStatus(bm09.getResignPassStatus());
//            resignSessionContractEntity.setContractDuration(bm09.getNewContractDuration());
//            resignSessionContractEntity.setWorkingProgressNote(bm07.getNote());
//            resignSessionContractEntity.setInterviewNote(bm08.getNote());
//            resignSessionContractEntity.setResignNote(bm09.getNote());
//            resignSessionContractEntity.setNewContractEffectiveDate(bm09.getNewEffectiveDate());
//            resignSessionContractEntity.setNewContractExpiredDate(bm09.getNewExpiredDate());
//            resignSessionContractEntity.setResignType(ResignType.LABOR);
//            resignSessionContractEntity.setResignStatus(ResignStatus.IN_EVALUATION);
//
//            resignContractList.add(resignSessionContractEntity);
//        });
//
////        resignSessionContractRepository.saveAll(resignContractList);
//
//        Assertions.assertEquals(resignSessionEntityList.size(), 60);
//    }

//    @Test
//    @DisplayName("Create Contract File for existing contract entity")
//    void createContractFileForExistingContractEntity() {
//
//        List<ProbationaryContractEntity> probationaryContractEntityList = probationaryContractRepository.findAll();
//
//        probationaryContractEntityList.forEach(contractEntity -> {
//            System.out.println("Start create contract for " + contractEntity.getEmployeeName() + " - " + contractEntity.getEmployeeCode() + " with id " + contractEntity.getContractId());
////            if (contractEntity.getContractFileEncodePath() == null) {
//                ContractDTO.ContractExportRequest request = new ContractDTO.ContractExportRequest();
//                request.contractId = contractEntity.getContractId();
//                probationaryContractService.createContractFileFromEntity(request);
////            }
//
//        });
//
//    }

}
