package com.viettel.hstd.dto.hstd;

public class ReligionDTO {
    public static class ReligionRequest {
        public String code;
        public String name;
        public String description;
    }

    public static class ReligionResponse {
        public Long religionId;
        public String code;
        public String name;
        public String description;
    }
}