package com.viettel.hstd.entity.hstd;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "CONTRACT")
@Getter
@Setter
@Where(clause = "DEL_FLAG = 0 AND CONTRACT_TYPE = 5")
public class ProbationaryContractEntity extends ContractBaseEntity {
        @Column(name = "LABOR_NOTE_NUMBER")
        private String laborNoteNumber;
        @Column(name = "LABOR_NOTE_DATE")
        private LocalDate laborNoteDate;
        @Column(name = "LABOR_NOTE_ADDRESS")
        private String laborNoteAddress;
//        @Column(name = "CONTRACT_LENGTH")
//        private Integer contractLength;
        @Column(name = "NEGOTIATE_SALARY")
        private Integer negotiateSalary;
        @Column(name = "PROBATIONARY_CONTRACT_TYPE")
        private int probationaryContractType = 1;
        @Column(name = "PERCENT_SALARY")
        private Float percentSalary = 100f;
}

