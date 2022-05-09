package com.viettel.hstd.dto.hstd;

import com.viettel.hstd.constant.DecreaseStatus;
import com.viettel.hstd.constant.InsuranceStatus;
import com.viettel.hstd.constant.PrepareDocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class InsuranceTerminateDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InsuranceTerminateRequest {
        private Long insuranceSessionTerminateContractId;
        private InsuranceStatus insuranceStatus = InsuranceStatus.JUST_ADD_INTO_INSURANCE_PROGRESS;
        private DecreaseStatus decreaseStatus = DecreaseStatus.NOT_IN_DECREASE_PROGRESS;
        private PrepareDocumentStatus prepareDocumentStatus = PrepareDocumentStatus.NOT_IN_PREPARE_DOCUMENT_PROGRESS;

        private InsuranceSessionDTO.InsuranceSessionResponse insuranceSessionResponse;
        private TerminateContractDTO.TerminateContractResponse terminateContractResponse;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InsuranceTerminateResponse {
        private Long insuranceSessionTerminateContractId;
        private InsuranceStatus insuranceStatus = InsuranceStatus.JUST_ADD_INTO_INSURANCE_PROGRESS;
        private DecreaseStatus decreaseStatus = DecreaseStatus.NOT_IN_DECREASE_PROGRESS;
        private PrepareDocumentStatus prepareDocumentStatus = PrepareDocumentStatus.NOT_IN_PREPARE_DOCUMENT_PROGRESS;

        private InsuranceSessionDTO.InsuranceSessionResponse insuranceSessionResponse;
        private TerminateContractDTO.TerminateContractResponse terminateContractResponse;
    }

}
