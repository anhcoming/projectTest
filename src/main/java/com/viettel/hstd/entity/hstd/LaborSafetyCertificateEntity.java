package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "LABOR_SAFETY_CERTIFICATE",indexes = {
        @Index(name = "ORGANIZATION_IDX", columnList = "UNIT_ID, DEPARTMENT_ID, GROUP_ID"),
        @Index(name = "EXPIRY_DATE_IDX", columnList = "EXPIRY_DATE"),
        @Index(name = "TRAINING_SESSION_IDX", columnList = "TRAINING_SESSION_ID")
})
@Where(clause = "DEL_FLAG = 0")
@SQLDelete(sql = "update LABOR_SAFETY_CERTIFICATE set DEL_FLAG = 1 where CERTIFICATE_ID = ?")
public class LaborSafetyCertificateEntity extends EntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LABOR_SAFETY_CERTIFICATE_GEN")
    @SequenceGenerator(name = "LABOR_SAFETY_CERTIFICATE_GEN", allocationSize = 1, sequenceName = "LABOR_SAFETY_CERTIFICATE_SEQ")
    @Column(name = "CERTIFICATE_ID", nullable = false)
    private Long id;

    @Column(name = "EMPLOYEE_ID", nullable = false)
    private Long employeeId;

    @Column(name = "EMPLOYEE_CODE")
    private String employeeCode;

    @Column(name = "EMPLOYEE_NAME")
    private String employeeName;

    @Column(name = "EMPLOYEE_EMAIL")
    private String employeeEmail;

    @Column(name = "EMPLOYEE_PHONE_NUMBER")
    private String employeePhoneNumber;

    @Column(name = "EMPLOYEE_GENDER")
    private String employeeGender;

    @Column(name = "POSITION_ID", nullable = false)
    private Long positionId;

    @Column(name = "POSITION_CODE")
    private String positionCode;

    @Column(name = "POSITION_NAME")
    private String positionName;

    @Column(name = "EMPLOYEE_TYPE")
    private String employeeType;

    @Column(name = "UNIT_ID", nullable = false)
    private Long unitId;

    @Column(name = "UNIT_CODE")
    private String unitCode;

    @Column(name = "UNIT_NAME")
    private String unitName;

    @Column(name = "DEPARTMENT_ID", nullable = false)
    private Long departmentId;

    @Column(name = "DEPARTMENT_CODE")
    private String departmentCode;

    @Column(name = "DEPARTMENT_NAME")
    private String departmentName;

    @Column(name = "GROUP_ID", nullable = false)
    private Long groupId;

    @Column(name = "GROUP_CODE")
    private String groupCode;

    @Column(name = "GROUP_NAME")
    private String groupName;

    @Column(name = "ISSUE_DATE", nullable = false)
    private LocalDate issueDate;

    @Column(name = "EXPIRY_DATE", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "TRAINING_SESSION_ID", nullable = false)
    private Long trainingSessionId;

}
