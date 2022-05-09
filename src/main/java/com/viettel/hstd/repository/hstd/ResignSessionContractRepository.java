package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.constant.ResignStatus;
import com.viettel.hstd.constant.ResignType;
import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.ResignSessionContractEntity;
import com.viettel.hstd.entity.hstd.ResignSessionContractIdAndContractId;
import com.viettel.hstd.entity.hstd.ResignSessionEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ResignSessionContractRepository extends SoftJpaRepository<ResignSessionContractEntity, Long> {
    @Query("UPDATE ResignSessionContractEntity t set t.resignStatus = ?2 where t.resignSessionContractId = ?1")
    @Modifying
    @Transactional
    void updateResignStatusByResignContractSet(Long contractId, ResignStatus resignStatus);

    @Query("UPDATE ResignSessionContractEntity t set t.resignStatus = ?2 where t.resignSessionContractId in ?1")
    @Modifying
    @Transactional
    void updateResignStatusByResignContractSet(Set<Long> resignContractIds, ResignStatus resignStatus);

    @Query("UPDATE ResignSessionContractEntity t set t.resignStatus = ?2 where t.resignSessionEntity.resignSessionId in ?1")
    @Modifying
    @Transactional
    void updateResignStatusByResignIdSet(Set<Long> resignIdSet, ResignStatus resignStatus);

    @Query("UPDATE ResignSessionContractEntity t set t.resignStatus = ?2 where t.resignSessionEntity.resignSessionId = ?1")
    @Modifying
    @Transactional
    void updateResignStatusByResignId(Long resignId, ResignStatus resignStatus);

    @Query("UPDATE ResignSessionContractEntity t set t.resignStatus = ?2 where t.resignSessionEntity.resignSessionId in ?1")
    @Modifying
    @Transactional
    void updateResignStatusWithResignSession(Set<Long> resignIds, ResignStatus resignStatus);

    @Query(value = "select t.resignSessionContractId from ResignSessionContractEntity t " +
            "where t.resignStatus in ?3 and t.resignSessionEntity.startDate >= ?1 and t.resignSessionEntity.endDate <= ?2")
    Set<Long> getAllResignContractByStartDateEndDateResignStatus(LocalDate startDate, LocalDate endDate, Set<ResignStatus> resignStatusSet);

    @Query(value = "select t from ResignSessionContractEntity t where t.resignStatus = ?1 and t.resignType = ?2")
    List<ResignSessionContractEntity> getAllResignContractToSendVoffice2(ResignStatus resignStatus, ResignType resignType);

    @Query(value = "select new com.viettel.hstd.entity.hstd.ResignSessionContractIdAndContractId(t.contractEntity.contractId, t.resignSessionContractId) from ResignSessionContractEntity t where t.resignSessionEntity.resignSessionId = ?1")
    List<ResignSessionContractIdAndContractId> getAllResignContractFromResign(Long resignId);

    @Query(value = "select  T FROM ResignSessionContractEntity  T WHERE  T.contractEntity.employeeCode = ?1")
    List<ResignSessionContractEntity> getByEmployeeCode(String employeeCode);

    @Query(value = "SELECT RSC FROM ResignSessionContractEntity  RSC WHERE  RSC.contractEntity.contractId = ?1")
    List<ResignSessionContractEntity> findByContractId(Long contractId);


}