package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.InsuranceStatus;
import com.viettel.hstd.core.custom.CustomLocalDateTimeDeserializer;
import com.viettel.hstd.core.custom.CustomLocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InsuranceSessionDTO {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InsuranceSessionRequest {
        private String sessionName;
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        private LocalDateTime doneAddingContractTimestamp;
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        private LocalDateTime doneDecreasingAndPrepareDocumentTimestamp;
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        private LocalDateTime doneSendingToBhxhTimestamp;
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        private LocalDateTime finishInsuranceProcedureTimestamp;
        private InsuranceStatus insuranceStatus = InsuranceStatus.JUST_ADD_INTO_INSURANCE_PROGRESS;

        private List<Long> terminateIds = new ArrayList<>();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InsuranceSessionResponse {
        private Long insuranceSessionId;
        private String sessionName;
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        private LocalDateTime doneAddingContractTimestamp;
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        private LocalDateTime doneDecreasingAndPrepareDocumentTimestamp;
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        private LocalDateTime doneSendingToBhxhTimestamp;
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        private LocalDateTime finishInsuranceProcedureTimestamp;
        private InsuranceStatus insuranceStatus = InsuranceStatus.JUST_ADD_INTO_INSURANCE_PROGRESS;
    }
}
