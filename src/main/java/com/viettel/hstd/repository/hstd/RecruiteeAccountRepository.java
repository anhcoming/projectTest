package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.RecruiteeAccountEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface RecruiteeAccountRepository extends SoftJpaRepository<RecruiteeAccountEntity, Long> {
    @Query("select u from  RecruiteeAccountEntity  u where u.interviewSessionCvEntity.interviewSessionCvId =?1")
    ArrayList<RecruiteeAccountEntity> findByInterviewCvId(Long interviewCvId);

    @Query(nativeQuery = true, value = "select * from RECRUITEE_ACCOUNT where INTERVIEW_SESSION_CV_ID = ?1")
    Optional<RecruiteeAccountEntity> getByInterviewSessionCvId(Long interviewSessionCvId);

    @Query("select u from  RecruiteeAccountEntity  u where u.interviewSessionCvEntity.interviewSessionCvId IN ?1")
    ArrayList<RecruiteeAccountEntity> findByInterviewCvIdIn(List<Long> interviewCvId);

    @Query("delete from  RecruiteeAccountEntity u where u.recruiteeAccountId  IN ?1")
    @Modifying
    @Transactional
    void deleteByRecruiteeAccountIdIn(ArrayList<Long> ids);

    ArrayList<RecruiteeAccountEntity> findByLoginNameContainingIgnoreCase(String loginName);

//    ArrayList<RecruiteeAccountEntity> findByLoginNameContainingIgnoreCase(String loginName);
}
