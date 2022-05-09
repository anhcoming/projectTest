package com.viettel.hstd.entity.hstd;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.viettel.hstd.constant.AcceptJobStatus;
import com.viettel.hstd.constant.ContractType;
import com.viettel.hstd.constant.InterviewResult;
import com.viettel.hstd.core.entity.EntityBase;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "INTERVIEW_SESSION_CV")
@SequenceGenerator(name = "INTERVIEW_SESSION_CV_GEN", initialValue = 1, allocationSize = 1, sequenceName = "INTERVIEW_SESSION_CV_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "update INTERVIEW_SESSION_CV set DEL_FLAG = 1 where INTERVIEW_SESSION_CV_ID = ?")
@Where(clause = "DEL_FLAG = 0")
public class InterviewSessionCvEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INTERVIEW_SESSION_CV_GEN")
    @Column(name = "INTERVIEW_SESSION_CV_ID")
    private Long interviewSessionCvId;
    @Column(name = "EXPERTISE_GRADE")
    private Float expertiseGrade;
    @Column(name = "CULTURAL_APPROPRIATION_GRADE")
    private Float culturalAppropriationGrade;
    @Column(name = "STYLE_GRADE")
    private Float styleGrade;
    @Column(name = "EXPERIENCE_GRADE")
    private Float experienceGrade;
    @Column(name = "CHARACTERISTIC_MULTIPLE_CHOISE")
    private String characteristicMultipleChoice;
    @Column(name = "ENGLISH")
    private String english;
    @Column(name = "TECHNOLOGY_FAMILIARITY_GRADE")
    private Float technologyFamiliarityGrade;
    @Column(name = "REVIEW")
    private String review;
    @Column(name = "RESULT")
    private InterviewResult result = InterviewResult.NOT_EVALUATE_YET;
    @Column(name = "RESULT_EMAIL_SEND_DATE")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate resultEmailSendDate;
    @Column(name = "INTERVIEW_DATE")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate interviewDate;
    @ManyToOne
    @JoinColumn(name = "INTERVIEW_SESSION_ID")
    private InterviewSessionEntity interviewSessionEntity;
    @ManyToOne
    @JoinColumn(name = "CV_ID")
    private CvEntity cvEntity;
    /**
     * Muc luong mong muon
     */
    @Column(name = "SALARY_EXPECTATIONS")
    private Float salaryExpectations;
    /**
     * Luong hien tai
     */
    @Column(name = "CURRENT_SALARY")
    private Float currentSalary;
    /**
     * Ngay du kien nhan viec
     */
    @Column(name = "EXPECTED_DATE")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate expectedDate;
    /**
     * Ngay bat dau lam viec
     */
    @Column(name = "STARTING_DATE")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate startingDate;
    /**
     * Ly do khong nhan viec
     */
    @Column(name = "REASON")
    private String reason;
    @Column(name = "IS_WORK")
    private AcceptJobStatus isWork = AcceptJobStatus.NOT_DECIDED_YET;
    /**
     * Loai hop dong de xuat
     */
    @Column(name = "CONTRACT_TYPE")
    private ContractType contractType;

    @Column(name = "SUM_POINT")
    private Float sumPoint;
    @Column(name = "MAX_POINT")
    private Float maxPoint;

    @OneToMany(mappedBy = "interviewSessionCvEntity",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JsonBackReference(value = "recruiteeAccountEntities")
    private List<RecruiteeAccountEntity> recruiteeAccountEntities;
    /**
     * Da goi trinh ky hay chua?
     */
    @Column(name = "IS_CALL_VOFFICE")
    private Boolean isCallVoffice;
    /**
     * Duong dan file sau khi da trinh ky
     */
    @Column(name = "SIGNED_FILE")
    private String signedFile;
    @Column(name = "TRANS_CODE")
    private String transCode;
    /**
     * Bao cao ket qua tuyen dung
     */
    @Column(name = "INTERVIEW_REPORT_FILE")
    private String interviewReportFile;

    @Column(name ="IS_LOCK")
    private Boolean isLock = false; // Đã trúng tuyển và tạo tài khoản nhân viên sẽ khóa lại


}
