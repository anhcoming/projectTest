package com.viettel.hstd.dto.hstd;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RecruitmentProgressDetailFileDTO {
    @Getter
    @Setter
    public static class Request{
        private String fileTitle;
        private String fileUrl;
    }
}
