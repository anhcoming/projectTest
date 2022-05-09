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
@Table(name = "TRAINING_SESSION", indexes = {
        @Index(name = "FROM_TRAINING_DATE_IDX", columnList = "FROM_DATE")
})
@Where(clause = "DEL_FLAG = 0")
@SQLDelete(sql = "update TRAINING_SESSION set DEL_FLAG = 1 where TRAINING_SESSION_ID = ?")
public class TrainingSessionEntity extends EntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRAINING_SESSION_GEN")
    @SequenceGenerator(name = "TRAINING_SESSION_GEN", allocationSize = 1, sequenceName = "TRAINING_SESSION_SEQ")
    @Column(name = "TRAINING_SESSION_ID", nullable = false)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "FROM_DATE", nullable = false)
    private LocalDate fromDate;

    @Column(name = "TO_DATE", nullable = false)
    private LocalDate toDate;

    @Column(name = "TRAINING_LOCATION")
    private String trainingLocation;

}
