package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.constant.VOfficeSignType;
import com.viettel.hstd.core.entity.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "VO_SIGN")
@SequenceGenerator(name = "VO_SIGN_GEN", initialValue = 1, allocationSize = 1, sequenceName = "VO_SIGN_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class VoSignEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VO_SIGN_GEN")
    @Column(name = "VO_SIGN_ID")
    private Long voSignId;
    @Column(name = "TYPE_SIGN")
    private VOfficeSignType typeSign;
    @Column(name = "ID_DATA")
    private Long idData;
    @Column(name = "TRANSCODE")
    private String transCode;
}
