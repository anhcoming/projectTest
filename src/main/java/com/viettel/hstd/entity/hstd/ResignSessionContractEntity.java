package com.viettel.hstd.entity.hstd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.*;
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

@Entity
@Table(name = "RESIGN_SESSION_CONTRACT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "update RESIGN_SESSION_CONTRACT set DEL_FLAG = 1 where RESIGN_SESSION_CONTRACT_ID = ?")
@Where(clause = "DEL_FLAG = 0")
public class ResignSessionContractEntity extends EntityBase {
    @Id
    @SequenceGenerator(name = "RESIGN_SESSION_CONTRACT_GEN", initialValue = 1, allocationSize = 1, sequenceName = "RESIGN_SESSION_CONTRACT_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RESIGN_SESSION_CONTRACT_GEN")
    @Column(name = "RESIGN_SESSION_CONTRACT_ID")
    private Long resignSessionContractId;
    @Column(name = "REVIEW_DATE")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate reviewDate;
    @Column(name = "INTERVIEW_SCORE")
    private Float interviewScore = 0f;
    @Column(name = "INTERVIEW_COMMENT")
    private String interviewComment;
    @Column(name = "ATTITUDE")
    private Attitude attitude = Attitude.NOT_EVALUATE_YET;
    @Column(name = "PASS_STATUS")
    private ResignPassStatus passStatus = ResignPassStatus.NOT_EVALUATE_YET;
    @Column(name = "CONTRACT_DURATION")
    private ContractDuration contractDuration = ContractDuration.ONE_YEAR;
    @Column(name = "WORKING_PROGRESS_NOTE")
    private String workingProgressNote;
    @Column(name = "INTERVIEW_NOTE")
    private String interviewNote;
    @Column(name = "RESIGN_NOTE")
    private String resignNote;
    @Column(name = "NEW_CONTRACT_START_DATE")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate newContractEffectiveDate;
    @Column(name = "NEW_CONTRACT_END_DATE")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate newContractExpiredDate;

    @Column(name = "RESIGN_TYPE", nullable = false)
    private ResignType resignType = ResignType.LABOR;
    @Column(name = "REPORT_SCORE", nullable = false)
    private Float reportScore = 0f;
    @Column(name = "SPECIALITY_SCORE", nullable = false)
    private Float specialityScore = 0f;
    @Column(name = "ATTITUDE_SCORE", nullable = false)
    private Float attitudeScore = 0f;
    @Column(name = "RESIGN_STATUS", nullable = false)
    private ResignStatus resignStatus = ResignStatus.IN_EVALUATION;

    @ManyToOne
    @JoinColumn(name = "RESIGN_SESSION_ID")
    public ResignSessionEntity resignSessionEntity;

    @ManyToOne
    @JoinColumn(name = "CONTRACT_ID")
    public ContractEntity contractEntity;

    @Column(name = "NEW_CONTRACT_ID")
    public Long newContractId;

}
