package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.constant.ContractType;
import com.viettel.hstd.constant.ResignStatus;
import com.viettel.hstd.constant.ResignType;
import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.ResignSessionEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface ResignSessionRepository extends SoftJpaRepository<ResignSessionEntity, Long> {
    Optional<ResignSessionEntity> findFirstByTranscode(String transcode);

    List<ResignSessionEntity> findByTranscode(String transcode);

    @Query(nativeQuery = true, value = "update RESIGN_SESSION\n" +
            "set STATUS = ?2\n" +
            "where RESIGN_SESSION_ID in ?1")
    @Modifying
    @Transactional
    Integer updateResignVofficeStatus(Long resignSessionId, int resignVofficeStatus);

    @Query("UPDATE ResignSessionEntity t set t.resignStatus = ?2 where t.resignSessionId = ?1")
    @Modifying
    @Transactional
    void updateResignStatusByResignId(Long resignSessionId, ResignStatus resignStatus);

    @Query(value = "select t.resignSessionId from ResignSessionEntity t where t.quarter = ?1 and t.year = ?2 and t.resignStatus = ?3 and t.resignType = ?4")
    Set<Long> getAllResignIdByQuarterYearResignStatusAndType(int quarter, int year, ResignStatus resignStatus, ResignType resignType);

    @Query(value = "select t from ResignSessionEntity t where t.quarter = ?1 and t.year = ?2 and t.resignStatus in ?3 and t.resignType = ?4")
    List<ResignSessionEntity> getAllResignByStartEndDateResignStatusAndType(int quarter, int year, Set<ResignStatus> resignStatusSet, ResignType resignType);

    @Query(value = "select t.resignSessionId from ResignSessionEntity t where t.startDate = ?1 and t.endDate = ?2 and t.resignStatus = ?3 and t.resignType = ?4")
    Set<Long> getAllResignIdByQuarterYearResignStatusAndType(LocalDate startDate, LocalDate endDate, ResignStatus resignStatus, ResignType resignType);

    @Query(value = "select t from ResignSessionEntity t where " +
            "t.startDate <= ?2 and t.endDate >= ?1 and t.resignStatus in ?3 and t.resignType = ?4")
    List<ResignSessionEntity> getAllResignByStartEndDateResignStatusAndType(LocalDate startDate, LocalDate endDate, Set<ResignStatus> resignStatusSet, ResignType resignType);

    @Query("UPDATE ResignSessionEntity t set t.resignStatus = ?2 where t.resignSessionId in ?1")
    @Modifying
    @Transactional
    void updateResignStatusByResignIdSet(Set<Long> resignIdSet, ResignStatus resignStatus);

    @Query("UPDATE ResignSessionEntity t set t.bmtctEncodePath = ?2, t.bmtctDocxEncodePath = ?3 where t.resignSessionId in ?1")
    @Modifying
    @Transactional
    void updateBMTCTFilePathByResignIdSet(Set<Long> resignIdSet, String bmTCTPdfFilePath, String bmTCTDocxFilePath);

    @Query("UPDATE ResignSessionEntity t set t.bmtctEncodePath = ?2 where t.resignSessionId in ?1")
    @Modifying
    @Transactional
    void updateBMTCTPdfFilePathByResignIdSet(Set<Long> resignIdSet, String bmTCTPdfFilePath);

    @Query("UPDATE ResignSessionEntity t set t.bmtctDocxEncodePath = ?2 where t.resignSessionId in ?1")
    @Modifying
    @Transactional
    void updateBMTCTDocxFilePathByResignIdSet(Set<Long> resignIdSet, String bmTCTDocxFilePath);

    @Query("UPDATE ResignSessionEntity t set t.bm09EncodePath = ?2 where t.resignSessionId in ?1")
    @Modifying
    @Transactional
    void updateBMUnitFilePathByResignIdSet(Set<Long> resignIdSet, String bmUnitPdfFilePath);

    // Transcode related
    @Query("UPDATE ResignSessionEntity t set t.transcode = ?2 where t.resignSessionId = ?1")
    @Modifying
    @Transactional
    void updateTranscodeByResignId(Long resignSessionId, String transcode);

    @Query("UPDATE ResignSessionEntity t set t.transcode = ?2 where t.resignSessionId in ?1")
    @Modifying
    @Transactional
    void updateTranscodeByResignIdSet(Set<Long> resignSessionIdSet, String transcode);

    @Query(value = "select t.resignSessionId from ResignSessionEntity t where t.quarter = ?1 and t.year = ?2 and t.resignType = ?3 and t.resignStatus >= ?4 and t.resignStatus <= ?5")
    Set<Long> getAllResignIdByQuarterYearResignType(int quarter, int year, ResignType resignType, ResignStatus startResignStatus, ResignStatus endResignStatus);

    @Query(value = "select t.resignSessionId from ResignSessionEntity t where t.quarter = ?1 and t.year = ?2 and t.resignType = ?3")
    Set<Long> getAllResignIdByQuarterYearResignType(LocalDate startDate, LocalDate year, ResignType resignType);

    @Query("UPDATE ResignSessionEntity t set t.transcode = ?4 where t.quarter = ?1 and t.year = ?2 and t.resignType = ?3")
    @Modifying
    @Transactional
    void updateTranscodeByResignIdSet(int quarter, int year, ResignType resignType, String transcode);

    @Query("UPDATE ResignSessionEntity t set t.transcode = ?4 where t.startDate >= ?1 and t.endDate <= ?2 and t.resignType = ?3")
    @Modifying
    @Transactional
    void updateTranscodeByResignIdSet(LocalDate startDate, LocalDate endDate, ResignType resignType, String transcode);

    @Query(value = "select t.resignSessionId from ResignSessionEntity t where t.startDate >= ?1 and t.endDate <= ?2 and t.resignType = ?3")
    Set<Long> getAllResignIdByStartDateEndDateResignType(LocalDate startDate, LocalDate endDate, ResignType resignType);

    @Query("UPDATE ResignSessionEntity t set t.bmListEncodePath = ?2 where t.resignSessionId in ?1")
    @Modifying
    @Transactional
    void updateBmListEncodePathByResignIdSet(Set<Long> resignIdSet, String bmTCTPdfFilePath);

    @Query("UPDATE ResignSessionEntity t set t.reportPath = ?2 where t.resignSessionId in ?1")
    @Modifying
    @Transactional
    void updateReportPathByResignIdSet(Set<Long> resignIdSet, String reportPath);

    @Query(value = "select t.resignSessionId from ResignSessionEntity t " +
            "where t.resignStatus in ?3 and t.startDate >= ?1 and t.endDate <= ?2")
    Set<Long> getAllResignByStartDateEndDateResignStatus(LocalDate startDate, LocalDate endDate, Set<ResignStatus> resignStatusSet);

    @Query(nativeQuery = true, value = "select distinct C.*\n" +
            "      from CONTRACT C\n" +
            "               left outer join RESIGN_SESSION_CONTRACT RSC\n" +
            "                               on C.CONTRACT_ID = RSC.CONTRACT_ID and (RSC.DEL_FLAG = 0)\n" +
            "               left outer join RESIGN_SESSION RS\n" +
            "                               on RSC.RESIGN_SESSION_ID = RS.RESIGN_SESSION_ID\n" +
            "      where (C.DEL_FLAG = 0)\n" +
            "        and (RSC.RESIGN_STATUS is null or RS.RESIGN_SESSION_ID = ?1)\n" +
            "        and C.RESIGN_STATUS < 1\n" +
            "        and C.UNIT_ID = ?2\n" +
            "        and C.CONTRACT_TYPE = ?5\n" +
            "        and C.EXPIRED_DATE >= ?3\n" +
            "        and C.EXPIRED_DATE <= ?4")
    Set<Long> getAllAvailableContractsForResignSessionUnit(Long resignId, Long unitId, LocalDate startDate, LocalDate endDate, ContractType contractType);

    @Query("SELECT  T FROM  ResignSessionEntity  T where T.resignSessionId IN ?1 ")
    List<ResignSessionEntity> findByListIds(List<Long> resignSessionId);

    @Query("SELECT T FROM  ResignSessionEntity  T where  T.quarter =?1 AND T.year = ?2 AND T.unitId = ?3 AND T.resignType = ?4")
    List<ResignSessionEntity> getResignSessionBySessionOfUnit(int quarter, int year, Long unitId, ResignType resignType);

    @Query("SELECT T FROM  ResignSessionEntity  T where  T.startDate =?1 AND T.endDate = ?2 AND T.unitId = ?3 AND T.resignType = ?4")
    List<ResignSessionEntity> getResignSessionBySessionOfUnit(LocalDate startDate, LocalDate endDate, Long unitId, ResignType resignType);
}
