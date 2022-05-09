package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.constant.ResignStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "CONTRACT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class CollaboratorContractEntity extends ContractBaseEntity {

    @Column(name = "VIETTEL_PAY_NAME")
    private String viettelPayName;
    @Column(name = "VIETTEL_PAY_NUMBER")
    private String viettelPayNumber;
    @Column(name = "VIETTEL_BRANCH_NAME")
    private String viettelBranchName;
    @Column(name = "VIETTEL_BRANCH_ADDRESS")
    private String viettelBranchAddress;
    @Column(name = "VIETTEL_BRANCH_EMAIL")
    private String viettelBranchEmail;
    @Column(name = "TAX_NUMBER")
    private String taxNumber;
    @Column(name = "CURRENT_ADDRESS")
    private String currentAddress;
    @Column(name = "VIETTEL_BRANCH_PHONE_NUMBER")
    private String viettelBranchPhoneNumber;
}
