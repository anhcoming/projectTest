package com.viettel.hstd.entity.hstd;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.viettel.hstd.constant.AttachmentDocumentStatus;
import com.viettel.hstd.constant.ContractType;
import com.viettel.hstd.constant.Gender;
import com.viettel.hstd.constant.NewContractStatus;
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
@Table(name = "EMPLOYEE_VHR_TEMP")
@SequenceGenerator(name = "EMPLOYEE_VHR_TEMP_GEN", initialValue = 1, allocationSize = 1, sequenceName = "EMPLOYEE_VHR_TEMP_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class EmployeeVhrTempEntity extends EntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMPLOYEE_VHR_TEMP_GEN")
    @Column(name = "EMPLOYEE_VHR_TEMP_ID")
    private Long employeeVhrTempId;
    @Column(name = "ORGANIZATION_CODE")
    private String organizationCode;
    @Column(name = "EMPLOYEE_ID")
    private Long employeeId;
    //Ma nhan vien
    @Column(name = "EMPLOYEE_CODE")
    private String employeeCode;
    //Ho va ten
    @Column(name = "FULLNAME")
    private String fullname;
    //Ngay sinh
    @Column(name = "USER_BIRTHDAY")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate userBirthday;
    //Gioi tinh
    @Column(name = "GENDER")
    private Gender gender = Gender.MALE;
    //Dia chi thuong tru
    @Column(name = "PERMANENT_ADDRESS")
    private String permanentAddress;
    //Cho o hien tai
    @Column(name = "CURRENT_ADDRESS")
    private String currentAddress;
    //So dien thoai
    @Column(name = "MOBILE_NUMBER")
    private String mobileNumber;
    //Dia chi email
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "TRAINING_SPECIALITY")
    private String trainingSpeciality;
    //Trinh do dao tao
    @Column(name = "TRAINING_LEVEL")
    private String trainingLevel;
    @Column(name = "PARTY_ADMISSION_DATE")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    //Ngay vao dang
    private LocalDate partyAdmissionDate;
    @Column(name = "PARTY_ADMISSION_PLACE")
    //Noi ket nap dang
    private String partyAdmissionPlace;
    @Column(name = "PERSONAL_ID_NUMBER")
    //So cmt
    private String personalIdNumber;
    @Column(name = "PERSONAL_ID_ISSUED_DATE")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    //Ngay cap
    private LocalDate personalIdIssuedDate;
    @Column(name = "PERSONAL_ID_ISSUED_PLACE")
    //Noi cap
    private String personalIdIssuedPlace;
    //Ngay gia nhap cong ty
    @Column(name = "JOIN_COMPANY_DATE")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate joinCompanyDate;
    //Vi tri lam viec
    @Column(name = "POSITION_ID")
    private Long positionId;
    @Column(name = "ORGANIZATION_ID")
    private String organizationId;
    @Column(name = "POSITION_NAME")
    private String positionName;
    @Column(name = "ORGANIZATION_NAME")
    private String organizationName;
    //    @Column(name = "DEPARTMENT_NAME")
//    private String departmentName;
//    @Column(name = "DESCRIPTION")
//    private String description;
    //Ngay ky hop dong
