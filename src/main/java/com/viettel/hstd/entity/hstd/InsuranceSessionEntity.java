package com.viettel.hstd.entity.hstd;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.InsuranceStatus;
import com.viettel.hstd.core.custom.CustomLocalDateTimeDeserializer;
import com.viettel.hstd.core.custom.CustomLocalDateTimeSerializer;
import com.viettel.hstd.core.entity.EntityBase;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "INSURANCE_SESSION")
@SequenceGenerator(name = "INSURANCE_SESSION_GEN", initialValue = 1, allocationSize = 1, sequenceName = "INSURANCE_SESSION_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "update INSURANCE_SESSION set DEL_FLAG = 1 where INSURANCE_SESSION_ID = ?")
@Where(clause = "DEL_FLAG = 0")
public class InsuranceSessionEntity extends EntityBase {

    @Id
    @SequenceGenerator(name = "INSURANCE_SESSION_GEN", initialValue = 1, allocationSize = 1, sequenceName = "INSURANCE_SESSION_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INSURANCE_SESSION_GEN")
    @Column(name = "INSURANCE_SESSION_ID")
    private Long insuranceSessionId;
    @Column(name = "SESSION_NAME")
    private String sessionName;
    @Column(name = "DONE_ADDING_CONTRACT_TIMESTAMP")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime doneAddingContractTimestamp;
    @Column(name = "DONE_DECREASING_AND_PREPARE_DOCUMENT_TIMESTAMP")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime doneDecreasingAndPrepareDocumentTimestamp;
    @Column(name = "DONE_SENDING_TO_BHXH_TIMESTAMP")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime doneSendingToBhxhTimestamp;
    @Column(name = "FINISH_INSURANCE_PROCEDURE_TIMESTAMP")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime finishInsuranceProcedureTimestamp;
    @Column(name = "INSURANCE_STATUS")
    private InsuranceStatus insuranceStatus = InsuranceStatus.NOT_IN_INSURANCE_PROGRESS;

    @OneToMany(mappedBy = "insuranceSessionEntity",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @JsonBackReference(value = "insuranceTerminateEntityList")
    private List<InsuranceTerminateEntity> insuranceTerminateEntityList = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InsuranceSessionEntity that = (InsuranceSessionEntity) o;
        return insuranceSessionId == that.insuranceSessionId && insuranceStatus == that.insuranceStatus && Objects.equals(sessionName, that.sessionName) && Objects.equals(doneAddingContractTimestamp, that.doneAddingContractTimestamp) && Objects.equals(doneDecreasingAndPrepareDocumentTimestamp, that.doneDecreasingAndPrepareDocumentTimestamp) && Objects.equals(doneSendingToBhxhTimestamp, that.doneSendingToBhxhTimestamp) && Objects.equals(finishInsuranceProcedureTimestamp, that.finishInsuranceProcedureTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(insuranceSessionId, sessionName, doneAddingContractTimestamp, doneDecreasingAndPrepareDocumentTimestamp, doneSendingToBhxhTimestamp, finishInsuranceProcedureTimestamp, insuranceStatus);
    }
}
