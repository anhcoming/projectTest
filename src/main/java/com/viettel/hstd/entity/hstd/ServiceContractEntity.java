package com.viettel.hstd.entity.hstd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "CONTRACT")
//@MappedSuperclass
@Getter
@Setter
@Where(clause = "DEL_FLAG = 0")
public class ServiceContractEntity extends ContractBaseEntity {
    @Column(name = "COMPANY_NAME")
    private String companyName;
    @Column(name = "COMPANY_ADDRESS")
    private String companyAddress;
    @Column(name = "REPRESENTATIVE")
    private String representative;
    @Column(name = "INSURANCE_AMOUNT")
    private Integer insuranceAmount;
    @Column(name = "INSURANCE_AMOUNT_STRING")
    private String insuranceAmountString;

    @Column(name = "TAX_NUMBER")
    private String taxNumber;
}