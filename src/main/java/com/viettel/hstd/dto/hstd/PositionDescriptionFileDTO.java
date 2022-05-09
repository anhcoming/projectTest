package com.viettel.hstd.dto.hstd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PositionDescriptionFileDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long id;
        private String fileTitle;
        private String fileUrl;
        private Long positionDescriptionId;
        private Boolean delFlag = false;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String fileTitle;
        private String fileUrl;
        private Long positionDescriptionId;
        private Boolean delFlag;
    }
}
