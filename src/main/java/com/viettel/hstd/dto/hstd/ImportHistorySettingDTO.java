package com.viettel.hstd.dto.hstd;

import com.viettel.hstd.constant.ImportConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotNull;

@UtilityClass
public class ImportHistorySettingDTO {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private String field;
        private String title;
    }

    @Getter
    @Setter
    public static class SearchCriteria {
        @NotNull
        private ImportConstant.ImportCode importCode;
    }
}
