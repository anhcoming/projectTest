package com.viettel.hstd.entity.vps;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.Gender;
import com.viettel.hstd.core.custom.CustomLocalDateTimeDeserializer;
import com.viettel.hstd.core.custom.CustomLocalDateTimeSerializer;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
import com.viettel.hstd.core.utils.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "EMPLOYEE_VHR")
@Getter
@Setter
@Entity
@Slf4j
public class EmployeeVhrEntityFull {
    @Id
    @Column(name = "EMPLOYEEID")
    private Long employeeId;
    //Ho va ten
    @Column(name = "FULLNAME")
    private String fullname;
    //Ma nhan vien
    @Column(name = "EMPLOYEECODE")
    private String employeeCode;
    //Gioi tinh
    @Column(name = "GENDER")
    private String gender;
    //Dien thoai ban
    @Column(name = "TELEPHONENUMBER")
    private String telephoneNumber;
    //Dien thoai ca nhan
    @Column(name = "MOBILENUMBER")
    private String mobileNumber;
    //Email
    @Column(name = "EMAIL")
    private String email;
    //Ngay sinh dang so
    @Column(name = "DATEOFBIRTH")
    private String dateOfBirth;
    @Column(name = "POSITIONID")
    private Long positionId;
    //Don vi
    @Column(name = "ORGANIZATIONID")
    private Long organizationId;
    //Ten don vi
    @Column(name = "ORGANIZATIONNAME")
    private String organizationName;
    //Trinh do dao tao
    @Column(name = "TRAININGLEVEL")
    private String trainingLevel;
    //Trinh do chuyen mon
    @Column(name = "TRAININGSPECIALITY")
    private String trainingSpeciality;
    //Ngay tuyen dung
    @Column(name = "JOINCOMPANYDATE")
    private String joinCompanyDate;
    //Ma don vi
    @Column(name = "ORGANIZATIONCODE")
    private String organizationCode;
    //Noi sinh
    @Column(name = "PLACEOFBIRTH")
    private String placeOfBirth;
    //Ho khau thuong tru
    @Column(name = "PERMANENTADDRESS")
    private String permanentAddress;
    //Dia chi hien tai
    @Column(name = "CURRENTADDRESS")
    private String currentAddress;
    @Column(name = "LANGUAGE")
    private String language;
    //Trinh do ngon ngu
    @Column(name = "LANGUAGELEVEL")
    private String languageLevel;
    //Ngay ket nap dang
    @Column(name = "PARTYADMISSIONDATE")
    private String partyAdmissionDate;
    //Noi ket nap dang
    @Column(name = "PARTYADMISSIONPLACE")
    private String partyDdmissionPlace;
    //So dinh danh ca nhan
    @Column(name = "PERSONALIDNUMBER")
    private String personalIdNumber;
    //Ngay cap
    @Column(name = "PERSONALIDISSUEDDATE")
    private String personalIdIssuedDate;
    //Noi cap
    @Column(name = "PERSONALIDISSUEDPLACE")
    private String personalIdIssuedPlace;
    //Ngay nhap ngu
    @Column(name = "ENLISTEDDATE")
    private String enlistedDate;
    @Column(name = "SOLDIERLEVEL")
    private String soldierLevel;
    @Column(name = "EMPTYPE")
    private String empType;
    //Ma so thue
    @Column(name = "TAXNUMBER")
    private String taxNumber;
    //Ten vi tri
    @Column(name = "POSITIONNAME")
    private String positionName;
    //Ten phong ban
    @Column(name = "DEPARTMENTNAME")
    private String departmentName;
    @Column(name = "GROUPNAME")
    private String groupName;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "POSITIONGRADE")
    private String positionGrade;
    @Column(name = "POSITIONFACTOR")
    private String positionFactor;
    //Ngay ky hop dong
    @Column(name = "SIGNCONTRACTDATE")
    private String signContractDate;
    @Column(name = "POSITIONDATE")
    private String positionDate;
    //    Ngay chinh thuc ket nap dang
    @Column(name = "PARTYOFFICIALADMISSIONDATE")
    private String partyOfficialAdmissionDate;
    //Ma don vi hien tai
    @Column(name = "CURRENTORGANIZATIONID")
    private String currentOrganizationId;
    //Loai nhan vien
    @Column(name = "EMPTYPEID")
    private String emptypeId;
    //Ten loai nhan vien
    @Column(name = "EMPTYPENAME")
    private String emptypeName;
    @Column(name = "SALECODE")
    private String saleCode;
    @Column(name = "COLLECTCALLCODE")
    private String collectCallCode;
    @Column(name = "ACCOUNTNUMBER")
    private String accountNumber;
    @Column(name = "BANK")
    private String bank;
    @Column(name = "BANKBRANCH")
    private String bankBranch;
    @Column(name = "SALARYTABLENAME")
    private String salaryTableName;
    @Column(name = "SALARYSCALENAME")
    private String salaryScaleName;
    @Column(name = "SALARYGRADENAME")
    private String salaryGradeName;
    @Column(name = "FACTOR")
    private String factor;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "ISLONGLEAVE")
    private String isLongLeave;
    @Column(name = "PERCENT")
    private String percent;
    @Column(name = "MINIMUMMONEY")
    private String minimumMoney;
    @Column(name = "ISFOREIGNEMPLOYEE")
    private String isForeignEmployee;
    //    So bao hiem
    @Column(name = "INSURANCEFACTOR")
    private String insuranceFactor;
    @Column(name = "POSITIONTYPE")
    private String positionType;
    @Column(name = "FIRSTNAME")
    private String firstName;
    @Column(name = "MIDDLENAME")
    private String middleName;
    @Column(name = "LASTNAME")
    private String lastName;
    @Column(name = "ALIASNAME")
    private String aliasName;
    @Column(name = "CREATEDTIME")
    private String createdTime;
    @Column(name = "MODIFIEDTIME")
    private String modifiedTime;
    @Column(name = "EFFECTIVESTARTDATE")
    private String effectiveStartFate;
    @Column(name = "EFFECTIVEENDDATE")
    private String effectiveEndDate;
    //Ngay ky hop dong
    @Column(name = "SIGNEDDATE")
    private String signedDate;
    //So hop dong
    @Column(name = "CONTRACTDECISIONNUMBER")
    private String contractDecisionNumber;
    @Column(name = "EDUCATIONSUBJECTNAME")
    private String educationSubjectName;
    @Column(name = "DEGREENAME")
    private String degreeName;
    //    //Que quan
    @Column(name = "ORGADDRESS")
    private String orgAddress;
    @Column(name = "CONTRACTMONTH")
    private String contractMonth;
    //Loai hop dong
    @Column(name = "LABOURCONTRACTTYPEID")
    private String labourContractTypeId;
    @Column(name = "CULTURELEVELNAME")
    private String cultureLevelName;
    @Column(name = "ISGHREMP")
    private String isGhrEmp;
    @Column(name = "ATTACHMENTFILEID")
    private String attachmentFileId;
    //Ton giao
    @Column(name = "RELIGIONID")
    private String religionId;
    //Dan toc
    @Column(name = "ETHNICID")
    private String ethnicId;
    @Column(name = "PASSPORTISSUEDATE")
    private String passportIssueDate;
    @Column(name = "INVALIDEDSOLDIERLEVELID")
    private String invalidedSoldierLevelId;
    @Column(name = "PASSPORTNUMBER")
    private String passportNumber;
    @Column(name = "NATIONID")
    private String nationId;
    @Column(name = "SOLDIERNUMBER")
    private String soldierNumber;
    @Column(name = "EDUCATIONTYPEID")
    private String educationTypeId;
    @Column(name = "SICKSOLDIERLEVELID")
    private String sickSoldierLevelId;
    @Column(name = "MARITALSTATUS")
    private String maritalStatus;
    @Column(name = "ISSICKSOLDIER")
    private String isSickSoldier;
    @Column(name = "LASTDATE")
    private LocalDate lastDate;

    public LocalDate getDateOfBirth() {
        LocalDateTime ldt = TimeUtil.convertStringEpochMilliSecondToLocalDateTime(dateOfBirth);
        return ldt == null ? null : ldt.toLocalDate();
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth.toEpochDay() + "";
    }

    public Gender getGender() {
        boolean isMale = gender != null && gender.equals("1");
        return isMale ? Gender.MALE : Gender.FEMALE;
    }

    public void setGender(Gender gender) {
        boolean isMale = gender.equals(Gender.MALE);
        this.gender = isMale ? "1" : "2";
    }

    public LocalDate getJoinCompanyDate() {
        LocalDateTime ldt = TimeUtil.convertStringEpochMilliSecondToLocalDateTime(dateOfBirth);
        return ldt == null ? LocalDate.of(1995, 10, 30) : ldt.toLocalDate();
    }

    public void setJoinCompanyDate(LocalDate joinCompanyDate) {
        this.joinCompanyDate = joinCompanyDate.toEpochDay() + "";
    }
}