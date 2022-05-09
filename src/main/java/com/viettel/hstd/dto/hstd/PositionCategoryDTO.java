package com.viettel.hstd.dto.hstd;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PositionCategoryDTO {
    @Getter
    @Setter
    public static class Response{
        private Long id;
        private String areaCode;
        private String areaName;
        private String branchCode;
        private String branchName;
        private String jobCode;
        private String jobName;
        private String positionCode;
        private String positionName;
        private String positionEngName;
    }
}
