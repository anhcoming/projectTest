package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "ETHNIC")
@SequenceGenerator(name = "ETHNIC_GEN", initialValue = 1, allocationSize = 1, sequenceName = "ETHNIC_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class EthnicEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ETHNIC_GEN")
    @Column(name = "ETHNIC_ID")
    private Long ethnicId;
    @Column(name = "CODE")
    private String code;
    @Column(name = "NAME")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
}
