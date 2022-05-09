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
@Table(name = "EMAIL_ALERT_RECIPIENT")
@Where(clause = "DEL_FLAG = 0")
@SQLDelete(sql = "update EMAIL_ALERT_RECIPIENT set DEL_FLAG = 1 where EMAIL_ALERT_RECIPIENT_ID = ?")
public class EmailAlertRecipientEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMAIL_ALERT_RECIPIENT_GEN")
    @SequenceGenerator(name = "EMAIL_ALERT_RECIPIENT_GEN", allocationSize = 1, initialValue= 1, sequenceName = "EMAIL_ALERT_RECIPIENT_SEQ")
    @Column(name = "EMAIL_ALERT_RECIPIENT_ID", nullable = false)
    private Long id;

    @Column(name = "EMAIL_ALERT_CONFIG_ID", nullable = false)
    private Long emailAlertConfigId;

    @Column(name = "EMPLOYEE_ID", nullable = false)
    private Long employeeId;

    @Column(name = "EMPLOYEE_CODE")
    private String employeeCode;

    @Column(name = "EMPLOYEE_NAME")
    private String employeeName;

    @Column(name = "EMPLOYEE_EMAIL")
    private String employeeEmail;
}
