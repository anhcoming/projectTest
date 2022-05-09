package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "SYS_CONFIG")
@SequenceGenerator(name = "SYS_CONFIG_GEN", initialValue = 1, allocationSize = 1, sequenceName = "SYS_CONFIG_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class SysConfigEntity extends EntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SYS_CONFIG_GEN")
    @Column(name = "SYS_CONFIG_ID")
    private long sysConfigId;
    @Column(name = "CONFIG_KEY")
    private String configKey;
    @Column(name = "CONFIG_VALUE")
    private String configValue;
    @Column(name = "CONFIG_COMMENT")
    private String configComment;
}
