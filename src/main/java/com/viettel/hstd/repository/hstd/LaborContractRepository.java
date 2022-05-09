package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.constant.ResignStatus;
import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.LaborContractEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LaborContractRepository extends SoftJpaRepository<LaborContractEntity, Long> {
    @Query("UPDATE ContractEntity t set t.resignStatus = ?2 where t.contractId = ?1")
    @Modifying
    @Transactional
    void updateResignStatus(Long contractId, ResignStatus resignStatus);

    @Query(value = "UPDATE ContractEntity t set t.resignStatus = ?2 where t.contractId in ?1")
    @Modifying
    @Transactional
    Integer updateResignStatus(Set<Long> contractIds, int resignStatus);

    @Query(value = "UPDATE ContractEntity t set t.isActive = true where t.contractId in ?1")
    @Modifying
    @Transactional
    Integer updateIsActiveTrue(Set<Long> contractIds);

    Optional<LaborContractEntity> findByEmployeeCode(String employeeCode);
}

