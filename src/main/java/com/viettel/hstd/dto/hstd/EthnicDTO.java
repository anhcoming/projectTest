package com.viettel.hstd.dto.hstd;

public class EthnicDTO {
    public static class EthnicRequest {
        public String code;
        public String name;
        public String description;
    }

    public static class EthnicResponse {
        public Long ethnicId;
        public String code;
        public String name;
        public String description;
    }
}
