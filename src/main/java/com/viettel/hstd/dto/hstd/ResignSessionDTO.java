package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.ResignStatus;
import com.viettel.hstd.constant.ResignType;
import com.viettel.hstd.constant.ResignVofficeStatus;
import com.viettel.hstd.core.custom.CustomLocalDateTimeDeserializer;
import com.viettel.hstd.core.custom.CustomLocalDateTimeSerializer;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
//import com.viettel.hstd.dto.hstd.ResignSessionEmployeeDTO.*;
import com.viettel.hstd.dto.hstd.ResignSessionContractDTO.*;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;

public class ResignSessionDTO {
    public static class ResignSessionRequest {
        @NotNull
        public Long unitId;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate summitDate;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate approveDate;
        public ResignVofficeStatus status = ResignVofficeStatus.NOT_SEND_YET;
        public int year = LocalDate.now().getYear();
        public int quarter = LocalDate.now().get(IsoFields.QUARTER_OF_YEAR);
        public String name = "Tên hợp đồng";
        public String bm09EncodePath;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate startDate;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate endDate;
        public ResignType resignType = ResignType.LABOR;
        public String bmtctEncodePath;

//        public List<ResignSessionEmployeeRequest> resignSessionEmployeeList = new ArrayList<>();
        public List<Long> contractIds = new ArrayList<>();
    }

    public static class ResignSessionResponse {
        public Long resignSessionId;
        public Long unitId;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate summitDate;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate approveDate;
        public ResignVofficeStatus status = ResignVofficeStatus.NOT_SEND_YET;
        public int year = LocalDate.now().getYear();
        public int quarter = LocalDate.now().get(IsoFields.QUARTER_OF_YEAR);
        public String name = "Tên hợp đồng";
        public String unitName;
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        public LocalDateTime createdAt;
        public String bm09EncodePath;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate startDate;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate endDate;
        public ResignType resignType = ResignType.LABOR;
        public String bmtctEncodePath;
        public ResignStatus resignStatus = ResignStatus.IN_EVALUATION;

//        public List<ResignSessionEmployeeResponse> resignSessionEmployeeList = new ArrayList<>();
        public List<ResignSessionContractResponse> resignSessionContractResponses = new ArrayList<>();
    }

    public static class Bm07Metadata {
        public int quarter;
        public int year;
        public List<EmployeeMonthlyReviewDTO.EmployeeMonthlyReviewResponse> monthlyReviewResponseList;
    }

}
