package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "RECRUITMENT_PROGRESS_EMPLOYEE")
@Where(clause = "DEL_FLAG = 0")
@SQLDelete(sql = "update RECRUITMENT_PROGRESS_EMPLOYEE set DEL_FLAG = 1 where PROGRESS_EMPLOYEE_ID = ?")
public class RecruitmentProgressEmployeeEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECRUITMENT_PROGRESS_EMPLOYEE_GEN")
    @SequenceGenerator(name = "RECRUITMENT_PROGRESS_EMPLOYEE_GEN", allocationSize = 1, initialValue= 1, sequenceName = "RECRUITMENT_PROGRESS_EMPLOYEE_SEQ")
    @Column(name = "PROGRESS_EMPLOYEE_ID", nullable = false)
    private Long id;

    @Column(name = "EMPLOYEE_ID", nullable = false)
    private Long employeeId;

    @Column(name = "EMPLOYEE_CODE")
    private String employeeCode;

    @Column(name = "EMPLOYEE_NAME")
    private String employeeName;

    @Column(name = "EMPLOYEE_EMAIL")
    private String employeeEmail;

    @Column(name = "IS_HR", nullable = false)
    private Boolean isHr;

    @Column(name = "RECRUITMENT_PROGRESS_ID", nullable = false)
    private Long recruitmentProgressId;
}
