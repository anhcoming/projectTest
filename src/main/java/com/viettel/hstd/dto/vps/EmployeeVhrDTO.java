package com.viettel.hstd.dto.vps;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.viettel.hstd.constant.Gender;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;

import javax.persistence.Column;
import java.time.LocalDate;

public class EmployeeVhrDTO {
    public static class EmployeeVhrRequest {
        public String employeeId;
        public String fullname;
        public String employeeCode;
        public Gender gender = Gender.MALE;
        public String telephoneNumber;
        public String mobileNumber;
        public String email;

    }

    public static class EmployeeVhrResponse {
        public Long employeeId;
        public String fullname;
        public String employeeCode;
        public Gender gender = Gender.MALE;
        public String telephoneNumber;
        public String mobileNumber;
        public String email;
        public LocalDate dateOfBirth = LocalDate.of(1998, 05, 28);
        public Long organizationId;
        public Long positionId;
        public String trainingLevel;
        public String trainingSpeciality;
    }

    public static class EmployeeFullResponse {
        public Long employeeId;
        //Ho va ten
        public String fullname;
        //Ma nhan vien
        public String employeeCode;
        //Gioi tinh
        public Gender gender = Gender.MALE;
        //Dien thoai ban
        public String telephoneNumber;
        //Dien thoai ca nhan
        public String mobileNumber;
        //Email
        public String email;
        //Ngay sinh dang so
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate dateOfBirth;
        public Long positionId;
        //Don vi
        public Long organizationId;
        //Ten don vi
        public String organizationName;
        //Trinh do dao tao
        public String trainingLevel;
        //Trinh do chuyen mon
        public String trainingSpeciality;
        //Ngay tuyen dung
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        public LocalDate joinCompanyDate;
        //Ma don vi
        public String organizationCode;
        //Noi sinh
        public String placeOfBirth;
        //Ho khau thuong tru
        public String permanentAddress;
        //Dia chi hien tai
        public String currentAddress;
        public String language;
        //Trinh do ngon ngu
        public String languageLevel;
        //Ngay ket nap dang
        public String partyAdmissionDate;
        //Noi ket nap dang
        public String partyDdmissionPlace;
        //So dinh danh ca nhan
        public String personalIdNumber;
        //Ngay cap
        public String personalIdIssuedDate;
        //Noi cap
        public String personalIdIssuedPlace;
        //Ngay nhap ngu
        public String enlistedDate;
        public String soldierLevel;
        public String empType;
        //Ma so thue
        public String taxNumber;
        //Ten vi tri
        public String positionName;
        //Ten phong ban
        public String departmentName;
        public String groupName;
        public String description;
        public String positionGrade;
        public String positionFactor;
        //Ngay ky hop dong
        public String signContractDate;
        public String positionDate;
        //Ngay chinh thuc ket nap dang
        public String partyOfficialAdmissionDate;
        //Ma don vi hien tai
        public String currentOrganizationId;
        //Loai nhan vien
        public String emptypeId;
        //Ten loai nhan vien
        public String emptypeName;
        public String saleCode;
        public String collectCallCode;
        public String accountNumber;
        public String bank;
        public String bankBranch;
        public String salaryTableName;
        public String salaryScaleName;
        public String salaryGradeName;
        public String factor;
        public String status;
        public String isLongLeave;
        public String percent;
        public String minimumMoney;
        public String isForeignEmployee;
        //So bao hiem
        public String insuranceFactor;
        public String positionType;
        public String firstName;
        public String middleName;
        public String lastName;
        public String aliasName;
        public String createdTime;
        public String modifiedTime;
        public String effectiveStartFate;
        public String effectiveEndDate;
        //Ngay ky hop dong
        public String signedDate;
        //So hop dong
        public String contractDecisionNumber;
        public String educationSubjectName;
        public String degreeName;
        //Que quan
        public String orgAddress;
        public String contractMonth;
        //Loai hop dong
        public String labourContractTypeId;
        public String cultureLevelName;
        public String isGhrEmp;
        public String attachmentFileId;
        //Ton giao
        public String religionId;
        //Dan toc
        public String ethnicId;
        public String passportIssueDate;
        public String invalidedSoldierLevelId;
        public String passportNumber;
        public String nationId;
        public String soldierNumber;
        public String educationTypeId;
        public String sickSoldierLevelId;
        public String maritalStatus;
        public String isSickSoldier;
        public LocalDate lastDate;

    }
}
