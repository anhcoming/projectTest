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
@Table(name = "EMAIL_ALERT_CONFIG")
@Where(clause = "DEL_FLAG = 0")
@SQLDelete(sql = "update EMAIL_ALERT_CONFIG set DEL_FLAG = 1 where EMAIL_ALERT_CONFIG_ID = ?")
public class EmailAlertConfigEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMAIL_ALERT_CONFIG_GEN")
    @SequenceGenerator(name = "EMAIL_ALERT_CONFIG_GEN", allocationSize = 1, initialValue= 1, sequenceName = "EMAIL_ALERT_CONFIG_SEQ")
    @Column(name = "EMAIL_ALERT_CONFIG_ID", nullable = false)
    private Long id;

    @Column(name = "POSITION_ID", nullable = false)
    private Long positionId;

    @Column(name = "POSITION_CODE")
    private String positionCode;

    @Column(name = "POSITION_NAME")
    private String positionName;

    @Column(name = "ORGANIZATION_ID", nullable = false)
    private Long organizationId;

    @Column(name = "ORGANIZATION_CODE")
    private String organizationCode;

    @Column(name = "ORGANIZATION_NAME")
    private String organizationName;

    @Column(name = "EMAIL_CONFIG_ID", nullable = false)
    private Long emailConfigId;

    @Column(name = "EMAIL_TEMPLATE_ID", nullable = false)
    private Long emailTemplateId;


}
