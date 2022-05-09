package com.viettel.hstd.dto.hstd;


public class CollaboratorContractDTO {
    public static class CollaboratorContractRequest extends ContractDTO.ContractRequest {
        public Long contractId;
        public String viettelPayName;
        public String viettelPayNumber;
        public String viettelBranchName;
        public String viettelBranchAddress;
        public String viettelBranchEmail;
    }

    public static class CollaboratorContractResponse extends ContractDTO.ContractResponse {
        public String viettelPayName;
        public String viettelPayNumber;
        public String viettelBranchName;
        public String viettelBranchAddress;
        public String viettelBranchEmail;
    }
}
