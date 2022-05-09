package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.constant.Attitude;
import com.viettel.hstd.constant.ContractDuration;
import com.viettel.hstd.constant.ResignPassStatus;
import com.viettel.hstd.constant.ResignStatus;
import com.viettel.hstd.core.entity.EntityBase;
import com.viettel.hstd.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(name = "CONTRACT_RENEWAL_BM09")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractRenewalBm09Entity extends EntityBase {
    @Id
    @Column(name = "CONTRACT_ID")
    private int contractId;
    @Column(name = "EMPLOYEE_CODE")
    private String employeeCode;
    @Column(name = "EMPLOYEE_NAME")
    private String employeeName;
    @Column(name = "BIRTH_YEAR")
    private Integer birthYear;
    @Column(name = "GENDER")
    private String gender;
    @Column(name = "POSITION_NAME")
    private String positionName;
    @Column(name = "UNIT_NAME")
    private String unitName;
    @Column(name = "TRAINING_LEVEL")
    private String trainingLevel;
    @Column(name = "TRAINING_SPECIALITY")
    private String trainingSpeciality;
    @Column(name = "CONTRACT_DURATION")
    private String contractDuration;
    @Column(name = "EFFECTIVE_DATE")
    private String effectiveDate;
    @Column(name = "EXPIRED_DATE")
    private String expiredDate;
    @Column(name = "TOTAL_SCORE")
    private Short totalScore;
    @Column(name = "KPI_SCORE")
    private Short kpiScore;
    @Column(name = "INTERVIEW_SCORE")
    private Short interviewScore;
    @Column(name = "ATTITUDE")
    private String attitude;
    @Column(name = "RESIGN_PASS_STATUS")
    private String resignPassStatus;
    @Column(name = "NEW_CONTRACT_DURATION")
    private String newContractDuration;
    @Column(name = "NEW_EFFECTIVE_DATE")
    private String newEffectiveDate;
    @Column(name = "NEW_EXPIRED_DATE")
    private String newExpiredDate;
    @Column(name = "NOTE")
    private String note;

    public LocalDate getEffectiveDate() {
        if (effectiveDate == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(effectiveDate, formatter);
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        if (effectiveDate == null) {
            this.effectiveDate = null;
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            this.effectiveDate = formatter.format(effectiveDate);
        }
    }

    public LocalDate getExpiredDate() {
        if (expiredDate == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(expiredDate, formatter);
    }

    public void setExpiredDate(LocalDate expiredDate) {
        if (expiredDate == null) {
            this.expiredDate = null;
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            this.expiredDate = formatter.format(expiredDate);
        }
    }

    public ContractDuration getContractDuration() {
        if (contractDuration.equals("12 tháng")) {
            return ContractDuration.ONE_YEAR;
        } else if (contractDuration.equals("24 tháng")){
            return ContractDuration.TWO_YEAR;
        } else if (contractDuration.equals("KTH")) {
            return ContractDuration.PERMANENCE;
        }

        return null;
    }

    public ContractDuration getNewContractDuration() {
        if (contractDuration.equals("12 tháng")) {
            return ContractDuration.ONE_YEAR;
        } else if (contractDuration.equals("24 tháng")){
            return ContractDuration.TWO_YEAR;
        } else if (contractDuration.equals("KTH")) {
            return ContractDuration.PERMANENCE;
        }

        return null;
    }

    public Attitude getAttitude() {
        if (attitude == null) return Attitude.NOT_EVALUATE_YET;
        if (attitude.equals("Đạt yêu cầu")) {
            return Attitude.PASS;
        } else if (attitude.equals("Không đạt")) {
            return Attitude.FAIL;
        }
        return Attitude.NOT_EVALUATE_YET;
    }

    public ResignPassStatus getResignPassStatus() {
        if (resignPassStatus == null) return ResignPassStatus.NOT_EVALUATE_YET;
        if (resignPassStatus.equals("Đề xuất ký tiếp")) {
            return ResignPassStatus.PASS;
        } else if (resignPassStatus.equals("Không ký tiếp")) {
            return ResignPassStatus.FAIL;
        }
        return ResignPassStatus.NOT_EVALUATE_YET;
    }

    public LocalDate getNewEffectiveDate() {
        if (newEffectiveDate == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(newEffectiveDate, formatter);
    }

    public LocalDate getNewExpiredDate() {
        if (newExpiredDate == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(newExpiredDate, formatter);
    }


}
