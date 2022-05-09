package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "VO_LOG")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "DEL_FLAG = 0")
@SQLDelete(sql = "update INSURANCE_SESSION set DEL_FLAG = 1 where INSURANCE_SESSION_ID = ?")
public class VoLogEntity extends EntityBase {
    @SequenceGenerator(name = "VO_LOG_GEN", initialValue = 1, allocationSize = 1, sequenceName = "VO_LOG_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VO_LOG_GEN")
    @Id
    @Column(name = "VO_LOG_ID")
    private Long voLogId;
    @Column(name = "REQUEST")
    private String content;
    @Column(name = "TYPE")
    private int type;
}
