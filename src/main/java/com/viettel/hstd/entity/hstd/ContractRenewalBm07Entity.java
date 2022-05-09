package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.constant.KpiGrade;
import com.viettel.hstd.core.entity.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(name = "CONTRACT_RENEWAL_BM07")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContractRenewalBm07Entity extends EntityBase {
    @Id
    @Column(name = "CONTRACT_ID")
    private Long contractId;
    @Column(name = "EMPLOYEE_CODE")
    private String employeeCode;
    @Column(name = "EMPLOYEE_NAME")
    private String employeeName;
    @Column(name = "BIRTH_YEAR")
    private Integer birthYear;
    @Column(name = "GENDER_TEXT")
    private String genderText;
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
    @Column(name = "KPI_GRADE1")
    private String kpiGrade1;
    @Column(name = "KPI_GRADE2")
    private String kpiGrade2;
    @Column(name = "KPI_GRADE3")
    private String kpiGrade3;
    @Column(name = "KPI_GRADE4")
    private String kpiGrade4;
    @Column(name = "KPI_GRADE5")
    private String kpiGrade5;
    @Column(name = "KPI_GRADE6")
    private String kpiGrade6;
    @Column(name = "KPI_GRADE7")
    private String kpiGrade7;
    @Column(name = "KPI_GRADE8")
    private String kpiGrade8;
    @Column(name = "KPI_GRADE9")
    private String kpiGrade9;
    @Column(name = "KPI_GRADE10")
    private String kpiGrade10;
    @Column(name = "KPI_GRADE11")
    private String kpiGrade11;
    @Column(name = "KPI_GRADE12")
    private String kpiGrade12;
    @Column(name = "KPI_SCORE")
    private Short kpiScore;
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

    public KpiGrade getKpiGrade1() {
        if (kpiGrade1 == null) return null;
        return KpiGrade.of(kpiGrade1.equals("D1") ? "D" : kpiGrade1.equals("c") ? "C" : kpiGrade1);
    }

    public KpiGrade getKpiGrade2() {
        if (kpiGrade2 == null) return null;
        return KpiGrade.of(kpiGrade2.equals("D1") ? "D" : kpiGrade2.equals("c") ? "C" : kpiGrade2);
    }
    public KpiGrade getKpiGrade3() {
        if (kpiGrade3 == null) return null;
        return KpiGrade.of(kpiGrade3.equals("D1") ? "D" : kpiGrade3.equals("c") ? "C" : kpiGrade3);
    }
    public KpiGrade getKpiGrade4() {
        if (kpiGrade4 == null) return null;
        return KpiGrade.of(kpiGrade4.equals("D1") ? "D" : kpiGrade4.equals("c") ? "C" : kpiGrade4);
    }
    public KpiGrade getKpiGrade5() {
        if (kpiGrade5 == null) return null;
        return KpiGrade.of(kpiGrade5.equals("D1") ? "D" : kpiGrade5.equals("c") ? "C" : kpiGrade5);
    }
    public KpiGrade getKpiGrade6() {
        if (kpiGrade6 == null) return null;
        return KpiGrade.of(kpiGrade6.equals("D1") ? "D" : kpiGrade6.equals("c") ? "C" : kpiGrade6);
    }
    public KpiGrade getKpiGrade7() {
        if (kpiGrade7 == null) return null;
        return KpiGrade.of(kpiGrade7.equals("D1") ? "D" : kpiGrade7.equals("c") ? "C" : kpiGrade7);
    }
    public KpiGrade getKpiGrade8() {
        if (kpiGrade8 == null) return null;
        return KpiGrade.of(kpiGrade8.equals("D1") ? "D" : kpiGrade8.equals("c") ? "C" : kpiGrade8);
    }
    public KpiGrade getKpiGrade9() {
        if (kpiGrade9 == null) return null;
        return KpiGrade.of(kpiGrade9.equals("D1") ? "D" : kpiGrade9.equals("c") ? "C" : kpiGrade9);
    }
    public KpiGrade getKpiGrade10() {
        if (kpiGrade10 == null) return null;
        return KpiGrade.of(kpiGrade10.equals("D1") ? "D" : kpiGrade10.equals("c") ? "C" : kpiGrade10);
    }
    public KpiGrade getKpiGrade11() {
        if (kpiGrade11 == null) return null;
        return KpiGrade.of(kpiGrade11.equals("D1") ? "D" : kpiGrade11.equals("c") ? "C" : kpiGrade11);
    }
    public KpiGrade getKpiGrade12() {
        if (kpiGrade12 == null) return null;
        return KpiGrade.of(kpiGrade12.equals("D1") ? "D" : kpiGrade12.equals("c") ? "C" : kpiGrade12);
    }
}
