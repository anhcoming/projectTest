package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.EmployeeVhrTempEntity;
import com.viettel.hstd.entity.vps.EmployeeVhrEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface EmployeeVhrTempRepository extends SoftJpaRepository<EmployeeVhrTempEntity, Long> {
    EmployeeVhrTempEntity findByInterviewSessionCvId(Long interviewSessionCvId);

    Optional<EmployeeVhrTempEntity> findFirstByInterviewSessionCvId(Long interviewSessionCvId);

//    EmployeeVhrTempEntity findByEmployeeId(Long employeeId);

    EmployeeVhrTempEntity findByEmployeeCode(String employeeCode);

    @Query("update EmployeeVhrTempEntity  set isLock = ?1 where employeeVhrTempId in ?2 and delFlag = false")
    @Modifying
    @Transactional
    int lock(boolean isLock, ArrayList<Long> ids);

    @Query("update EmployeeVhrTempEntity  set status = ?1 where employeeVhrTempId = ?2 and delFlag = false")
    @Modifying
    @Transactional
    int approval(int status, Long id);

    @Query("update EmployeeVhrTempEntity  set newContractStatus = ?1 where employeeVhrTempId = ?2")
    @Modifying
    @Transactional
    int updateProbationaryStatus(int status, Long id);

    EmployeeVhrTempEntity findByInterviewSessionCvIdIn(List<Long> ids);

    @Query(nativeQuery = true, value = "select EMPLOYEE_VHR_TEMP_CODE_SEQ.nextval from DUAL")
    Long generateEmployeeVhrTempCode();

//    EmployeeVhrTempEntity findByEmployeeId(Integer employeeId);

    Optional<EmployeeVhrTempEntity> findByEmployeeId(Long employeeId);
}
