package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.constant.AttachmentDocumentStatus;
import com.viettel.hstd.core.entity.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "RECRUIT_PROFILE_ATTACHMENT")
@SequenceGenerator(name = "RECRUIT_PROFILE_ATTACHMENT_GEN", initialValue = 1, allocationSize = 1, sequenceName = "RECRUIT_PROFILE_ATTACHMENT_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class RecruitProfileAttachmentEntity extends EntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECRUIT_PROFILE_ATTACHMENT_GEN")
    @Column(name = "RECRUIT_PROFILE_ATTACHMENT_ID")
    private Long recProfileAttachmentId;
    @Column(name = "ACCOUNT_ID")
    private Long accountId;
    @Column(name = "FILE_NAME")
    private String fileName;
    @Column(name = "FILE_PATH")
    private String filePath;
    @Column(name = "INTERVIEW_SESSION_CV_ID")
    private Long interviewSessionCvId;

    @Column(name = "DOCUMENT_TYPE_ID")
    private Long documentTypeId;
    /**
     * Trang thai tai lieu
     */
    @Column(name = "STATUS")
    private AttachmentDocumentStatus status = AttachmentDocumentStatus.PENDING;
    /**
     * Ly do tra ve neu co
     */
    @Column(name = "REASON")
    private String reason;
}
