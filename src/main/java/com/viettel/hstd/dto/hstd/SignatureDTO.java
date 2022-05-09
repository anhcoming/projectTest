package com.viettel.hstd.dto.hstd;

import javax.persistence.Column;

public class SignatureDTO {
    public static class SignatureRequest {
        public Long accountId;
        public Long employeeId;
        public String employeeCode;
        public String filePath;
        public String fileName;
    }

    public static class SignatureResponse {
        public Long signatureId;
        public Long accountId;
        public Long employeeId;
        public String employeeCode;
        public String filePath;
        public String fileName;
    }
}
