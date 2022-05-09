package com.viettel.hstd.dto.hstd;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ImportHistoryDetailDTO {
    @Getter
    @Setter
    public static class Request {
        private Long importHistoryId;
        private String rowContent;
    }

    @Getter
    @Setter
    public static class SearchCriteria {
        private Long importHistoryId;
        private Integer page = 0;
        private Integer size = 10;
    }
}
