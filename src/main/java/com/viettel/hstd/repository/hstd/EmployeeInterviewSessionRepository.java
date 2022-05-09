package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.EmployeeInterviewSessionEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;

public interface EmployeeInterviewSessionRepository extends SoftJpaRepository<EmployeeInterviewSessionEntity, Long> {
    @Query("select u from  EmployeeInterviewSessionEntity  u where  u.interviewSessionEntity.interviewSessionId =?1")
    ArrayList<EmployeeInterviewSessionEntity> findByInterviewSessionIdIn(Long sessionInterviewId);
}
