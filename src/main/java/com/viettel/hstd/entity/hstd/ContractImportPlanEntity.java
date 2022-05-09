package com.viettel.hstd.entity.hstd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.ContractImportCategory;
import com.viettel.hstd.core.entity.EntityBase;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "CONTRACT_IMPORT_PLAN", schema = "HSTD", catalog = "")
@SequenceGenerator(name = "CONTRACT_IMPORT_PLAN_GEN", initialValue = 1, allocationSize = 1, sequenceName = "CONTRACT_IMPORT_PLAN_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class ContractImportPlanEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONTRACT_IMPORT_PLAN_GEN")
    @Column(name = "CONTRACT_IMPORT_PLAN_ID")
    private Long contractImportPlanId;
    @Column(name = "CATEGORY_IMPORT")
    private ContractImportCategory categoryImport;
    @Column(name = "QUARTER")
    private Integer quarter;
    @Column(name = "YEAR")
    private Integer year;
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @Column(name = "START_DATE")
    private LocalDate startDate;
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @Column(name = "END_DATE")
    private LocalDate endDate;
    @Column(name = "FILE_NAME_IMPORT")
    private String fileNameImport;
    @Column(name = "FILE_PATH_IMPORT")
    private String filePathImport;

}
