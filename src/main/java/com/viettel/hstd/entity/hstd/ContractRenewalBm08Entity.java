package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "CONTRACT_RENEWAL_BM08")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractRenewalBm08Entity extends EntityBase {
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
    @Column(name = "EFFECTIVE_DATE")
    private String effectiveDate;
    @Column(name = "EXPIRED_DATE")
    private String expiredDate;
    @Column(name = "INTERVIEW_SCORE")
    private Float interviewScore;
    @Column(name = "COUNCIL_REVIEW")
    private String councilReview;
    @Column(name = "NOTE")
    private String note;

    public Float getInterviewScore() {
        return interviewScore != null ? interviewScore : 0;
    }
}
