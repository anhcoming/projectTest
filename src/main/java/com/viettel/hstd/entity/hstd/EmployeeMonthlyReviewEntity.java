package com.viettel.hstd.entity.hstd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.KpiGrade;
import com.viettel.hstd.core.entity.EntityBase;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "EMPLOYEE_MONTHLY_REVIEW")
@SequenceGenerator(name = "EMPLOYEE_MONTHLY_REVIEW_GEN", initialValue = 1, allocationSize = 1, sequenceName = "EMPLOYEE_MONTHLY_REVIEW_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "update EMPLOYEE_MONTHLY_REVIEW set DEL_FLAG = 1 where EMPLOYEE_MONTHLY_REVIEW_ID = ?")
@Where(clause = "DEL_FLAG = 0")
public class EmployeeMonthlyReviewEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMPLOYEE_MONTHLY_REVIEW_GEN")
    @Column(name = "EMPLOYEE_MONTHLY_REVIEW_ID")
    private Long employeeMonthlyReviewId;
    @Column(name = "EMPLOYEE_ID")
    private Long employeeId;
    @Column(name = "MONTH")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate month = LocalDate.now();
    @Column(name = "GRADE")
    private KpiGrade grade = KpiGrade.UNKNOWN;

}
