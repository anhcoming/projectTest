package com.viettel.hstd.entity.hstd;


import com.viettel.hstd.core.entity.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "DOCUMENT_RETIREMENT")
@SequenceGenerator(name = "DOCUMENT_RETIREMENT_GEN", initialValue = 1, allocationSize = 1, sequenceName = "DOCUMENT_RETIREMENT_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class DocumentRetirementEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCUMENT_RETIREMENT_GEN")
    @Column(name = "DOCUMENT_RETIREMENT_ID")
    private Long documentRetirementId;
    @Column(name = "CODE")
    private String code;
    @Column(name = "NAME")
    private String name;
    @Column(name = "OBLIGATE_FLAG")
    private Boolean obligateFlag;
}
