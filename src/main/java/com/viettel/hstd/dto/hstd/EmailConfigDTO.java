package com.viettel.hstd.dto.hstd;

public class EmailConfigDTO {
    public static class EmailConfigRequest {
        public String mailServer;
        public Integer port;
        public String authenticate;
        public String email;
        public String password;
        public Boolean isActive;

    }

    public static class EmailConfigResponse {
        public Long emailConfigId;
        public String mailServer;
        public Integer port;
        public String authenticate;
        public String email;
        public String password;
        public Boolean isActive;
    }
}
