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
@Table(name = "POSITION_DESCRIPTION")
@Where(clause = "DEL_FLAG = 0")
@SQLDelete(sql = "update POSITION_DESCRIPTION set DEL_FLAG = 1 where POSITION_DESCRIPTION_ID = ?")
public class PositionDescriptionEntity extends EntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POSITION_DESCRIPTION_GEN")
    @SequenceGenerator(name = "POSITION_DESCRIPTION_GEN", allocationSize = 1, initialValue = 1, sequenceName = "POSITION_DESCRIPTION_SEQ")
    @Column(name = "POSITION_DESCRIPTION_ID", nullable = false)
    private Long id;

    @Column(name = "UNIT_ID")
    private Long unitId;

    @Column(name = "UNIT_CODE")
    private String unitCode;

    @Column(name = "UNIT_NAME")
    private String unitName;

    @Column(name = "DEPARTMENT_ID")
    private Long departmentId;

    @Column(name = "DEPARTMENT_CODE")
    private String departmentCode;

    @Column(name = "DEPARTMENT_NAME")
    private String departmentName;

    @Column(name = "GROUP_ID")
    private Long groupId;

    @Column(name = "GROUP_CODE")
    private String groupCode;

    @Column(name = "GROUP_NAME")
    private String groupName;

    @Column(name = "POSITION_ID")
    private Long positionId;

    @Column(name = "POSITION_CODE")
    private String positionCode;

    @Column(name = "POSITION_NAME")
    private String positionName;

    @Column(name = "POSITION_ENGLISH_NAME")
    private String positionEnglishName;

    @Column(name = "GENDER_NAME")
    private String genderName;

    @Column(name = "ACADEMIC_REQUIREMENT")
    private String academicRequirement;

    @Column(name = "MAJOR_REQUIREMENT")
    private String majorRequirement;

    @Column(name = "EXPERIENCE_REQUIREMENT")
    private Double experienceRequirement;

    @Column(name = "HEALTH_REQUIREMENT")
    private String healthRequirement;

    @Column(name = "ENGLISH_REQUIREMENT")
    private String englishRequirement;

    @Column(name = "POSITION_DESCRIPTION")
    private String positionDescription;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "AREA_CODE")
    private String areaCode;

    @Column(name = "SPECIALIZATION_CODE")
    private String specializationCode;

    @Column(name = "JOB_CODE")
    private String jobCode;

    @Column(name = "HAS_DESCRIPTION")
    private Boolean hasDescription;

}
