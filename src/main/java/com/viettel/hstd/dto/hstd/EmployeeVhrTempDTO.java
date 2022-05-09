package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.AttachmentDocumentStatus;
import com.viettel.hstd.constant.ContractType;
import com.viettel.hstd.constant.Gender;
import com.viettel.hstd.constant.NewContractStatus;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class EmployeeVhrTempDTO {
    public static class EmployeeVhrTempRequest {
        public Long employeeVhrTempId;
        public String organizationCode;
        public String employeeId;
        //Ma nhan vien
        public String employeeCode;
        //Ho va ten
        public String fullname;
        //Ngay sinh
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate userBirthday;
        //Gioi tinh
        public Gender gender = Gender.MALE;
        //Dia chi thuong tru
        public String permanentAddress;
        //Cho o hien tai
        public String currentAddress;
        //So dien thoai
        public String mobileNumber;
        //Dia chi email
        public String email;
        public String trainingSpeciality;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        //Ngay vao dang
        public LocalDate partyAdmissionDate;
        //Noi ket nap dang
        public String partyAdmissionPlace;
        //So cmt
        public String personalIdNumber;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        //Ngay cap
        public LocalDate personalIdIssuedDate;
        //Noi cap
        public String personalIdIssuedPlace;
        //Ngay gia nhap cong ty
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate joinCompanyDate;
        //Vi tri lam viec
        public Long positionId;
        public Long organizationId;
        public String positionName;
        public String organizationName;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        //Ngay chinh thuc vao dang
        public LocalDate partyOfficialAdmissionDate;
        //Ngay ky hop dong
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate signedDate;
        //So hop dong
        public String contractDecisionNumber;
        //Chuyen mon nghiep vu
        public String educationSubjectName;
        //Loai hop dong
        public Integer labourContractTypeId;
        //Ton giao
        public Long religionId;
        //Dan toc
        public Long ethnicId;
        //Loai hop dong
        public ContractType contractType;
        //File cv
        public String fileName;
        public String filePath;
        //Ngay gui email thong bao nhan viec
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate resultEmailSendDate;
        //Ngay phong van
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate interviewDate;
        public Long interviewSessionCvId;
        public Long accountId;

        public Integer currentProvinceId;
        public Integer currentDistrictId;
        public Integer currentWardId;
        public Integer originProvinceId;
        public Integer originDistrictId;
        public Integer originWardId;

        public String educationName;

        //<editor-fold desc="Thong tin ong ba noi">
        public String grandFatherName;
        public Integer grandFatherBirth;
        public String grandFatherCareer;
        public String grandMotherName;
        public Integer grandMotherBirth;
        public String grandMotherCareer;
        public String grandParentAddress;
        public String grandParentWorkPlace;
        public String grandParentDescription;
        //</editor-fold>

        //<editor-fold desc="Thong tin cua bo">
        public String fatherName;
        public Integer fatherBirth;
        public String fatherCareer;
        public String fatherAddress;
        public String fatherWorkPlace;
        public String fatherDescription;
        public String fatherFamily;
        //</editor-fold>

        //<editor-fold desc="Thong tin cua me">
        public String motherName;
        public Integer motherBirth;
        public String motherCareer;
        public String motherAddress;
        public String motherWorkPlace;
        public String motherDescription;
        public String motherFamily;
        //</editor-fold>

        //<editor-fold desc="Thong tin ong ba ngoai">
        public String maternalGrandFather;
        public Integer maternalGrandFatherBirth;
        public String maternalGrandFatherCareer;
        public String maternalGrandMother;
        public Integer maternalGrandMotherBirth;
        public String maternalGrandMotherCareer;
        public String maternalGrandAddress;
        public String grandFatherWorkPlace;
        public String grandMotherWorkPlace;
        public String maternalGrandDescription;
        //</editor-fold>
        public String family;
        public String wifeAndChildren;
        public String foreignFamily;
        public String workProcess;

        public String confirmationLocal;

        public Integer status;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate notifySendDate;
        public boolean isLock = false;

        public String taxNumber;
        //Que quan
        public String origin;
        //Noi sinh
        public String birthPlace;
        //So bao hiem
        public String insuranceNumber;
        //Ngay vao doan
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate unionAdmissionDate;
        public String maternalFatherWorkPlace;
        public String maternalMotherWorkPlace;
        public String trainingLevel;
        public List<DocumentTypeDTO.DocumentTypeRequest> listAttachment = new ArrayList<>();
    }

    public static class EmployeeVhrTempResponse {
        public Long employeeVhrTempId;
        public String organizationCode;
        public String employeeId;
        //Ma nhan vien
        public String employeeCode;
        //Ho va ten
        public String fullname;
        //Ngay sinh
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate userBirthday;
        //Gioi tinh
        public Gender gender = Gender.MALE;
        //Dia chi thuong tru
        public String permanentAddress;
        //Cho o hien tai
        public String currentAddress;
        //So dien thoai
        public String mobileNumber;
        //Dia chi email
        public String email;
        public String trainingSpeciality;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        //Ngay vao dang
        public LocalDate partyAdmissionDate;
        //Noi ket nap dang
        public String partyAdmissionPlace;
        //So cmt
        public String personalIdNumber;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        //Ngay cap
        public LocalDate personalIdIssuedDate;
        //Noi cap
        public String personalIdIssuedPlace;
        //Ngay gia nhap cong ty
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate joinCompanyDate;
        //Vi tri lam viec
        public Long positionId;
        public Long organizationId;
        public String positionName;
        public String organizationName;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        //Ngay chinh thuc vao dang
        public LocalDate partyOfficialAdmissionDate;
        //Ngay ky hop dong
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate signedDate;
        //So hop dong
        public String contractDecisionNumber;
        //Chuyen mon nghiep vu
        public String educationSubjectName;
        //Loai hop dong
        public Integer labourContractTypeId;
        //Ton giao
        public Long religionId;
        //Dan toc
        public Long ethnicId;
        //Loai hop dong
        public ContractType contractType;
        //File cv
        public String fileName;
        public String filePath;
        //Ngay gui email thong bao nhan viec
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate resultEmailSendDate;
        //Ngay phong van
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate interviewDate;
        public Long interviewSessionCvId;
        public Long accountId;

        public Integer currentProvinceId;
        public Integer currentDistrictId;
        public Integer currentWardId;
        public Integer originProvinceId;
        public Integer originDistrictId;
        public Integer originWardId;

        public String educationName;

        //<editor-fold desc="Thong tin ong ba noi">
        public String grandFatherName;
        public Integer grandFatherBirth;
        public String grandFatherCareer;
        public String grandMotherName;
        public Integer grandMotherBirth;
        public String grandMotherCareer;
        public String grandParentAddress;
        public String grandFatherWorkPlace;
        public String grandMotherWorkPlace;
        public String grandParentDescription;
        //</editor-fold>

        //<editor-fold desc="Thong tin cua bo">
        public String fatherName;
        public Integer fatherBirth;
        public String fatherCareer;
        public String fatherAddress;
        public String fatherWorkPlace;
        public String fatherDescription;
        public String fatherFamily;
        //</editor-fold>

        //<editor-fold desc="Thong tin cua me">
        public String motherName;
        public Integer motherBirth;
        public String motherCareer;
        public String motherAddress;
        public String motherWorkPlace;
        public String motherDescription;
        public String motherFamily;
        //</editor-fold>

        //<editor-fold desc="Thong tin ong ba ngoai">
        public String maternalGrandFather;
        public Integer maternalGrandFatherBirth;
        public String maternalGrandFatherCareer;
        public String maternalGrandMother;
        public Integer maternalGrandMotherBirth;
        public String maternalGrandMotherCareer;
        public String maternalGrandAddress;
        public String maternalFatherWorkPlace;
        public String maternalMotherWorkPlace;
        public String maternalGrandDescription;
        //</editor-fold>
        public String family;
        public String wifeAndChildren;
        public String foreignFamily;
        public String workProcess;

        public String confirmationLocal;
        public List<DocumentTypeDTO.DocumentTypeResponse> listAttachment = new ArrayList<>();

        public AttachmentDocumentStatus status;
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate notifySendDate;
        public boolean isLock = false;

        public String taxNumber;
        //Que quan
        public String origin;
        //Noi sinh
        public String birthPlace;
        //So bao hiem
        public String insuranceNumber;
        //Ngay vao doan
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate unionAdmissionDate;
        public String trainingLevel;

        public List<ContractDTO.ContractResponse> lstContract;
        public List<TerminateContractDTO.TerminateContractResponse> lstTeminate;

        public Long unitId;
        public String unitName;

        public Long departmentId;
        public String departmentName;

        public NewContractStatus newContractStatus = NewContractStatus.HAVENT_SENT_REQUEST_TO_VHR;
    }

    public static class EmployeeVhrTempLocking {
        public ArrayList<Long> ids;
    }

    public static class SendVhrCodeRequest {
        public Long employeeVhrTempId;
    }

    public static class EmployeeVhrAttachmentResponse {
        public ContractType contractType;
        public List<DocumentTypeDTO.DocumentTypeResponse> documentTypes = new ArrayList<>();
        public AttachmentDocumentStatus status;
    }

}
