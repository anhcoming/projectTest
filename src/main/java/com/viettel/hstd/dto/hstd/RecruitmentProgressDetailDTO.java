package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@UtilityClass
public class RecruitmentProgressDetailDTO {

    public interface ResponseProjection {
        public Integer getRecruited();

        public LocalDate getRecruitmentDate();

        public String getFiles();
    }

    public interface DailyUpdateProjection {
        Long getId();

        Long getRecruitmentProgressId();

        Integer getRecruited();

        LocalDate getRecruitmentDate();

    }

    @Getter
    @Setter
    public static class Request {
        @NotNull
        private Integer recruited;

        @NotNull
        private Long recruitmentProgressId;

        @NotNull
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        private LocalDate recruitmentDate;

        @NotNull
        @NotEmpty
        private List<RecruitmentProgressDetailFileDTO.Request> files;

    }

    @Getter
    @Setter
    public static class SearchCriteria {
        @JsonFormat(pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
        private Date recruitmentDate;

        @NotNull
        private Long recruitmentProgressId;

        private Integer page = 0;

        private Integer size = 10;

        private List<SearchDTO.OrderDTO> sortList;

        private boolean requestAll;

    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Response {
        private Integer recruited;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        private LocalDate recruitmentDate;
        private String files;
    }
}
