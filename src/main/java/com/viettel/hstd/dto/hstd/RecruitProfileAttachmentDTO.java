package com.viettel.hstd.dto.hstd;

//import com.viettel.hstd.entity.hstd.DocumentTypeEntity;
//import jdk.nashorn.internal.objects.annotations.Getter;

import com.viettel.hstd.constant.AttachmentDocumentStatus;

import javax.persistence.*;
import java.io.Serializable;

public class RecruitProfileAttachmentDTO {
    public static class RecruitProfileAttachmentRequest {
        public Long recProfileAttachmentId;
        public Long documentTypeId;
        public Long interviewSessionCvId;
        public Long accountId;
        public String fileName;
        public String filePath;
        public AttachmentDocumentStatus status;
        public String reason;
    }

    public static class RecruitProfileAttachmentResponse implements Serializable {
        public Long recProfileAttachmentId;
        public Long documentTypeId;
        public Long interviewSessionCvId;
        public Long accountId;
        public String fileName;
        public String filePath;
        public AttachmentDocumentStatus status = AttachmentDocumentStatus.PENDING;;
        public String reason;

    }
}
