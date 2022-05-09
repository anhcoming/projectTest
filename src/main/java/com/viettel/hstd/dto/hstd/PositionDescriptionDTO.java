package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poiji.annotation.ExcelCell;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.entity.hstd.ImportHistoryEntity;
import com.viettel.hstd.util.validator.annotation.HasDescriptionNotNullAttachment;
import com.viettel.hstd.util.validator.annotation.PositionDescriptionGender;
import lombok.*;
import lombok.experimental.UtilityClass;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@UtilityClass
public class PositionDescriptionDTO {
    @Getter
    @Setter
    public static class Request {
        private Long unitId;
        private String unitCode;
        private String unitName;
        private Long departmentId;
        private String departmentCode;
        private String departmentName;
        private Long groupId;
        private String groupCode;
        private String groupName;
        private Long positionId;
        private String positionCode;
        private String positionName;
        private String positionEnglishName;
        private String genderName;
        @JsonProperty("academic")
        private String academicRequirement;
        @JsonProperty("major")
        private String majorRequirement;
        @JsonProperty("experience")
        private Double experienceRequirement;
        @JsonProperty("health")
        private String healthRequirement;
        @JsonProperty("english")
        private String englishRequirement;
        private String positionDescription;
        private String note;
        @JsonProperty("areaName")
        private String areaCode;
        @JsonProperty("branchName")
        private String specializationCode;
        @JsonProperty("jobName")
        private String jobCode;
        private Boolean hasDescription;
        private List<PositionDescriptionFileDTO.Request> fileRequests;
        @JsonProperty("listRecipients")
        private List<PositionDescriptionRecipientDTO.Request> recipientRequests;
    }

    @Getter
    @Setter
    @ToString
    public static class ImportRequest {
        @NotNull
        @NotBlank
        private String excelFileUrl;

        @NotNull
        @NotBlank
        private String excelFileTitle;

        private String attachmentFileUrl;

        private String attachmentFileTitle;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @HasDescriptionNotNullAttachment
    public static class PositionDescriptionExcelRow {

        @ExcelCell(0)
        private int rowIndex;

        @ExcelCell(1)
        @NotNull(message = "Mã đơn vị không được để trống")
        @Length(min = 1, max = 255, message = "Mã đơn vị không được vượt quá 255 ký tự")
        private String unitCode;

        @ExcelCell(2)
        @NotNull(message = "Tên đơn vị không được để trống")
        @Length(min = 1, max = 255, message = "Tên đơn vị không được vượt quá 255 ký tự")
        private String unitName;

        @ExcelCell(3)
        @Length(max = 255, message = "Mã phòng ban không được vượt quá 255 ký tự")
        private String departmentCode;

        @ExcelCell(4)
        @Length(max = 255, message = "Tên phòng ban không được vượt quá 255 ký tự")
        private String departmentName;

        @ExcelCell(5)
        @Length(max = 255, message = "Mã tổ nhóm không được vượt quá 255 ký tự")
        private String groupCode;

        @ExcelCell(6)
        @Length(max = 255, message = "Tên tổ nhóm không được vượt quá 255 ký tự")
        private String groupName;

        @ExcelCell(7)
        @NotNull(message = "Tên chức danh không được để trống")
        @Length(min = 1, max = 255, message = "Tên chức danh không được vượt quá 255 ký tự")
        private String positionName;

        @ExcelCell(8)
        @Length(max = 255, message = "Tên chức danh (tiếng anh) không được vượt quá 255 ký tự")
        private String positionEnglishName;

        @ExcelCell(9)
        @Length(max = 255, message = "Giới tính không được vượt quá 255 ký tự")
        @PositionDescriptionGender
        private String genderName;

        @ExcelCell(10)
        @Length(max = 255, message = "Trình độ không được vượt quá 255 ký tự")
        private String academicRequirement;

        @ExcelCell(11)
        @Length(max = 255, message = "Chuyên môn không được vượt quá 255 ký tự")
        private String majorRequirement;

        @ExcelCell(12)
        @Pattern(regexp = "^\\d+$", message = "Kinh nghiệm phải là số dương")
        private String experienceRequirement;

        @ExcelCell(13)
        @Length(max = 255, message = "Sức khỏe không được vượt quá 255 ký tự")
        private String healthRequirement;

        @ExcelCell(14)
        @Length(max = 255, message = "Tiếng anh không được vượt quá 255 ký tự")
        private String englishRequirement;

        @ExcelCell(15)
        @Length(max = 4000, message = "Nhiệm vụ chính không được vượt quá 4000 ký tự")
        private String positionDescription;

        @ExcelCell(16)
        @Length(max = 4000, message = "Ghi chú không được vượt quá 4000 ký tự")
        private String note;

        @ExcelCell(17)
        @Length(max = 255, message = "Khối không được vượt quá 255 ký tự")
        private String areaCode;

        @ExcelCell(18)
        @Length(max = 255, message = "Ngành không được vượt quá 255 ký tự")
        private String specializationCode;

        @ExcelCell(19)
        @Length(max = 255, message = "Nghề không được vượt quá 255 ký tự")
        private String jobCode;

        @ExcelCell(20)
        @NotNull(message = "Mã chức danh không được để trống")
        @Length(min = 1, max = 255, message = "Mã chức danh không được vượt quá 255 ký tự")
        private String positionCode;

        @ExcelCell(21)
        private String hasDescription;

        @ExcelCell(22)
        private List<String> fileAttachments = new ArrayList<>();

        @ExcelCell(23)
        private List<String> employeeRecipients = new ArrayList<>();

        private String errors;

    }

    @Getter
    @Setter
    public static class SearchCriteria{
        private Long unitId;
        private Long departmentId;
        private Long groupId;
        private Long positionId;
        private Boolean hasDescription;
        private Integer page = 0;
        private Integer size = 10;
        private List<SearchDTO.OrderDTO> sortList;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        private Long id;
        private Long unitId;
        private String unitCode;
        private String unitName;
        private Long departmentId;
        private String departmentCode;
        private String departmentName;
        private Long groupId;
        private String groupCode;
        private String groupName;
        private Long positionId;
        private String positionCode;
        private String positionName;
        private String positionEnglishName;
        private String genderName;
        private String academicRequirement;
        private String majorRequirement;
        private Double experienceRequirement;
        private String healthRequirement;
        private String englishRequirement;
        private String positionDescription;
        private String note;
        private String areaCode;
        private String specializationCode;
        private String jobCode;
        private String positionCodeCreated;
        private Boolean hasDescription;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Event{
        private List<PositionDescriptionDTO.PositionDescriptionExcelRow> excelRows;
        private Map<String, String> attachments;
        private ImportHistoryEntity importHistoryEntity;

    }

    @Getter
    @Setter
    public static class SingleResponse{
        private Long id;
        private Long unitId;
        private String unitCode;
        private String unitName;
        private Long departmentId;
        private String departmentCode;
        private String departmentName;
        private Long groupId;
        private String groupCode;
        private String groupName;
        private Long positionId;
        private String positionCode;
        private String positionName;
        private String positionEnglishName;
        private String genderName;
        private String academicRequirement;
        private String majorRequirement;
        private Double experienceRequirement;
        private String healthRequirement;
        private String englishRequirement;
        private String positionDescription;
        private String note;
        private String areaCode;
        private String specializationCode;
        private String jobCode;
        private String positionCodeCreated;
        private Boolean hasDescription;
        private List<PositionDescriptionFileDTO.Response> files;
        private List<PositionDescriptionRecipientDTO.Response> recipients;
    }
}
