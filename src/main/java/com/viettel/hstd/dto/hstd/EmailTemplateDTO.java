package com.viettel.hstd.dto.hstd;

public class EmailTemplateDTO {
    public static class EmailTemplateRequest {
        public String name;
        public String content;
        public String fileName;
        public String filePath;
        public Integer type;
    }

    public static class EmailTemplateResponse {
        public Long emailTemplateId;
        public String name;
        public String content;
        public Integer type;
        public String fileName;
        public String filePath;
    }
}
