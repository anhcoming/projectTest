package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.constant.KpiGrade;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.EmployeeMonthlyReviewDTO.*;
import com.viettel.hstd.entity.hstd.ContractEntity;
import com.viettel.hstd.entity.hstd.EmployeeMonthlyReviewEntity;
import com.viettel.hstd.entity.hstd.ResignSessionEntity;
import com.viettel.hstd.entity.vps.EmployeeVhrEntity;
import com.viettel.hstd.entity.vps.PositionEntity;
import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.ContractRepository;
import com.viettel.hstd.repository.hstd.EmployeeMonthlyReviewRepository;
import com.viettel.hstd.repository.hstd.ResignSessionRepository;
import com.viettel.hstd.repository.vps.EmployeeVhrRepository;
import com.viettel.hstd.repository.vps.PositionRepository;
import com.viettel.hstd.repository.vps.VhrFutureOrganizationRepository;
import com.viettel.hstd.service.inf.EmployeeMonthlyReviewService;
import com.viettel.hstd.util.ScoreUtil;
import liquibase.pro.packaged.A;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.annotations.NaturalId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class EmployeeMonthlyReviewServiceImp extends BaseService implements EmployeeMonthlyReviewService {

    @Autowired
    EmployeeMonthlyReviewRepository employeeMonthlyReviewRepository;

    @Autowired
    Message message;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MapUtils mapUtils;

    @Autowired
    EmployeeVhrRepository employeeVhrRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    ContractRepository contractRepository;

    @Autowired
    VhrFutureOrganizationRepository organizationRepository;

    @Autowired
    ResignSessionRepository resignSessionRepository;

    @Override
    public EmployeeMonthlyReviewResponse findOneById(Long id) {
        EmployeeMonthlyReviewEntity emailConfigEntity = employeeMonthlyReviewRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(emailConfigEntity, EmployeeMonthlyReviewResponse.class);
    }

    @Override
    public Boolean delete(Long id) {
        if (!employeeMonthlyReviewRepository.existsById(id))
            throw new NotFoundException(message.getMessage("message.not_found"));
        employeeMonthlyReviewRepository.softDelete(id);
        addLog("DELETE");
        return true;
    }

    @Override
    public Page<EmployeeMonthlyReviewResponse> findPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<EmployeeMonthlyReviewEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<EmployeeMonthlyReviewEntity> list;
        if (searchRequest.pagedFlag) {
            list = employeeMonthlyReviewRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = employeeMonthlyReviewRepository.findAll(p);
        }

        return list.map(obj ->
                this.objectMapper.convertValue(obj, EmployeeMonthlyReviewResponse.class)
        );
    }

    @Override
    public EmployeeMonthlyReviewResponse create(EmployeeMonthlyReviewRequest request) {
        EmployeeMonthlyReviewEntity employeeMonthlyReviewEntity = objectMapper.convertValue(request, EmployeeMonthlyReviewEntity.class);
        employeeMonthlyReviewEntity = employeeMonthlyReviewRepository.save(employeeMonthlyReviewEntity);
        addLog("CREATE");
        return objectMapper.convertValue(employeeMonthlyReviewEntity, EmployeeMonthlyReviewResponse.class);
    }

    @Override
    public EmployeeMonthlyReviewResponse update(Long id, EmployeeMonthlyReviewRequest request) {
        EmployeeMonthlyReviewEntity employeeMonthlyReviewEntity = employeeMonthlyReviewRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        EmployeeMonthlyReviewEntity newE = objectMapper.convertValue(request, EmployeeMonthlyReviewEntity.class);
        mapUtils.customMap(newE, employeeMonthlyReviewEntity);
        employeeMonthlyReviewEntity.setEmployeeMonthlyReviewId(id);
        employeeMonthlyReviewEntity = employeeMonthlyReviewRepository.save(employeeMonthlyReviewEntity);
        addLog("UPDATE");
        return objectMapper.convertValue(employeeMonthlyReviewEntity, EmployeeMonthlyReviewResponse.class);
    }

