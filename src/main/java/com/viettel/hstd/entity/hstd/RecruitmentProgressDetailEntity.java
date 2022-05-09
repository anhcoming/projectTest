package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "RECRUITMENT_PROGRESS_DETAIL")
@Getter
@Setter
@Where(clause = "DEL_FLAG = 0")
@SQLDelete(sql = "update RECRUITMENT_PROGRESS_DETAIL set DEL_FLAG = 1 where PROGRESS_DETAIL_ID = ?")
public class RecruitmentProgressDetailEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECRUITMENT_PROGRESS_DETAIL_GEN")
    @SequenceGenerator(name = "RECRUITMENT_PROGRESS_DETAIL_GEN", allocationSize = 1, initialValue = 1, sequenceName = "RECRUITMENT_PROGRESS_DETAIL_SEQ")
    @Column(name = "PROGRESS_DETAIL_ID", nullable = false)
    private Long id;

    @Column(name = "RECRUITED", nullable = false)
    private Integer recruited;

    @Column(name = "RECRUITMENT_DATE", nullable = false)
    private LocalDate recruitmentDate;

    @Column(name = "RECRUITMENT_PROGRESS_ID", nullable = false)
    private Long recruitmentProgressId;

}