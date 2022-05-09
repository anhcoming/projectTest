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
@Table(name = "POSITION_DESCRIPTION_RECIPIENT")
@Where(clause = "DEL_FLAG = 0")
@SQLDelete(sql = "update POSITION_DESCRIPTION_RECIPIENT set DEL_FLAG = 1 where POSITION_DESCRIPTION_RECIPIENT_ID = ?")
public class PositionDescriptionRecipientEntity extends EntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POSITION_DESCRIPTION_RECIPIENT_GEN")
    @SequenceGenerator(name = "POSITION_DESCRIPTION_RECIPIENT_GEN", allocationSize = 1, initialValue = 1, sequenceName = "POSITION_DESCRIPTION_RECIPIENT_SEQ")
    @Column(name = "POSITION_DESCRIPTION_RECIPIENT_ID", nullable = false)
    private Long id;

    @Column(name = "EMPLOYEE_ID", nullable = false)
    private Long employeeId;

    @Column(name = "EMPLOYEE_CODE")
    private String employeeCode;

    @Column(name = "EMPLOYEE_NAME")
    private String employeeName;

    @Column(name = "EMPLOYEE_EMAIL")
    private String employeeEmail;

    @Column(name = "EMPLOYEE_MOBILE_PHONE")
    private String employeeMobilePhone;

    @Column(name = "POSITION_DESCRIPTION_ID", nullable = false)
    private Long positionDescriptionId;

}