//    @Column(name = "SIGN_CONTRACT_DATE")
//    @JsonDeserialize(using = LocalDateDeserializer.class)
//    @JsonSerialize(using = LocalDateSerializer.class)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
//    private LocalDate signContractDate;
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @Column(name = "PARTY_OFFICIAL_ADMISSION_DATE")
    //Ngay chinh thuc vao dang
    private LocalDate partyOfficialAdmissionDate;
    //Ngay ky hop dong
    @Column(name = "SIGNED_DATE")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate signedDate;
    //So hop dong
    @Column(name = "CONTRACT_DECISION_NUMBER")
    private String contractDecisionNumber;
    //Chuyen mon nghiep vu
    @Column(name = "EDUCATION_SUBJECT_NAME")
    private String educationSubjectName;
    //Loai hop dong
    @Column(name = "LABOUR_CONTRACT_TYPE_ID")
    private Integer labourContractTypeId;
    //Ton giao
    @Column(name = "RELIGION_ID")
    private Long religionId;
    //Dan toc
    @Column(name = "ETHNIC_ID")
    private Long ethnicId;
    //Loai hop dong
    @Column(name = "CONTRACT_TYPE")
    private ContractType contractType;
    //File cv
    @Column(name = "FILE_NAME")
    private String fileName;
    @Column(name = "FILE_PATH")
    private String filePath;
    //Ngay gui email thong bao nhan viec
    @Column(name = "RESULT_EMAIL_SEND_DATE")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate resultEmailSendDate;
    //Ngay phong van
    @Column(name = "INTERVIEW_DATE")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate interviewDate;
    @Column(name = "INTERVIEW_SESSION_CV_ID")
    private Long interviewSessionCvId;
    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "CURRENT_PROVINCE_ID")
    private Integer currentProvinceId;
    @Column(name = "CURRENT_DISTRICT_ID")
    private Integer currentDistrictId;
    @Column(name = "CURRENT_WARD_ID")
    private Integer currentWardId;
    @Column(name = "ORIGIN_PROVINCE_ID")
    private Integer originProvinceId;
    @Column(name = "ORIGIN_DISTRICT_ID")
    private Integer originDistrictId;
    @Column(name = "ORIGIN_WARD_ID")
    private Integer originWardId;

    @Column(name = "EDUCATION_NAME")
    private String educationName;

    //<editor-fold desc="Thong tin ong ba noi">
    @Column(name = "GRAND_FATHER_NAME")
    private String grandFatherName;
    @Column(name = "GRAND_FATHER_BIRTH")
    private Integer grandFatherBirth;
    @Column(name = "GRAND_FATHER_CAREER")
    private String grandFatherCareer;
    @Column(name = "GRAND_MOTHER_NAME")
    private String grandMotherName;
    @Column(name = "GRAND_MOTHER_BIRTH")
    private Integer grandMotherBirth;
    @Column(name = "GRAND_MOTHER_CAREER")
    private String grandMotherCareer;
    @Column(name = "GRAND_PARENT_ADDRESS")
    private String grandParentAddress;
    @Column(name = "GRAND_FATHER_WORK_PLACE")
    private String grandFatherWorkPlace;
    @Column(name = "GRAND_MOTHER_WORK_PLACE")
    private String grandMotherWorkPlace;
    @Column(name = "GRAND_PARENT_DESCRIPTION")
    private String grandParentDescription;
    //</editor-fold>

    //<editor-fold desc="Thong tin cua bo">
    @Column(name = "FATHER_NAME")
    private String fatherName;
    @Column(name = "FATHER_BIRTH")
    private Integer fatherBirth;
    @Column(name = "FATHER_CAREER")
    private String fatherCareer;
    @Column(name = "FATHER_ADDRESS")
    private String fatherAddress;
    @Column(name = "FATHER_WORK_PLACE")
    private String fatherWorkPlace;
    @Column(name = "FATHER_DESCRIPTION")
    private String fatherDescription;
    @Column(name = "FATHER_FAMILY")
    private String fatherFamily;
    //</editor-fold>

    //<editor-fold desc="Thong tin cua me">
    @Column(name = "MOTHER_NAME")
    private String motherName;
    @Column(name = "MOTHER_BIRTH")
    private Integer motherBirth;
    @Column(name = "MOTHER_CAREER")
    private String motherCareer;
    @Column(name = "MOTHER_ADDRESS")
    private String motherAddress;
    @Column(name = "MOTHER_WORK_PLACE")
    private String motherWorkPlace;
    @Column(name = "MOTHER_DESCRIPTION")
    private String motherDescription;
    @Column(name = "MOTHER_FAMILY")
    private String motherFamily;
    //</editor-fold>

    //<editor-fold desc="Thong tin ong ba ngoai">
    @Column(name = "MATERNAL_GRAND_FATHER")
    private String maternalGrandFather;
    @Column(name = "MATERNAL_GRAND_FATHER_BIRTH")
    private Integer maternalGrandFatherBirth;
    @Column(name = "MATERNAL_GRAND_FATHER_CAREER")
    private String maternalGrandFatherCareer;
    @Column(name = "MATERNAL_GRAND_MOTHER")
    private String maternalGrandMother;
    @Column(name = "MATERNAL_GRAND_MOTHER_BIRTH")
    private Integer maternalGrandMotherBirth;
    @Column(name = "MATERNAL_GRAND_MOTHER_CAREER")
    private String maternalGrandMotherCareer;
    @Column(name = "MATERNAL_GRAND_ADDRESS")
    private String maternalGrandAddress;
    @Column(name = "MATERNAL_GRAND_DESCRIPTION")
    private String maternalGrandDescription;
    //</editor-fold>
    @Column(name = "FAMILY")
    private String family;
    @Column(name = "WIFE_AND_CHILDREN")
    private String wifeAndChildren;
    @Column(name = "FOREIGN_FAMILY")
    private String foreignFamily;
    @Column(name = "WORK_PROCESS")
    private String workProcess;

    @Column(name = "CONFIRMATION_LOCAL")
    private String confirmationLocal;

    /**
     * trang thai nhan vien
     */
    @Column(name = "STATUS")
    private AttachmentDocumentStatus status;
    /**
     * Ngay gui email thong bao
     */
    @Column(name = "NOTIFY_SEND_DATE")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate notifySendDate;
    /**
     * trang thai lock ung vien
     */
    @Column(name = "IS_LOCK")
    private Boolean isLock = false;

    @Column(name = "TAX_NUMBER")
    private String taxNumber;
    //Que quan
    @Column(name = "ORIGIN")
    private String origin;
    //Noi sinh
    @Column(name = "BIRTH_PLACE")
    private String birthPlace;
    //So bao hiem
    @Column(name = "INSURANCE_NUMBER")
    private String insuranceNumber;
    //Ngay vao doan
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @Column(name = "UNION_ADMISSION_DATE")
    private LocalDate unionAdmissionDate;

    @Column(name = "MATERNAL_FATHER_WORK_PLACE")
    private String maternalFatherWorkPlace;
    @Column(name = "MATERNAL_MOTHER_WORK_PLACE")
    private String maternalMotherWorkPlace;
    @Column(name = "NEW_CONTRACT_STATUS")
    private NewContractStatus newContractStatus = NewContractStatus.HAVENT_SENT_REQUEST_TO_VHR;



}
