package com.viettel.hstd.entity.hstd;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.viettel.hstd.constant.ContractDuration;
import com.viettel.hstd.constant.ContractType;
import com.viettel.hstd.constant.Gender;
import com.viettel.hstd.constant.InsuranceStatus;
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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TERMINATE_CONTRACT")
@SequenceGenerator(name = "TERMINATE_CONTRACT_GEN", initialValue = 1, allocationSize = 1, sequenceName = "TERMINATE_CONTRACT_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class TerminateContractEntity extends EntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TERMINATE_CONTRACT_GEN")
    @Column(name = "TERMINATE_CONTRACT_ID")
    private long terminateContractId;
    //Ma Id hop dong
    @Column(name = "CONTRACT_ID")
    private Long contractId;
    //Ma Id nhan vien
    @Column(name = "EMPLOYEE_ID")
    private Long employeeId;
    //Ma nhan vien
    @Column(name = "EMPLOYEE_CODE")
    private String employeeCode;
    //Ten nhan vien
    @Column(name = "EMPLOYEE_NAME")
    private String employeeName;

    //Ngay hop dong co hieu luc
    @Column(name = "EFFECTIVE_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate effectiveDate;
    //Ngay het han hop dong
    @Column(name = "EXPIRED_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate expiredDate;
    /*
    Ly do
     */
    @Column(name = "REASON")
    private String reason;
    /*
    Thoi gian de nghi
     */
    @Column(name = "SUBMIT_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate submitDate;
    /**
     * Thoi gian cham dut hd
     */
    @Column(name = "APPROVAL_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate approvalDate;
    //Ngay sinh
    @Column(name = "BIRTH_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate birthDate;
    //Noi sinh
    @Column(name = "PLACE_OF_BIRTH")
    private String placeOfBirth;
    //Gioi tinh
    @Column(name = "GENDER")
    private Gender gender = Gender.MALE;
    //Trinh do dao tao
    @Column(name = "TRAINING_LEVEL")
    private String trainingLevel;
    //Trinh do chuyen mon
    @Column(name = "TRAINING_SPECIALITY")
    private String trainingSpeciality;
    //So dinh danh ca nhan/cmnd
    @Column(name = "PERSONAL_ID_NUMBER")
    private String personalIdNumber;
    //Ngay cap
    @Column(name = "PERSONAL_ID_ISSUED_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate personalIdIssuedDate;
    //Noi cap
    @Column(name = "PERSONAL_ID_ISSUED_PLACE")
    private String personalIdIssuedPlace;
    //Dia chi thuong tru
    @Column(name = "PERMANENT_ADDRESS")
    private String permanentAddress;
    //So dien thoai
    @Column(name = "MOBILE_NUMBER")
    private String mobileNumber;
    //Ma vi tri
    @Column(name = "POSITION_ID")
    private Long positionId;
    //Ma vi tri
    @Column(name = "POSITION_CODE")
    private String positionCode;
    //Ten vi tri
    @Column(name = "POSITION_NAME")
    private String positionName;
    //Dia chi hien nay
    @Column(name = "CURRENT_ADDRESS")
    private String currentAddress;

    @Column(name = "TRANS_CODE")
    private String transCode;
    //Trang thai gui trinh ky voffice lan 3
    @Column(name = "STATUS")
    private Integer status;
    //Duong dan file don xin nghi viec
    @Column(name = "FILE_PATH")
    private String filePath;
    //File don xin nghi viec da duoc ky
    @Column(name = "SIGNED_FILE")
    private String signedFile;
    //Ten file don xin nghi viec
    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "AGR_FILE_NAME")
    private String agrFileName;
    @Column(name = "SIGNED_AGR_FILE_NAME")
    private String signedAgrFileName;

    @Column(name = "CONTRACT_TYPE")
    private ContractType contractType;
    @Column(name = "ORGANIZATION_ID")
    private Long organizationId;
    @Column(name = "ORGANIZATION_NAME")
    private String organizationName;

    @Column(name = "CONTRACT_DURATION")
    private ContractDuration contractDuration;
    /**
     * Nguoi nhan ban giao cong viec
     */
    @Column(name = "RECEIVER_HANDOVER")
    private String receiverHandover;
    /**
     * Ngay tuyen dung
     */
    @Column(name = "JOIN_COMPANY_DATE")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate joinCompanyDate;
    /**
     * Ngay du kien nghi viec
     */
    @Column(name = "EXPECTED_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate expectedDate;

    @Column(name = "DEPARTMENT_NAME")
    private String departmentName;
    @Column(name = "DEPARTMENT_ID")
    private Long departmentId;
    /**
     * Ma don vi xin nghi viec
     */
    @Column(name = "REAL_UNIT_ID")
    private Long realUnitId;
    /**
     * Ten don vi xin nghi viec
     */
    @Column(name = "REAL_UNIT_NAME")
    private String realUnitName;

    @Column(name = "SIGNED_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate signedDate;
    /**
     * Ngay hoan tat cong no
     */
    @Column(name = "DEBT_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate debtDate;

    @Column(name = "CONTRACT_FILE")
    private String contractFile;
    /**
     * Ma nguoi nhan ban giao cong viec
     */
    @Column(name = "RECEIVER_HANDOVER_ID")
    private Long receiverHandoverId;
    /**
     * Thong tin nguoi dung neu co
     */
    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "CONTRACT_NUMBER")
    private String contractNumber;
    @Column(name = "DEBT_FILE")
    private String debtFile;
    @Column(name = "DEBT_TYPE")
    private Integer debtType;
    //File da duoc v-office ky
    @Column(name = "SIGNED_DEBT_FILE")
    private String signedDebtFile;
    @Column(name = "TYPE_OBJECT")
    private Boolean typeObject;

    //Thoi gian bat dau lam viec
    @Column(name = "START_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate startDate;
    //Thoi gian ket thuc lam viec
    @Column(name = "END_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate endDate;

    //Thoi gian bat dau huong tro cap that nghiep
    @Column(name = "SEV_ALL_START_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate sevAllStartDate;
    //Thoi gian ket thuc huong tro cap that nghiep
    //Severance Allowance
    @Column(name = "SEV_ALL_END_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate sevAllEndDate;
    //Lam tron bang nam
    @Column(name = "SEV_ALL_TOTAL_YEAR")
    private Integer sevAllTotalYear;
    //Muc luong trung binh 6 thang
    @Column(name = "SALARY")
    private Float salary;
    //Tro cap thoi viec
    @Column(name = "SEVERANCE_ALLOWANCE")
    private Double sevAllowance;
    //Tien tro cap thoi viec bang chu
    @Column(name = "SEVERANCE_ALLOWANCE_TEXT")
    private String sevAllowanceText;
    //Thoi gian huong tro cap that nghiep
    @Column(name = "INSURANCE_TIME")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate insuranceTime;

    @Column(name = "SEV_ALLOWANCE_PATH")
    private String sevAllowancePath;
    @Column(name = "SIGNED_SEV_ALLOWANCE_PATH")
    private String signedSevAllowancePath;
    //Chu ky nhan vien
    @Column(name = "SIGNATURE_PATH")
    private String signaturePath;
    @Column(name = "SIGNATURE_NAME")
    private String signatureName;

    //Nguoi quan ly truc tiep
    @Column(name = "MANAGER_NAME")
    private String managerName;
    //Ma nguoi quan ly truc tiep
    @Column(name = "MANAGER_ID")
    private Long managerId;
    //Y kien cua ng quan ly truc tiep
    @Column(name = "NOTE")
    private String note;
    @Column(name = "IS_TERMINATE_BY_HR")
    private Boolean isTerminateByHr;

    @Column(name = "SEV_ALLOWANCE_TYPE")
    private Integer sevAllowanceType;
    //Ngay nhan ho so, ngay xac nhan thoa thuan cham dut hop dong
    @Column(name = "AGR_APPROVAL_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate agrApprovalDate;
    //Ngay ra quyet dinh nghi viec
    @Column(name = "QUIT_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate quitDate;

    @Column(name = "DOCUMENT_CODE")
    private String documentCode;
    //Ngay quan ly xac nhan don xin nghi viec
    @Column(name = "CONFIRM_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate confirmDate;

    @Column(name = "INSURANCE_STATUS")
    private InsuranceStatus insuranceStatus = InsuranceStatus.NOT_IN_INSURANCE_PROGRESS;

    @OneToMany(mappedBy = "terminateContractEntity",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JsonBackReference(value = "insuranceTerminateEntityList")
    private List<InsuranceTerminateEntity> insuranceTerminateEntityList = new ArrayList<>();

    @Column(name = "TRANS_CODE_CONTRACT")
    private String transCodeContract;

    @Column(name = "TRANS_CODE_SEV_ALLOWANCE")
    private String transCodeSevAllowance;

    @Column(name = "MERGE_SEV_ALLOWANCE")
    private Boolean mergeSevAllowance = false;

    @Column(name = "SEV_ALLOWANCE_MULTI_PATH")
    private String sevAllowanceMultiPath;

    @Column(name = "SIGNED_SEV_ALLOWANCE_MULTI_PATH")
    private String signedSevAllowanceMultiPath;
}
