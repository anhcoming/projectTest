package com.viettel.hstd.dto.hstd;

import javax.validation.constraints.NotNull;

public class SysConfigDTO {
    public static class SysConfigRequest {
        @NotNull
        public String configKey;
        @NotNull
        public String configValue;
        @NotNull
        public String configComment;
        public Long sysConfigId;
        public  Boolean isActive;
    }

    public static class SysConfigResponse {
        public String configKey;
        public String configValue;
        public String configComment;
        public Long sysConfigId;
        public  Boolean isActive;
    }
}
