package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.constant.DecreaseStatus;
import com.viettel.hstd.constant.InsuranceStatus;
import com.viettel.hstd.constant.PrepareDocumentStatus;
import com.viettel.hstd.core.entity.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "INSURANCE_SESSION_TERMINATE_CONTRACT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "update INSURANCE_SESSION_TERMINATE_CONTRACT set DEL_FLAG = 1 where INSURANCE_SESSION_TERMINATE_CONTRACT_ID = ?")
@Where(clause = "DEL_FLAG = 0")
public class InsuranceTerminateEntity extends EntityBase {
    @Id
    @SequenceGenerator(name = "INSURANCE_SESSION_TERMINATE_CONTRACT_GEN", initialValue = 1, allocationSize = 1, sequenceName = "INSURANCE_SESSION_TERMINATE_CONTRACT_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INSURANCE_SESSION_TERMINATE_CONTRACT_GEN")
    @Column(name = "INSURANCE_SESSION_TERMINATE_CONTRACT_ID")
    private Long insuranceSessionTerminateContractId;
    @Column(name = "INSURANCE_STATUS")
    private InsuranceStatus insuranceStatus = InsuranceStatus.JUST_ADD_INTO_INSURANCE_PROGRESS;
    @Column(name = "DECREASE_STATUS")
    private DecreaseStatus decreaseStatus = DecreaseStatus.NOT_IN_DECREASE_PROGRESS;
    @Column(name = "PREPARE_DOCUMENT_STATUS")
    private PrepareDocumentStatus prepareDocumentStatus = PrepareDocumentStatus.NOT_IN_PREPARE_DOCUMENT_PROGRESS;

    @ManyToOne
    @JoinColumn(name = "INSURANCE_SESSION_ID")
    private InsuranceSessionEntity insuranceSessionEntity;

    @ManyToOne
    @JoinColumn(name = "TERMINATE_CONTRACT_ID")
    private TerminateContractEntity terminateContractEntity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InsuranceTerminateEntity that = (InsuranceTerminateEntity) o;
        return insuranceSessionTerminateContractId == that.insuranceSessionTerminateContractId && insuranceStatus == that.insuranceStatus && decreaseStatus == that.decreaseStatus && prepareDocumentStatus == that.prepareDocumentStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(insuranceSessionTerminateContractId, insuranceStatus, decreaseStatus, prepareDocumentStatus);
    }
}
