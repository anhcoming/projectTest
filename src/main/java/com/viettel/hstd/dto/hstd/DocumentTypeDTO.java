package com.viettel.hstd.dto.hstd;

import com.viettel.hstd.constant.ContractType;

public class DocumentTypeDTO {
    public static class DocumentTypeRequest {
        public Long documentTypeId;
        public String code;
        public String name;
        public Boolean obligateFlag;
        public ContractType type;
        public Boolean isActive;
        public RecruitProfileAttachmentDTO.RecruitProfileAttachmentRequest recruitProfileAttachment;
    }

    public static class DocumentTypeResponse {
        public Long documentTypeId;
        public String code;
        public String name;
        public Boolean obligateFlag;
        public ContractType type;
        public Boolean isActive;
        public RecruitProfileAttachmentDTO.RecruitProfileAttachmentResponse recruitProfileAttachment;
    }
}
