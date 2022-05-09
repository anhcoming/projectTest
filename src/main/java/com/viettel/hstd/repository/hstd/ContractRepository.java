package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.constant.ContractDuration;
import com.viettel.hstd.constant.ContractType;
import com.viettel.hstd.constant.NewContractStatus;
import com.viettel.hstd.constant.ResignStatus;
import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.ContractEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

public interface ContractRepository extends SoftJpaRepository<ContractEntity, Long> {
    ContractEntity findByTransCode(String transCode);

    Optional<ContractEntity> findFirstByTransCode(String transCode);

    Optional<ContractEntity> findByEmployeeIdAndIsActiveTrue(Long employeeId);

    ArrayList<ContractEntity> findByEmployeeCodeAndIsActiveTrue(String employeeCode);

    ArrayList<ContractEntity> findByEmployeeCode(String employeeCode);

    Optional<ContractEntity> findFirstByEmployeeCode(String employeeCode);

    ArrayList<ContractEntity> findByAccountIdAndIsActiveTrue(Long accountId);

    ContractEntity findFirstByTransCodeAndIsActiveTrue(String transcode);

    @Query(value = "update ContractEntity t set t.resignStatus = ?2 where t.contractId in ?1")
    @Modifying
    @Transactional
    Integer updateResignStatusByContractIdSet(Set<Long> contractIds, ResignStatus resignStatus);

    @Query("UPDATE ContractEntity t set t.newContractStatus = ?2 where t.contractId = ?1")
    @Modifying
    @Transactional
    void updateNewContractStatus(Long contractId, NewContractStatus newContractStatusInt);

    @Query("UPDATE ContractEntity t set t.signedFileEncodePath = ?2, t.contractFileEncodePath = ?3 where t.contractId = ?1")
    @Modifying
    @Transactional
    void updateSignedFile(Long contractId, String signedFile, String contractFile);

    @Query("UPDATE ContractEntity t set t.transCode = ?2 where t.contractId = ?1")
    @Modifying
    @Transactional
    void updateTranscode(Long contractId, String transcode);

    @Query(nativeQuery = true, value = "select CONTRACT_SEQ.nextval from DUAL connect by LEVEL < ?1 + 1")
    List<Long> getSequence(int total);

    @Query(value = "SELECT T.contractId FROM ContractEntity T WHERE T.transCode = ?1")
    Set<Long> getAllContractIdByTranscode(String transcode);

    @Query("UPDATE ContractEntity t set t.signedFileEncodePath=t.contractFileEncodePath where t.contractId in ?1")
    @Modifying
    @Transactional
    void updateContractFileEncodeToContractFileAndSignedFile(Set<Long> contractIdSet);

    @Query("SELECT T.mobileNumber FROM ContractEntity T WHERE T.contractId = ?1")
    Optional<String> getPhoneNumberByContractId(Long contractIds);

    @Query("SELECT T FROM ContractEntity T WHERE T.newContractStatus in ?1")
    List<ContractEntity> getContractByNewContractStatusSet(Set<NewContractStatus> newContractStatusSet);

    @Query("SELECT T FROM ContractEntity T WHERE T.employeeCode = ?1 and ?2 <= T.effectiveDate and T.expiredDate <= ?2  ")
    List<ContractEntity> getCurrentContract(String employeeCode, LocalDate now);

    @Query(value = "SELECT  T FROM ContractEntity  T WHERE T.transCode = ?1")
    List<ContractEntity> getListContractByTransCode(String transCode);

    @Query(value = "SELECT  T FROM ContractEntity  T  WHERE  T.employeeCode IN ?1")
    List<ContractEntity> getContractEntitiesByEmployeeCodes(List<String> employees);

    @Query(value = "SELECT T FROM  ContractEntity  T WHERE  T.employeeCode =?1 and T.effectiveDate = ?2 and T.contractDuration = ?3 and T.expiredDate = ?4 ")
    Optional<ContractEntity> getContractEntityExist(String employeeCode, LocalDate effectiveDate, ContractDuration duration, LocalDate expiredDate);

    @Query(value = "SELECT T FROM  ContractEntity  T  WHERE T.contractId = (SELECT RSC.newContractId FROM  ResignSessionContractEntity  RSC WHERE RSC.contractEntity.contractId = ?1) " +
            "AND T.isActive = false")
    List<ContractEntity> getNewContract(Long contractId);

    @Query(value = "SELECT T FROM ContractEntity  T WHERE  T.employeeCode = ?1 AND T.isTerminate <> true")
    List<ContractEntity> getContractActiveByEmployeeCode(String employeeCode);

    Optional<ContractEntity> findByContractId(Long contractId);
}
