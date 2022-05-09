package com.viettel.hstd.dto.hstd;


public class ServiceContractDTO {
    public static class ServiceContractRequest extends ContractDTO.ContractRequest {
        public Long contractId;
        public String companyName;
        public String companyAddress;
        public String representative;
        public Integer insuranceAmount;
        public String insuranceAmountString;
    }

    public static class ServiceContractResponse extends ContractDTO.ContractResponse {
        public String companyName;
        public String companyAddress;
        public String representative;
        public Integer insuranceAmount;
        public String insuranceAmountString;
    }
}