//    @Override
//    public EmployeeYearlyReviewResponse getYearlyReview(Long employeeId, LocalDate monthReview) {
//        EmployeeYearlyReviewResponse response = new EmployeeYearlyReviewResponse();
//        try {
//            EmployeeVhrEntity employeeVhrEntity = employeeVhrRepository.findById(employeeId).orElseThrow(() -> new NotFoundException("Không tìm thấy Id của Nhân viên"));
//
//            response.employeeId = employeeVhrEntity.getEmployeeId();
//            response.employeeCode = employeeVhrEntity.getEmployeeCode();
//            response.fullName = employeeVhrEntity.getFullname();
//            response.birthYear = employeeVhrEntity.getDateOfBirth().getYear();
//            response.gender = employeeVhrEntity.getGender();
//
//            Long positionId = employeeVhrEntity.getPositionId();
//            PositionEntity positionEntity = positionRepository.findById(positionId).orElseThrow(() -> new NotFoundException("Không tìm thấy chức danh"));
//            response.positionId = positionEntity.getPositionId();
//            response.positionName = positionEntity.getPositionName();
//
//            Long organizationId = employeeVhrEntity.getOrganizationId();
//            VhrFutureOrganizationEntity organizationEntity = organizationRepository.findById(organizationId).orElseThrow(() -> new NotFoundException("Không tìm thấy đơn vị của người dùng"));
//
//            response.unitName = organizationEntity.getOrgNameLevel2();
//            response.departmentName = organizationEntity.getOrgNameLevel3();
//            response.trainingLevel = employeeVhrEntity.getTrainingLevel();
//            response.trainingSpeciality = employeeVhrEntity.getTrainingSpeciality();
//
//            ContractEntity contractEntity = contractRepository.findByEmployeeIdAndIsActiveTrue(employeeId).orElseThrow(() -> new NotFoundException("Không tìm thấy hợp đồng của nhân viên " + employeeVhrEntity.getEmployeeCode()));
//            response.contractEffectiveDate = contractEntity.getEffectiveDate();
//            response.contractExpiredDate = contractEntity.getExpiredDate();
//
//            LocalDate startDate = monthReview.minusMonths(12).withDayOfMonth(1);
//            LocalDate endDate = monthReview.withDayOfMonth(1);
//            List<EmployeeMonthlyReviewEntity> employeeMonthlyReviewEntities = employeeMonthlyReviewRepository
//                    .findByEmployeeIdAndMonthGreaterThanAndMonthLessThanOrderByMonthDesc(employeeId, startDate, endDate);
//
//            response.listMonthlyReview = employeeMonthlyReviewEntities.stream()
//                    .map(obj -> objectMapper.convertValue(obj, EmployeeMonthlyReviewResponse.class))
//                    .collect(Collectors.toList());
//            response.averageScore = KpiGrade.calculateAverageScore(employeeMonthlyReviewEntities.stream()
//                    .map(EmployeeMonthlyReviewEntity::getGrade)
//                    .collect(Collectors.toList()));
//
//        } catch (NumberFormatException ex) {
//            String stackTrace = ExceptionUtils.getStackTrace(ex);
//            log.error(stackTrace);
//        }
//
//        return response;
//    }
//
//    @Override
//    public List<EmployeeYearlyReviewResponse> getYearlyReviewList (Long resignSessionId) {
//        List<EmployeeYearlyReviewResponse> responseList = new ArrayList<>();
//        ResignSessionEntity resignSessionEntity = resignSessionRepository
//                .findById(resignSessionId)
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy đợt tái ký"));
//        List<Long> employeeIds = resignSessionEntity
//                .getResignSessionContractEntities()
//                .stream()
//                .map(obj -> obj.getContractEntity().getEmployeeId())
//                .collect(Collectors.toList());
//
//        employeeIds.forEach(employeeId -> {
//            responseList.add(getYearlyReview(employeeId, LocalDate.now()));
//        });
//
//        return responseList;
//    }

//    @EventListener(ApplicationReadyEvent.class)
//    private void createTempReview(ApplicationReadyEvent event) {
//        List<EmployeeMonthlyReviewEntity> list = new ArrayList<>();
//        LocalDate now = LocalDate.now().withDayOfMonth(1);
//
//        Random random = new Random();
//        int[] randomInt = random.ints(24,0, 10).toArray();
//        for (int i = 0; i < 24; i++) {
//            EmployeeMonthlyReviewEntity entity = new EmployeeMonthlyReviewEntity();
//            entity.setEmployeeId(900048617l);
//            entity.setMonth(now.minusMonths(i));
//            if (randomInt[i] >= 9) {
//                entity.setGrade("A");
//            } else if (randomInt[i] >= 5) {
//                entity.setGrade("B");
//            } else if (randomInt[i] >= 2) {
//                entity.setGrade("C");
//            } else {
//                entity.setGrade("D");
//            }
//
//            list.add(entity);
//        }
//
//        employeeMonthlyReviewRepository.saveAll(list);
//
//    }


}
