package com.viettel.hstd.dto.hstd;


public class FreelanceContractDTO {
    public static class FreelanceContractRequest extends ContractDTO.ContractRequest {
        public Long contractId;
        public String taxNumber;
        public String currentAddress;
    }

    public static class FreelanceContractResponse extends ContractDTO.ContractResponse {
        public String taxNumber;
        public String currentAddress;
    }
}
