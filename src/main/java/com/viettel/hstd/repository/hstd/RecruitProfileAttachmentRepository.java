package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.constant.AttachmentDocumentStatus;
import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.RecruitProfileAttachmentEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.viettel.hstd.dto.hstd.RecruitProfileAttachmentDTO.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface RecruitProfileAttachmentRepository extends SoftJpaRepository<RecruitProfileAttachmentEntity, Long> {
    ArrayList<RecruitProfileAttachmentEntity> findByAccountId(Long accountId);

    List<RecruitProfileAttachmentEntity> findByInterviewSessionCvId(Long interviewSessionCvId);

    @Query("update RecruitProfileAttachmentEntity  set status = ?1 where recProfileAttachmentId = ?2 and delFlag = false")
    @Modifying
    @Transactional
    Integer approval(AttachmentDocumentStatus status, Long id);

    RecruitProfileAttachmentEntity findByRecProfileAttachmentId(Long recProfileAttachmentId);

    @Query("select  r from  RecruitProfileAttachmentEntity r where r.recProfileAttachmentId in ?1 and  r.delFlag = false ")
    List<RecruitProfileAttachmentEntity> getByIds(List<Long> recProfileAttachmentIds);
}
