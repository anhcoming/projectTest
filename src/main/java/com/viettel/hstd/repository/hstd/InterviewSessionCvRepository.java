package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.constant.NewContractStatus;
import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.CvEntity;
import com.viettel.hstd.entity.hstd.InterviewSessionCvEntity;
import com.viettel.hstd.entity.hstd.InterviewSessionEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface InterviewSessionCvRepository extends SoftJpaRepository<InterviewSessionCvEntity, Long> {
    @Query("select u from  InterviewSessionCvEntity  u where u.interviewSessionEntity.interviewSessionId =?1")
    ArrayList<InterviewSessionCvEntity> findByInterviewSessionId(Long sessionInterviewId);

    @Query("select u from  InterviewSessionCvEntity  u where u.cvEntity.cvId =?1")
    ArrayList<InterviewSessionCvEntity> findByCvId(Long cvId);

    @Query("select u from  InterviewSessionCvEntity  u where u.interviewSessionCvId IN ?1")
    ArrayList<InterviewSessionCvEntity> findByIdIn(ArrayList<Long> ids);

    InterviewSessionCvEntity findByTransCode(String transCode);

    @Query(value = "SELECT ISC FROM InterviewSessionCvEntity ISC" +
            " inner join EmployeeVhrTempEntity EVT ON ISC.interviewSessionCvId = EVT.interviewSessionCvId " +
            " inner join ContractEntity C on C.employeeCode = EVT.employeeCode " +
            " where C.contractId = ?1")
    List<InterviewSessionCvEntity> getFromContract(Long contractId);

    @Query(value = "SELECT CASE WHEN COUNT(ISC) > 0 THEN TRUE ELSE FALSE END FROM InterviewSessionCvEntity ISC" +
            " inner join EmployeeVhrTempEntity EVT ON ISC.interviewSessionCvId = EVT.interviewSessionCvId " +
            " inner join ContractEntity C on C.employeeCode = EVT.employeeCode " +
            " where ISC.interviewSessionCvId = ?1 ")
    Boolean isHasContract(Long interviewSessionCvId);

    @Query(value = "SELECT CASE WHEN COUNT(ISC) > 0 THEN TRUE ELSE FALSE END FROM InterviewSessionCvEntity ISC" +
            " inner join EmployeeVhrTempEntity EVT ON ISC.interviewSessionCvId = EVT.interviewSessionCvId " +
            " inner join ContractEntity C on C.employeeCode = EVT.employeeCode " +
            " where ISC.interviewSessionCvId IN ?1 ")
    Boolean isHasContract(List<Long> interviewSessionCvIds);
}
