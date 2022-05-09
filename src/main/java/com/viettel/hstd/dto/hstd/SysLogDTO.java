package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SysLogDTO {
    public static class SysLogRequest {
        public long sysLogId;
        public Long sysUserId;
        public String workStation;
        public String content;
        public String tableName;
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
        public LocalDateTime createdAt;
    }

    public static class SysLogResponse {
        public long sysLogId;
        public Long sysUserId;
        public String workStation;
        public String content;
        public String tableName;
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
        public LocalDateTime createdAt;
        public String loginName;
        public String fullName;
    }
}
