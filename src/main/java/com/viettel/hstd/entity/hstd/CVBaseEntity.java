package com.viettel.hstd.entity.hstd;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.Gender;
import com.viettel.hstd.constant.ResignStatus;
import com.viettel.hstd.core.entity.EntityBase;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "update CV set DEL_FLAG = 1 where CONTRACT_ID = ?")
@Where(clause = "DEL_FLAG = 0")
public abstract class CVBaseEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CV_GEN")
    @SequenceGenerator(name = "CV_GEN", initialValue = 1, allocationSize = 1, sequenceName = "CV_SEQ")
    @Column(name = "CV_ID")
    private Long cvId;
    @Column(name = "FULL_NAME")
    private String fullName;
    @Column(name = "GENDER")
    private Gender gender = Gender.MALE;
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @Column(name = "USER_BIRTHDAY")
    private LocalDate userBirthday;
    @Column(name = "EMAIL", unique = true)
    private String email;
    @Column(name = "PHONE_NUMBER", unique = true)
    private String phoneNumber;
    @Column(name = "ADDRESS")
    private String address;
    @Column(name = "TECHNICAL_EXPERTISE_PROFESSION")
    private String technicalExpertiseProfession;
    @Column(name = "SCHOOL")
    private String school;
    @Column(name = "MAJOR")
    private String major;

    @Column(name = "APPLY_DATE")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate applyDate;
    @Column(name = "APPLY_POSITION")
    private String applyPosition;
    @Column(name = "POSITION_CODE")
    private String positionCode;
    @Column(name = "POSITION_ID")
    private Long positionId;
    @Column(name = "INTERVIEW_STATE")
    private Boolean interviewState;
    @Column(name = "YEARS_EXPERIENCE")
    private Integer yearsExperience;
    @Column(name = "FILE_NAME")
    private String fileName;
    @Column(name = "FILE_PATH")
    private String filePath;
    @Column(name = "PERSONAL_ID_NUMBER")
    private String personalIdNumber;
    @Column(name = "PERMANENCE_ADDRESS")
    private String permanenceAddress;

    @OneToMany(mappedBy = "cvEntity",
//            cascade = CascadeType.ALL,
        fetch = FetchType.EAGER)
    @JsonBackReference(value = "interviewSessionCvEntities")
    private List<InterviewSessionCvEntity> interviewSessionCvEntities = new ArrayList<>();

    @Column(name = "DEGREE_CERTIFICATE_NAME")
    private String diplomaNo;
    @Column(name = "DEGREE_CERTIFICATE_NUMBER")
    private String registerNo;
    @Column(name = "DEGREE_CERTIFICATE_DATE")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate issuedOn;
    @Column(name = "DEGREE_CERTIFICATE_SIGNED_BY")
    private String rector;
    @Column(name = "UNIT_ID")
    private Long unitId = 9004488l; // KCQ
    @Column(name = "UNIT_NAME")
    private String unitName = "KCQ TCT Công trình"; // KCQ
}
