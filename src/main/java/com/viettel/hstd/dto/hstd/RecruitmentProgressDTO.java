package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.poiji.annotation.ExcelCell;
import com.poiji.annotation.ExcelRow;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
import com.viettel.hstd.entity.hstd.ImportHistoryEntity;
import lombok.*;
import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@UtilityClass
public class RecruitmentProgressDTO {

    @Getter
    @Setter
    public static class RecruitmentProgressRequest {
        @NotNull
        private Long positionId;

        @NotNull
        private String positionCode;

        private String positionName;

        @NotNull
        private Long organizationId;

        @NotNull
        private String organizationCode;

        private String organizationName;

        @NotNull
        private Integer hrPlan;

        @NotNull
        private Integer currentEmp;

        @NotNull
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        private LocalDate deadline;

        @NotNull
        @NotEmpty
        private List<RecruitmentProgressEmployeeDTO.Request> listEmployee;

        private String description;
    }

    @Getter
    @Setter
    public static class SearchCriteria {
        private Long positionId;
        private Long organizationId;
        private Integer completionRate;
        private Long employeeId;
        private Long employeeEmailRecipientId;
        @JsonFormat(pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
        private Date fromDeadline;
        @JsonFormat(pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
        private Date toDeadline;
        private Integer page = 0;
        private Integer size = 10;
        private List<SearchDTO.OrderDTO> sortList;

    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Response {
        private Long progressId;
        private Long positionId;
        private String positionCode;
        private String positionName;
        private Long organizationId;
        private String organizationCode;
        private String organizationName;
        private Integer hrPlan;
        private Integer currentEmp;
        private Integer recruited;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        private LocalDate deadline;
        private Integer completionRate;
        private String listEmployee;
        private String listEmailRecipients;
        private String description;
    }

    public interface Projection{
        Long getProgressId();
        Long getPositionId();
        String getPositionCode();
        String getPositionName();
        Long getOrganizationId();
        String getOrganizationCode();
        String getOrganizationName();
        Integer getHrPlan();
        Integer getCurrentEmp();
        Integer getRecruited();
        LocalDate getDeadline();
        Integer getCompletionRate();
        String getListEmployee();
        String getListEmailRecipients();
        String getDescription();
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class SingleResponse {
        private Long progressId;
        private Long positionId;
        private String positionCode;
        private String positionName;
        private Long organizationId;
        private String organizationCode;
        private String organizationName;
        private Integer hrPlan;
        private Integer currentEmp;
        private Integer recruited;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        private LocalDate deadline;
        private Integer completionRate;
        private List<RecruitmentProgressEmployeeDTO.Response> listEmployee;
        private List<RecruitmentProgressEmployeeDTO.Response> listEmailRecipients;
        private String description;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @ToString
    public static class EmailResponse{
        private Integer numberOrder;
        private String positionName;
        private String organizationName;
        private int hrPlan;
        private int currentEmp;
        private int recruited;
        private String deadline;
        private String completionRate;
        private String employeeEmail;
        private String employeeName;
        private String description;
        private String listRecipients;
    }

    public interface EmailResponseProjection{
        String getEmployeeEmail();
        String getPositionName();
        String getOrganizationName();
        int getHrPlan();
        int getCurrentEmp();
        int getRecruited();
        String getDeadline();
        String getCompletionRate();
        String getEmployeeName();
        String getDescription();
        String getListRecipients();

    }

    public interface DailyUpdateProjection{
        Long getId();
        Long getPositionId();
        String getPositionCode();
        String getPositionName();
        Long getOrganizationId();
        String getOrganizationCode();
        String getOrganizationName();
        Integer getHrPlan();
        Integer getCurrentEmp();
        Integer getRecruited();
        LocalDate getDeadline();
        String getDescription();
    }

    @Getter
    @Setter
    public static class ImportResultResponse{
        private String positionId;
        private String positionCode;
        private String positionName;
        private String organizationId;
        private String organizationCode;
        private String organizationName;
        private String hrPlan;
        private String currentEmp;
        private String deadline;
        private String listEmployee;
        private String listRecipient;
        private String description;
        private boolean isValid;
        private String error;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecruitmentProgressExcelRow{
        @ExcelCell(0)
        private int rowIndex;
        @ExcelCell(3)
        private String positionCode;
        @ExcelCell(4)
        private String positionName;
        @ExcelCell(1)
        private String organizationCode;
        @ExcelCell(2)
        private String organizationName;
        @ExcelCell(5)
        private String hrPlan;
        @ExcelCell(6)
        private String deadline;
        @ExcelCell(7)
        private List<String> listEmployees;
        @ExcelCell(8)
        private List<String> listRecipients;
        @ExcelCell(9)
        private String description;

        private boolean isValid = true;
        private String errors;
    }

    @Getter
    @Setter
    public static class ImportRequest{
        @NotNull
        private String fileUrl;
        @NotNull
        private String fileTitle;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Event{
        private List<RecruitmentProgressExcelRow> lists;
        private ImportHistoryEntity importHistoryEntity;
    }

}
