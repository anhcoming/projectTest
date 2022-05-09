package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.core.custom.CustomLocalDateTimeDeserializer;
import com.viettel.hstd.core.custom.CustomLocalDateTimeSerializer;

import java.time.LocalDateTime;

public class EmailCategoryDTO {
    public static class EmailCategoryRequest {
        public Long emailCategoryId;
        public String emailSend;
        public String name;
        public String content;
        public Long emailTemplateId;
        public Boolean isStatus;
        public Boolean isActive;
        public Long emailConfigId;
        public String createdName;
    }

    public static class EmailCategoryResponse {
        public Long emailCategoryId;
        public String emailSend;
        public String name;
        public String content;
        public Long emailTemplateId;
        public Boolean isStatus;
        public Boolean isActive;
        public Long emailConfigId;
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        public LocalDateTime createdAt;
        public String createdName;
    }
}
