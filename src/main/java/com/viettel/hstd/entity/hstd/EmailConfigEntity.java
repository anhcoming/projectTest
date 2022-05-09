package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "EMAIL_CONFIG")
@SequenceGenerator(name = "EMAIL_CONFIG_GEN", initialValue = 1, allocationSize = 1, sequenceName = "EMAIL_CONFIG_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class EmailConfigEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMAIL_CONFIG_GEN")
    @Column(name = "EMAIL_CONFIG_ID")
    private Long emailConfigId;

    @Column(name = "MAIL_SERVER")
    private String mailServer;
    @Column(name = "PORT")
    private Integer port;
    @Column(name = "AUTHENTICATE")
    private String authenticate;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "IS_ACTIVE")
    private Boolean isActive;
}
