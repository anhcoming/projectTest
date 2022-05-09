package com.viettel.hstd.service.imp;

import com.viettel.hstd.constant.InsuranceStatus;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.dto.hstd.InsuranceTerminateDTO;
import com.viettel.hstd.entity.hstd.InsuranceSessionEntity;
import com.viettel.hstd.entity.hstd.InsuranceTerminateEntity;
import com.viettel.hstd.entity.hstd.TerminateContractEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.InsuranceSessionRepository;
import com.viettel.hstd.repository.hstd.InsuranceTerminateRepository;
import com.viettel.hstd.service.inf.InsuranceTerminateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class InsuranceTerminateImp implements InsuranceTerminateService {

    @Autowired
    private InsuranceTerminateRepository insuranceTerminateRepository;
    @Autowired
    private InsuranceSessionRepository insuranceSessionRepository;

    @Override
    public Page<InsuranceTerminateDTO.InsuranceTerminateResponse> findPage(SearchDTO searchDTO) {
        return InsuranceTerminateService.super.findPage(searchDTO);
    }

    @Override
    public InsuranceTerminateDTO.InsuranceTerminateResponse findOneById(Long aLong) {
        return null;
    }

    @Override
    public Boolean delete(Long aLong) {
        return null;
    }

    @Override
    public InsuranceTerminateDTO.InsuranceTerminateResponse create(InsuranceTerminateDTO.InsuranceTerminateRequest request) {
        return InsuranceTerminateService.super.create(request);
    }

    @Override
    public InsuranceTerminateDTO.InsuranceTerminateResponse update(Long aLong, InsuranceTerminateDTO.InsuranceTerminateRequest request) {
        return InsuranceTerminateService.super.update(aLong, request);
    }

    @Override
    public void addContractToInsuranceSession(Set<Long> contractIds, Long insuranceSessionId) {
        InsuranceSessionEntity insuranceSession = insuranceSessionRepository.findById(insuranceSessionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đợt chốt sổ"));

        Set<Long> existingInsuranceTerminateIds = insuranceSession.getInsuranceTerminateEntityList()
                .stream()
                .map(obj -> obj.getTerminateContractEntity().getTerminateContractId())
                        .collect(Collectors.toSet());

        contractIds.forEach(contractId -> {
            if (existingInsuranceTerminateIds.contains(contractId)) return;
            InsuranceTerminateEntity insuranceTerminate = new InsuranceTerminateEntity();
            insuranceTerminate.setInsuranceSessionEntity(insuranceSession);
            TerminateContractEntity terminateContractEntity = new TerminateContractEntity();
            terminateContractEntity.setTerminateContractId(contractId);
            insuranceTerminate.setTerminateContractEntity(terminateContractEntity);

            insuranceSession.getInsuranceTerminateEntityList().add(insuranceTerminate);
        });

        insuranceSessionRepository.save(insuranceSession);
    }

    @Override
    public void removeContractToInsuranceSession(Set<Long> contractIds, Long insuranceSessionId) {
        InsuranceSessionEntity insuranceSession = insuranceSessionRepository.findById(insuranceSessionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đợt chốt sổ"));

        insuranceSession.getInsuranceTerminateEntityList().removeIf(obj -> contractIds.contains(obj.getTerminateContractEntity().getTerminateContractId()));

        insuranceSessionRepository.save(insuranceSession);
    }

    @Override
    public void startPreparingDocumentAndDecreasingAnnounce(List<Long> insuranceSessionIdSet) {
        List<InsuranceTerminateEntity> insuranceTerminateEntityList = insuranceTerminateRepository.findAllById(insuranceSessionIdSet);

        insuranceTerminateEntityList.parallelStream().forEach(insuranceTerminateEntity -> {
            insuranceTerminateEntity.setInsuranceStatus(InsuranceStatus.IN_PREPARING_AND_DECREASING_PROGRESS);
        });

        insuranceTerminateRepository.saveAll(insuranceTerminateEntityList);
    }

    @Override
    public void failDecreasingAnnounce(Set<Long> insuranceSessionIdSet) {
    }

    @Override
    public void finishDecreasingAnnounce(Set<Long> insuranceSessionIdSet) {

    }

    @Override
    public void finishPreparingDocument(Set<Long> insuranceSessionIdSet) {

    }

    @Override
    public void startSentToBHXH(Set<Long> insuranceSessionIdSet) {

    }

    @Override
    public void failBHXH(Set<Long> insuranceSessionIdSet) {

    }

    @Override
    public void finishBHXH(Set<Long> insuranceSessionIdSet) {

    }
}
