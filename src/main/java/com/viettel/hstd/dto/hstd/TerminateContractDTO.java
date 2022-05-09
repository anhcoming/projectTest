package com.viettel.hstd.dto.hstd;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.constant.ContractDuration;
import com.viettel.hstd.constant.ContractType;
import com.viettel.hstd.constant.Gender;
import com.viettel.hstd.core.custom.CustomLocalDateTimeDeserializer;
import com.viettel.hstd.core.custom.CustomLocalDateTimeSerializer;
import com.viettel.hstd.core.serializer.CustomLocalDateDeserializer;
import com.viettel.hstd.core.serializer.CustomLocalDateSerializer;
import org.apache.tomcat.jni.Local;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TerminateContractDTO {
    public static class TerminateContractRequest {
        public Long terminateContractId;
        //Ma Id hop dong
        public Long contractId;
        //Ma Id nhan vien
        public Integer employeeId;
        //Ma nhan vien
        public String employeeCode;
        //Ten nhan vien
        public String employeeName;
        //Ngay hop dong co hieu luc
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate effectiveDate;
        //Ngay het han hop dong
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate expiredDate;
        /*
        Ly do
         */
        public String reason;
        /*
        Thoi gian de nghi
         */
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate submitDate;
        /**
         * Thoi gian cham dut hd
         */
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate approvalDate;
        //Ngay sinh
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate birthDate;
        //Noi sinh
        public String placeOfBirth;
        //Gioi tinh
        public Gender gender = Gender.MALE;
        //Trinh do dao tao
        public String trainingLevel;
        //Trinh do chuyen mon
        public String trainingSpeciality;
        //So dinh danh ca nhan/cmnd
        public String personalIdNumber;
        //Ngay cap
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate personalIdIssuedDate;
        //Noi cap
        public String personalIdIssuedPlace;
        //Dia chi thuong tru
        public String permanentAddress;
        //So dien thoai
        public String mobileNumber;
        //Ma vi tri
        public Integer positionId;
        //Ma vi tri
        public String positionCode;
        //Ten vi tri
        public String positionName;
        //Dia chi hien nay
        public String currentAddress;
        public String transCode;
        //Duong dan file thoa thuan cham dut hop dong
        public String agrFileName;
        public String signedAgrFileName;
        //Ten file thoa thuan cham dut hop dong
//        public String terminationContractName;
        //Trang thai gui trinh ky voffice lan 3
        public Integer status;
        //        //Trang thai de xuat cham dut hop dong
//        public Integer proTerminateStatus;
//        //Trang thai thoa thuan cham dut hop dong voi nhan vien
//        public Integer agrTerminateStatus;
        public List<FileAttachmentDTO.FileAttachmentRequest> lstAttachment;
        public String filePath;
        public String signedFile;
        public String fileName;
        public String signedTerminationContract;

        public ContractType contractType;
        public Long organizationId;
        public String organizationName;
        public ContractDuration contractDuration;
        public String receiverHandover;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate joinCompanyDate;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate expectedDate;

        public Long realUnitId;
        public String realUnitName;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate signedDate;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate debtDate;
        public Long receiverHandoverId;
        public Long accountId;
        public String contractNumber;
        /**
         * Xac dinh loai la don vi hay tong cong ty, type = 1: tct khac 1 la don vi
         */
        public Integer type;


        public Boolean typeObject;
        public Integer debtType;
        public String debtFile;


        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate startDate;
        //Thoi gian ket thuc lam viec
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate endDate;

        //Thoi gian bat dau huong tro cap that nghiep
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate sevAllStartDate;
        //Thoi gian ket thuc huong tro cap that nghiep
        //Severance Allowance
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate sevAllEndDate;
        //Lam tron bang nam
        public Integer sevAllTotalYear;
        //Muc luong trung binh 6 thang
        public Float salary;
        //Tro cap thoi viec
//        public Float sevAllowance;
        //Tien tro cap thoi viec bang chu
//        public String sevAllowanceText;
        //Thoi gian huong tro cap that nghiep
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate insuranceTime;
        public Long departmentId;
        public String departmentName;
        public String signaturePath;
        public String signatureName;

        public String managerName;
        public Long managerId;
        public String note;

        public Boolean isTerminateByHr;

        public String transCodeContract;
        public String transCodeSevAllowance;

        public Boolean mergeSevAllowance = false;
        public String sevAllowanceMultiPath;
        public String signedSevAllowanceMultiPath;
    }

    public static class TerminateContractResponse {
        public Long terminateContractId;
        //Ma Id hop dong
        public Long contractId;
        //Ma Id nhan vien
        public Integer employeeId;
        //Ma nhan vien
        public String employeeCode;
        //Ten nhan vien
        public String employeeName;
        //Ngay hop dong co hieu luc
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate effectiveDate;
        //Ngay het han hop dong
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate expiredDate;
        /*
        Ly do
         */
        public String reason;
        /*
        Thoi gian de nghi
         */
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate submitDate;
        /**
         * Thoi gian cham dut hd
         */
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate approvalDate;
        //Ngay sinh
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate birthDate;
        //Noi sinh
        public String placeOfBirth;
        //Gioi tinh
        public Gender gender = Gender.MALE;
        //Trinh do dao tao
        public String trainingLevel;
        //Trinh do chuyen mon
        public String trainingSpeciality;
        //So dinh danh ca nhan/cmnd
        public String personalIdNumber;
        //Ngay cap
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate personalIdIssuedDate;
        //Noi cap
        public String personalIdIssuedPlace;
        //Dia chi thuong tru
        public String permanentAddress;
        //So dien thoai
        public String mobileNumber;
        //Ma vi tri
        public Integer positionId;
        //Ma vi tri
        public String positionCode;
        //Ten vi tri
        public String positionName;
        //Dia chi hien nay
        public String currentAddress;

        public String transCode;

        //Duong dan file thoa thuan cham dut hop dong
//        public String terminationContractPath;
        //Ten file thoa thuan cham dut hop dong
        public String agrFileName;
        public String signedAgrFileName;
        //Trang thai gui trinh ky voffice lan 3
        public Integer status;
        //Trang thai de xuat cham dut hop dong
//        public Integer proTerminateStatus;
        //Trang thai thoa thuan cham dut hop dong voi nhan vien
//        public Integer agrTerminateStatus;
        public List<FileAttachmentDTO.FileAttachmentResponse> lstAttachment;

        public String filePath;
        public String signedFile;
        public String fileName;
        public String signedTerminationContract;
        public ContractType contractType;

        public Long organizationId;
        public String organizationName;
        public ContractDuration contractDuration;
        public String receiverHandover;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate joinCompanyDate;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate expectedDate;

        public Long realUnitId;
        public String realUnitName;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate signedDate;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate debtDate;
        public String contractFile;
        public Long receiverHandoverId;
        public Long accountId;
        public String contractNumber;

        public Boolean typeObject;
        public String debtFile;
        public String signedDebtFile;


        //<editor-fold desc="Cap nhat quyet dinh nghi viec">
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate startDate;
        //Thoi gian ket thuc lam viec
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate endDate;

        //Thoi gian bat dau huong tro cap that nghiep
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate sevAllStartDate;
        //Thoi gian ket thuc huong tro cap that nghiep
        //Severance Allowance
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate sevAllEndDate;
        //Lam tron bang nam
        public Integer sevAllTotalYear;
        //Muc luong trung binh 6 thang
        public Float salary;
        //Tro cap thoi viec
        public Float sevAllowance;
        //Tien tro cap thoi viec bang chu
        public String sevAllowanceText;
        //Thoi gian huong tro cap that nghiep
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        public LocalDate insuranceTime;
        public String sevAllowancePath;
        public String signedSevAllowancePath;
        //</editor-fold>

        public Long unitId;
        public String unitName;
        public Long departmentId;
        public String departmentName;
        public String signaturePath;
        public String signatureName;

        public Integer debtType;

        public String managerName;
        public Long managerId;
        public String note;
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
        public LocalDateTime createdAt;
        public Boolean isTerminateByHr;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        private LocalDate agrApprovalDate;
        @JsonSerialize(using = CustomLocalDateSerializer.class)
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        private LocalDate quitDate;
        private String documentCode;

        public String transCodeContract;
        public String transCodeSevAllowance;
        public Boolean mergeSevAllowance;
        public String sevAllowanceMultiPath;
        public String signedSevAllowanceMultiPath;
    }

    public static class UploadFile {
        public MultipartFile[] files;
        public Long id;
        public Integer status;

        public String filePath;
    }
}
