package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.InterviewSessionEntity;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public interface InterviewSessionRepository extends SoftJpaRepository<InterviewSessionEntity, Long> {
    boolean existsByStartDateGreaterThanEqual(LocalDateTime localDateTime);

    @Query("select u from  InterviewSessionEntity u where u.positionId =?1 AND u.startDate >= ?2 AND u.endDate <= ?3")
    ArrayList<InterviewSessionEntity> existsByPositionId(Long positionId, LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "select case when count(i) > 0 then true else false end from  InterviewSessionEntity i " +
            "inner join InterviewSessionCvEntity  ins on ins.interviewSessionEntity.interviewSessionId = i.interviewSessionId " +
            "inner join EmployeeVhrTempEntity evt on ins.interviewSessionCvId = evt.interviewSessionCvId " +
            " inner join ContractEntity  c on c.employeeCode = evt.employeeCode " + " where i.interviewSessionId = ?1")
    Boolean isHasContract(Long interviewSessionId);

}
