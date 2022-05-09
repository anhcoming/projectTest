package com.viettel.hstd.dto.vps;

public class DomainDataDTO {
    public static class DomainDataRequest {
        public Long dataId;
        public String dataCode;
        public String dataName;
        public Long domainTypeId;
    }

    public static class DomainDataResponse {
        public Long domainDataId;
        public Long dataId;
        public String dataCode;
        public String dataName;
        public Long domainTypeId;
    }
}
