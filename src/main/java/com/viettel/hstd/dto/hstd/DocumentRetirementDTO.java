package com.viettel.hstd.dto.hstd;

public class DocumentRetirementDTO {
    public static class DocumentRetirementRequest {
        public Long documentTypeId;
        public String code;
        public String name;
        public Boolean obligateFlag;
        public Boolean isActive;
    }

    public static class DocumentRetirementResponse {
        public Long documentTypeId;
        public String code;
        public String name;
        public Boolean obligateFlag;
        public Boolean isActive;
    }
}
