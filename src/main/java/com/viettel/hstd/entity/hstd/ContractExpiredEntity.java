package com.viettel.hstd.entity.hstd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.ContractDuration;
import com.viettel.hstd.core.entity.EntityBase;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "CONTRACT_EXPIRED")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContractExpiredEntity extends EntityBase {
    @Id
    @Column(name = "CONTRACT_ID")
    private Long contractId;
    @Column(name = "EMPLOYEE_CODE")
    private String employeeCode;
    @Column(name = "EMPLOYEE_NAME")
    private String employeeName;
    @Column(name = "BIRTH_YEAR")
    private int birthYear;
    @Column(name = "GENDER_TEXT")
    private String genderText;
    @Column(name = "POSITION_NAME")
    private String positionName;
    @Column(name = "TEAM_NAME")
    private String teamName;
    @Column(name = "DEPARTMENT_NAME")
    private String departmentName;
    @Column(name = "UNIT_NAME")
    private String unitName;
    @Column(name = "TRAINING_LEVEL")
    private String trainingLevel;
    @Column(name = "TRAINING_SPECIALITY")
    private String trainingSpeciality;
    @Column(name = "CONTRACT_DURATION")
    private ContractDuration contractDuration;
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @Column(name = "EFFECTIVE_DATE")
    private LocalDate effectiveDate;
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @Column(name = "EXPIRED_DATE")
    private LocalDate expiredDate;
    @Column(name = "PROGRESS")
    private String progress;
    @Column(name = "MOBILE_NUMBER")
    private String mobileNumber;

}
