package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.*;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
import com.viettel.hstd.dto.hstd.ResignSessionDTO.*;
import com.viettel.hstd.dto.hstd.EmployeeMonthlyReviewDTO.*;
import com.viettel.hstd.dto.hstd.ContractDTO.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ResignSessionContractDTO {

    public static class ResignSessionContractRequest {
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate reviewDate;
        @DecimalMin(value = "0", message = "Điểm phỏng vấn không được dưới 0")
        @DecimalMax(value = "60", message = "Điểm phỏng vấn không được trên 60")
        public Float interviewScore = 0f;
        public String interviewComment;
        public Attitude attitude = Attitude.NOT_EVALUATE_YET;
        public ResignPassStatus passStatus = ResignPassStatus.NOT_EVALUATE_YET;
        public ContractDuration contractDuration = ContractDuration.ONE_YEAR;
        public String workingProgressNote;
        public String interviewNote;
        public String resignNote;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate newContractEffectiveDate;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate newContractExpiredDate;

        public ResignType resignType = ResignType.LABOR;
        public Float reportScore = 0f;
        public Float specialityScore = 0f;
        public Float attitudeScore = 0f;
        public Long newContractId;
        public ResignStatus resignStatus = ResignStatus.NOT_IN_RESIGN_SESSION;

        public Long resignSessionId;
        public Long contractId;
    }

    public static class ExportBMRequest {
        public Long resignSessionId;
    }

    public static class ResignSessionContractResponse {
        public Long resignSessionContractId;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate reviewDate;
        public Float interviewScore = 0f;
        public String interviewComment;
        public Attitude attitude = Attitude.NOT_EVALUATE_YET;
        public ResignPassStatus passStatus = ResignPassStatus.NOT_EVALUATE_YET;
        public ContractDuration contractDuration = ContractDuration.ONE_YEAR;
        public String workingProgressNote;
        public String interviewNote;
        public String resignNote;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate newContractEffectiveDate;
        public String newContractEffectiveDate_ddmmyyyy;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate newContractExpiredDate;
        public String newContractExpiredDate_ddmmyyyy;

        public ResignType resignType = ResignType.LABOR;
        public Float reportScore = 0f;
        public Float specialityScore = 0f;
        public Float attitudeScore = 0f;
        public Long newContractId;
        public ResignStatus resignStatus = ResignStatus.NOT_IN_RESIGN_SESSION;

        public ResignSessionResponse resignSessionResponse;
        public ContractResponse contractResponse;
    }

    public static class ResignBm07Response {
        public int index;
        public Long employeeId;
        public String employeeCode;
        public String employeeName;
        public int birthYear;
        public Gender gender = Gender.MALE;
        public Long positionId;
        public String positionName;
        public Long unitId;
        public String unitName;
        public String departmentName;
        public String trainingLevel;
        public String trainingSpeciality;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate contractEffectiveDate;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate contractExpiredDate;
        public List<EmployeeMonthlyReviewResponse> listMonthlyReview = new ArrayList<>();
        public Float kpiScore;
        public String kpiNote;

        public Long resignSessionContractId;
    }

    public static class ResignBm08Response extends ResignSessionContractResponse {
        public int index = 0;
        public Long employeeId;
        public String employeeCode;
        public String employeeName;
        public int birthYear;
        public Gender gender = Gender.MALE;
        public Long positionId;
        public String positionName;
        public Long unitId;
        public String unitName;
        public String trainingLevel;
        public String trainingSpeciality;
        public LocalDate contractEffectiveDate;
        public LocalDate contractExpiredDate;
        public Float interviewScore = 0f;
        public String interviewComment;
        public String interviewNote;
        public ResignStatus resignStatus = ResignStatus.IN_EVALUATION;

        public Long resignSessionContractId;
    }

    @Getter
    @Setter
    public static class ExportBM09Response extends ResignSessionContractResponse {
        public int index = 0;
        public Long employeeId;
        public String employeeCode;
        public String employeeName;
        public int birthYear;
        public Gender gender = Gender.MALE;
        public Long positionId;
        public String positionName;
        public Long unitId;
        public String unitName;
        public String trainingLevel;
        public String trainingSpeciality;
        public LocalDate contractEffectiveDate;
        public String contractEffectiveDate_ddmmyyyy;
        // newContractEffectiveDate_ddmmyyyy not comparable with SpireDoc (maybe too long?)
        public String newContractStartDate_ddmmyyyy;
        public LocalDate contractExpiredDate;
        public String contractExpiredDate_ddmmyyyy;
        public String newContractEndDate_ddmmyyyy;


        public Float kpiScore = 0f;
//        public Float interviewScore = 0f;
//        public String interviewComment;
//        public String interviewNote;
        public Float totalScore = 0f;

//        public Attitude attitude = Attitude.NOT_EVALUATE_YET;
//        public ResignPassStatus passStatus = ResignPassStatus.NOT_EVALUATE_YET;
//        public ContractDuration contractDuration = ContractDuration.ONE_YEAR;
//        public LocalDate newContractEffectiveDate;
//        public LocalDate newContractExpiredDate;
//        public String resignNote;
        public ResignVofficeStatus resignSessionStatus = ResignVofficeStatus.NOT_SEND_YET;
//        public ResignStatus resignStatus = ResignStatus.IN_EVALUATION;

//        public Long resignSessionContractId;
        public Integer stt;

        public int quarter;
        public int year;
        public String genderVietnamese;
        public String passStatusVietnamese;
        public String attitudeVietnamese;
        public String contractDurationVietnamese;
    }

    public static class ExportBM03Response extends ResignSessionContractResponse {
        // Extend from ExportBM09Response
        public int index = 0;
        public Long employeeId;
        public String employeeCode;
        public String employeeName;
        public int birthYear;
        public Gender gender = Gender.MALE;
        public Long positionId;
        public String positionName;
        public Long unitId;
        public String unitName;
        public String trainingLevel;
        public String trainingSpeciality;
        public LocalDate contractEffectiveDate;
        public String contractEffectiveDate_ddmmyyyy;
        public LocalDate contractExpiredDate;
        public String contractExpiredDate_ddmmyyyy;

//        public Float reportScore = 0f;
//        public Float specialityScore = 0f;
//        public Float attitudeScore = 0f;
//        public Float interviewScore = 0f;
        public Float totalScore = 0f;

//        public ResignPassStatus passStatus = ResignPassStatus.NOT_EVALUATE_YET;
//        public ContractDuration contractDuration = ContractDuration.ONE_YEAR;
//        public String resignNote;
        public ResignVofficeStatus resignSessionStatus = ResignVofficeStatus.NOT_SEND_YET;

//        public Long resignSessionContractId;
        public Integer stt;


        // Vietnamese field for export Excel
        public int quarter;
        public int year;
        public String genderVietnamese;
        public String passStatusVietnamese;
        public String attitudeVietnamese;
        public String contractDurationVietnamese;
    }

    public static class ResignContractAddToVofficeLaborRequest {
        public boolean isAll = true;
        public List<Long> resignIdList;
        public int quarter = 1;
        public int year = 2021;
    }

    public static class ResignContractAddToVofficeProbationaryRequest {
        public boolean isAll = true;
        public List<Long> resignIdList;
        public LocalDate startDate = LocalDate.now();
        public LocalDate endDate = LocalDate.now();
    }

    // Kết quả đánh giá đối với CBNV hết hạn hợp đồng lao động
    public static class LaborResignReportResponse {
        public int quarter = 1;
        public int year = 2021;
        // Tổng số CBNV đánh giá hết hạn hợp đồng lao động
        public int totalAboutToExpiredContract = 0;
        // Đề xuất ký tiếp hợp đồng lao động với các trường hợp đạt
        public int passResignContract = 0;
        // Phần trăm Số lượng đánh giá đạt
        public float percentPassResignContract = 0f;
        // Hợp đồng lao động 24 tháng
        public int twoYearContract = 0;
        // Hợp đồng lao động không thời hạn
        public int infiniteContract = 0;
        // Không ký tiếp hợp đồng lao động với các trường hợp đánh giá không đạt và nghỉ việc
        public int failResignContract = 0;
        // Phần trăm Số lượng đánh giá không đạt
        public float percentFailResignContract = 0f;
        // Số lượng nghỉ việc không đánh giá
        public int notEvaluateResignContract = 0;
        // Phần trăm Số lượng nghỉ việc không đánh giá
        public float percentNotEvaluateResignContract = 0f;
    }

    // Kết quả đánh giá CBNV hết hạn thử việc
    public static class ProbationaryResignReportResponse {
        // Trong khi trình ký cho thử việc thì yêu cầu sẽ phải có startDate, endDate (nhưng trong file báo cáo
        // thì lại chỉ có tháng thì a cứ sửa cái tháng đấy thành từ ngày đến ngày cũng được)
        public LocalDate startDate;
        public LocalDate endDate;
        // Số lượng tham gia đánh giá/ CBNV hết hạn thử việc tại Tổng Công ty cần thiết phải tổ chức đánh giá để xem xét ký hợp đồng lao động
        public int totalAboutToExpiredContract = 0;
        // Số lượng đánh giá đạt
        public int passResignContract = 0;
        // Phần trăm Số lượng đánh giá đạt
        public float percentPassResignContract = 0f;
        // Số lượng đánh giá không đạt
        public int failResignContract = 0;
        // Phần trăm Số lượng đánh giá không đạt
        public float percentFailResignContract = 0f;
        // Số lượng nghỉ việc không đánh giá
        public int notEvaluateResignContract = 0;
        // Phần trăm Số lượng nghỉ việc không đánh giá
        public float percentNotEvaluateResignContract = 0f;
    }

}
