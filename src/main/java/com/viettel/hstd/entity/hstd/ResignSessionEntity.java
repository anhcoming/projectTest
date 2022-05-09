package com.viettel.hstd.entity.hstd;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.ResignStatus;
import com.viettel.hstd.constant.ResignType;
import com.viettel.hstd.constant.ResignVofficeStatus;
import com.viettel.hstd.core.entity.EntityBase;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "RESIGN_SESSION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "update RESIGN_SESSION set DEL_FLAG = 1 where RESIGN_SESSION_ID = ?")
@Where(clause = "DEL_FLAG = 0")
public class ResignSessionEntity extends EntityBase {
    @Id
    @Column(name = "RESIGN_SESSION_ID")
    @SequenceGenerator(name = "RESIGN_SESSION_GEN", initialValue = 1, allocationSize = 1, sequenceName = "RESIGN_SESSION_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RESIGN_SESSION_GEN")
    private Long resignSessionId;

    @Column(name = "UNIT_ID", nullable = false)
    private Long unitId;
    @Column(name = "SUMMIT_DATE")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate summitDate;
    @Column(name = "APPROVE_DATE")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate approveDate;
    @Column(name = "STATUS")
    private ResignVofficeStatus status = ResignVofficeStatus.NOT_SEND_YET;
    @Column(name = "YEAR")
    private int year = LocalDate.now().getYear();
    @Column(name = "QUARTER")
    private int quarter = LocalDate.now().get(IsoFields.QUARTER_OF_YEAR);
    @Column(name = "NAME")
    private String name = "Tên hợp đồng";
    @Column(name = "TRANSCODE")
    private String transcode;
    @Column(name = "BM09_ENCODE_PATH")
    private String bm09EncodePath;

    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @Column(name = "START_DATE")
    private LocalDate startDate;
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @Column(name = "END_DATE")
    private LocalDate endDate;
    @Column(name = "RESIGN_TYPE", nullable = false)
    private ResignType resignType = ResignType.LABOR;
    @Column(name = "RESIGN_STATUS", nullable = false)
    private ResignStatus resignStatus = ResignStatus.IN_EVALUATION;
    @Column(name = "BMTCT_ENCODE_PATH")
    private String bmtctEncodePath;
    @Column(name = "BMTCT_DOCX_ENCODE_PATH")
    private String bmtctDocxEncodePath;
    @Column(name = "REPORT_TCT_PATH")
    private String reportPath;
    @Column(name = "BM_LIST_ENCODE_PATH")
    private String bmListEncodePath;

    @OneToMany(mappedBy = "resignSessionEntity",
//            cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JsonBackReference(value = "resignSessionContractEntities")
    private List<ResignSessionContractEntity> resignSessionContractEntities = new ArrayList<>();
}
