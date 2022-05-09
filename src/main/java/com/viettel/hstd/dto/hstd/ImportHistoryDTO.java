package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.viettel.hstd.constant.ImportConstant;
import com.viettel.hstd.core.dto.SearchDTO;
import lombok.*;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@UtilityClass
public class ImportHistoryDTO {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private Long employeeId;
        private String employeeCode;
        private String employeeName;
        private ImportConstant.ImportStatus importStatus;
        private String fileTitle;
        private String fileUrl;
        private ImportConstant.ImportCode importCode;
    }

    @Getter
    @Setter
    public static class SearchCriteria {
        private Long employeeId;
        private String employeeCode;
        private ImportConstant.ImportStatus importStatus;
        private ImportConstant.ImportCode importCode;
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
        private LocalDateTime fromImportDate;
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
        private LocalDateTime toImportDate;
        private Integer page = 0;
        private Integer size = 10;
        private List<SearchDTO.OrderDTO> sortList;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private String employeeCode;
        private String employeeName;
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
        private LocalDateTime importAt;
        private ImportConstant.ImportStatus importStatus;
        private String statusTitle;
        private ImportConstant.ImportCode importCode;
        private String importCodeTitle;
        private String fileTitle;
        private String fileUrl;
    }
}
