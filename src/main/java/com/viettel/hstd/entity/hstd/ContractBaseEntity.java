package com.viettel.hstd.entity.hstd;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.*;
import com.viettel.hstd.core.entity.EntityBase;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "update CONTRACT set DEL_FLAG = 1 where CONTRACT_ID = ?")
@Where(clause = "DEL_FLAG = 0")
public abstract class ContractBaseEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONTRACT_GEN")
    @SequenceGenerator(name = "CONTRACT_GEN", initialValue = 1, allocationSize = 1, sequenceName = "CONTRACT_SEQ")
    @Column(name = "CONTRACT_ID")
    private Long contractId;
    @Column(name = "CONTRACT_TYPE")
    private ContractType contractType = ContractType.UNKNOWN;
    @Column(name = "CONTRACT_NUMBER")
    private String contractNumber;
    @Column(name = "SIGNED_PLACE")
    private String signedPlace;
    @Column(name = "EFFECTIVE_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate effectiveDate;
    @Column(name = "EXPIRED_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate expiredDate;
    @Column(name = "SIGNED_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate signedDate;
    @Column(name = "ACCOUNT_ID")
    private Long accountId;
    @Column(name = "ACCOUNT_CODE")
    private String accountCode;
    @Column(name = "ACCOUNT_NAME")
    private String accountName;
    @Column(name = "EMPLOYEE_ID")
    private Long employeeId;
    @Column(name = "EMPLOYEE_CODE")
    private String employeeCode;
    @Column(name = "EMPLOYEE_NAME")
    private String employeeName;
    @Column(name = "NATIONALITY")
    private String nationality;
    @Column(name = "BIRTH_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate birthDate;
    @Column(name = "PLACE_OF_BIRTH")
    private String placeOfBirth;
    @Column(name = "GENDER")
    private Gender gender = Gender.MALE;
    @Column(name = "TRAINING_LEVEL")
    private String trainingLevel;
    @Column(name = "TRAINING_SPECIALITY")
    private String trainingSpeciality;
    @Column(name = "TRAINING_PLACE")
    private String trainingPlace;
    @Column(name = "PERSONAL_ID_NUMBER")
    private String personalIdNumber;
    @Column(name = "PERSONAL_ID_ISSUED_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate personalIdIssuedDate;
    @Column(name = "PERSONAL_ID_ISSUED_PLACE")
    private String personalIdIssuedPlace;
    @Column(name = "PERMANENT_ADDRESS")
    private String permanentAddress;
    @Column(name = "MOBILE_NUMBER")
    private String mobileNumber;
//    @Column(name = "EMAIL")
//    private String email;
    @Column(name = "POSITION_ID")
    private Long positionId;
    @Column(name = "POSITION_CODE")
    private String positionCode;
    @Column(name = "POSITION_NAME")
    private String positionName;
    @Column(name = "ACCOUNT_NUMBER")
    private String accountNumber;
    @Column(name = "BANK")
    private String bank;
    @Column(name = "CURRENT_ADDRESS")
    private String currentAddress;
    @Column(name = "CONTRACT_FILE")
    private String contractFile;
    @Column(name = "CONTRACT_FILE_ENCODE_PATH")
    private String contractFileEncodePath;
    @Column(name = "IS_CALL_VOFFICE")
    private Boolean isCallVoffice;
    @Column(name = "SIGNED_FILE")
    private String signedFile;
    @Column(name = "SIGNED_FILE_ENCODE_PATH")
    private String signedFileEncodePath;
    @Column(name = "TRANS_CODE")
    private String transCode;
    @Column(name = "CONTRACT_DURATION")
    private ContractDuration contractDuration = ContractDuration.ONE_YEAR;
    /**
     * Hop dong da duoc cham dut hay chua?
     */
    @Column(name = "IS_TERMINATE")
    private Boolean isTerminate = false;
    @Column(name = "UNIT_ID")
    private Long unitId = 9004488l; // KCQ
    @Column(name = "DEPARTMENT_ID")
    private Long departmentId = 9004497l; // Tổ chức lao động

    /**
     * Tình trạng khi tạo mới hợp đồng
     */
    @Column(name = "NEW_CONTRACT_STATUS")
    private NewContractStatus newContractStatus = NewContractStatus.SENT_TO_VOFFICE;
    @Column(name = "IS_CREATED_BY_HSDT_SERVICE")
    private boolean isCreatedByHsdtService = false;

    @Column(name = "EMPLOYEE_SIGNED_FILE")
    private String employeeSignedFile;
    @Column(name = "EMPLOYEE_SIGNED_FILE_ENCODE_PATH")
    private String employeeSignedFileEncodePath;
    @Column(name = "SIGNATURE_ABSOLUTE_PATH")
    private String signatureAbsolutePath;
    @Column(name = "SIGNATURE_FILE_ENCODE_PATH")
    private String signatureFileEncodePath;
    @Column(name = "SIGNATURE_NAME")
    private String signatureName;
    @Column(name = "CONTRACT_CODE")
    private String contractCode;

    @Column(name = "BASIC_SALARY")
    private String basicSalary; // Lương cơ bản
    @Column(name = "PAY_RATE")
    private String payRate; // Hệ số lương
    @Column(name = "PAY_GRADE")
    private String payGrade; // Ngạch lương
    @Column(name = "PAY_RANGE")
    private String payRange; // Bậc lương


    @Column(name = "RESIGN_STATUS")
    private ResignStatus resignStatus = ResignStatus.NOT_IN_RESIGN_SESSION;

    @OneToMany(mappedBy = "contractEntity",
//            cascade = CascadeType.ALL,
        fetch = FetchType.LAZY)
    @JsonBackReference(value = "resignSessionContractEntities")
    private List<ResignSessionContractEntity> resignSessionContractEntities = new ArrayList<>();


}
