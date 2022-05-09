package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@UtilityClass
public class TrainingSessionDTO {

    @Getter
    @Setter
    public static class Request {
        private Long id;
        @NotNull
        @NotBlank
        @Size(max = 255)
        private String name;

        @NotNull
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        private LocalDate fromDate;

        @NotNull
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        private LocalDate toDate;

        @Size(max = 255)
        private String trainingLocation;
    }

    @Getter
    @Setter
    public static class SearchCriteria {
        private String name;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        private LocalDate fromStartDate;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        private LocalDate toStartDate;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        private LocalDate fromFinishDate;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        private LocalDate toFinishDate;
        private int page = 0;
        private int size = 10;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private String name;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        private LocalDate fromDate;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        private LocalDate toDate;
        private String trainingLocation;
    }
}
