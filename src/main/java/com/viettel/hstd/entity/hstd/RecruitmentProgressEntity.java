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
@Table(name = "RECRUITMENT_PROGRESS")
@Where(clause = "DEL_FLAG = 0")
@SQLDelete(sql = "update RECRUITMENT_PROGRESS set DEL_FLAG = 1 where PROGRESS_ID = ?")
public class RecruitmentProgressEntity extends EntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECRUITMENT_PROGRESS_GEN")
    @SequenceGenerator(name = "RECRUITMENT_PROGRESS_GEN", allocationSize = 1, initialValue= 1, sequenceName = "RECRUITMENT_PROGRESS_SEQ")
    @Column(name = "PROGRESS_ID", nullable = false)
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

    @Column(name = "HR_PLAN", nullable = false)
    private Integer hrPlan;

    @Column(name = "CURRENT_EMP")
    private Integer currentEmp;

    @Column(name = "RECRUITED")
    private Integer recruited;

    @Column(name = "DEADLINE", nullable = false)
    private LocalDate deadline;

    @Column(name = "DESCRIPTION")
    private String description;

    @PrePersist
    public void prePersist(){
        if(recruited==null) recruited = 0;
    }

}
