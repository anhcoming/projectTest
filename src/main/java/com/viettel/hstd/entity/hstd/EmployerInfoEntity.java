package com.viettel.hstd.entity.hstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.viettel.hstd.core.entity.EntityBase;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "EMPLOYER_INFO")
@SequenceGenerator(name = "EMPLOYER_INFO_GEN", initialValue = 1, allocationSize = 1, sequenceName = "EMPLOYER_INFO_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class EmployerInfoEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMPLOYER_INFO_GEN")
    @Column(name = "EMPLOYER_INFO_ID")
    private Long employerInfoId;

    @Column(name = "ADDRESS")
    private String address;
    @Column(name = "UNIT_NAME")
    private String unitName;
    @Column(name = "UNIT_ID")
    private Long unitId;
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;
    @Column(name = "TAX_CODE")
    private String taxCode;

    @Column(name = "AUTHORIZED_NO")
    private String authorizedNo;
    @Column(name = "AUTHORIZED_DATE")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate authorizedDate;

    @Column(name = "REPRESENTATIVE")
    private String representative;
    @Column(name = "POSITION")
    private String position;
    @Column(name = "REPRESENTATIVE_NATIONALITY")
    private String representativeNationality;
    @Column(name = "REPRESENTATIVE_PERSONAL_ID_NUMBER")
    private String representativePersonalIdNumber;
    @Column(name = "REPRESENTATIVE_PERSONAL_ID_ISSUE_DATE")
    private LocalDate representativePersonalIdIssueDate;
    @Column(name = "REPRESENTATIVE_PERSONAL_ID_ISSUE_PLACE")
    private String representativePersonalIdIssuePlace;
    @Column(name = "REPRESENTATIVE_DATE_OF_BIRTH")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate representativeDateOfBirth;
    @Column(name = "REPRESENTATIVE_PLACE_OF_BIRTH")
    private String representativePlaceOfBirth;
    @Column(name = "REPRESENTATIVE_ADDRESS")
    private String representativeAddress;
}
