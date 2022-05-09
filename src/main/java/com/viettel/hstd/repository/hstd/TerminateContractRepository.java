package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.constant.InsuranceStatus;
import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.TerminateContractEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface TerminateContractRepository extends SoftJpaRepository<TerminateContractEntity, Long> {
    TerminateContractEntity findByTransCode(String transCode);

    TerminateContractEntity findByContractId(Long contractId);

    ArrayList<TerminateContractEntity> findByEmployeeCode(String employeeCode);

    int countByManagerIdAndStatusIn(Long managerId, Set<Integer> statusSet);

    @Query(value = "update InsuranceSessionEntity set insuranceStatus = ?2 where insuranceSessionId in ?1")
    boolean updateInsuranceStatus(Set<Long> setTerminateIds, InsuranceStatus insuranceStatus);

    Optional<TerminateContractEntity> findByTransCodeSevAllowance(String transCode);

    @Query("SELECT  T FROM TerminateContractEntity  T where  T.terminateContractId IN ?1 AND  T.status = ?2 AND T.sevAllowancePath IS NOT NULL")
    List<TerminateContractEntity> findByTerminateContractIdInAndHasSevAllowance(List<Long> ids, Integer status);

    @Query("SELECT T FROM TerminateContractEntity T JOIN VoSignEntity  VO ON VO.transCode  = T.transCodeSevAllowance WHERE  VO.transCode = ?1")
    List<TerminateContractEntity> findByTransCodeMulti(String transCode);
}
