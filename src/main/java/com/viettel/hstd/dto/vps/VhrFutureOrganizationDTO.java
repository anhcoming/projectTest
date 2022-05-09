package com.viettel.hstd.dto.vps;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.core.custom.CustomLocalDateTimeDeserializer;
import com.viettel.hstd.core.custom.CustomLocalDateTimeSerializer;

import javax.persistence.Column;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class VhrFutureOrganizationDTO {
    public static class VhrFutureOrganizationRequest {
        //    public Boolean hasEmployee;
        public Integer orgLevel;
        //    public Integer orgOrder;
//    public Long enterpriseTypeId;
        public String code;
        public String name;
        //    public String englishName;
//    public String abbreviation;
//    public String establishDecideNum;
//    public String decisionLevel;
//    public Integer orgTypeId;
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    public LocalDateTime effectiveStartDate;
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    public LocalDateTime effectiveEndDate;
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    public LocalDateTime establishedDate;
        public Integer orgManagerId;
        public Long orgParentId;
        //    public String directManager;
//    public String address;
//    public Integer nationId;
//    public Integer provinceId;
//    public String businessCode;
//    public String taxCode;
//    public String bankAccount;
//    public Integer bankId;
//    public String bankAgency;
//    public String phoneNumber;
//    public String description;
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    public LocalDateTime createdTime;
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    public LocalDateTime modifiedTime;
//    public String expiryDecideNum;
//    public String expiryDescription;
        public Boolean isActive;
        public Boolean isLocked;
        public Boolean status;
        public String path;
        //    public Integer createdBy;
//    public Integer modifiedBy;
//    public Integer positionBoundaryNumber;
//    public Integer expiryRelationTypeId;
//    public Integer rewardOrganizationId;
////    public String fullPath;
//    public String orgCodePath;
        public Integer orgLevelManage;
        //    public String generateOrgOrder;
//    public Boolean isInsuranceOrg;
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    public LocalDateTime startWorkingDate;
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
////    public LocalDateTime endWorkingDate;
//    public Integer fileManagerOrgId;
//    public String districtCode;
//    public String wardCode;
        public String orgNameLevel1;
        public String orgNameLevel2;
        public String orgNameLevel3;
//    public Boolean isIntermediateUnit;
    }

    public static class VhrFutureOrganizationResponse {
        public Long organizationId;
        //    public Boolean hasEmployee;
        public Integer orgLevel;
        //    public Integer orgOrder;
//    public Long enterpriseTypeId;
        public String code;
        public String name;
        //    public String englishName;
//    public String abbreviation;
//    public String establishDecideNum;
//    public String decisionLevel;
//    public Integer orgTypeId;
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    public LocalDateTime effectiveStartDate;
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    public LocalDateTime effectiveEndDate;
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    public LocalDateTime establishedDate;
        public Integer orgManagerId;
        public Long orgParentId;
        //    public String directManager;
//    public String address;
//    public Integer nationId;
//    public Integer provinceId;
//    public String businessCode;
//    public String taxCode;
//    public String bankAccount;
//    public Integer bankId;
//    public String bankAgency;
//    public String phoneNumber;
//    public String description;
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    public LocalDateTime createdTime;
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    public LocalDateTime modifiedTime;
//    public String expiryDecideNum;
//    public String expiryDescription;
        public Boolean isActive;
        public Boolean isLocked;
        public Boolean status;
        public String path;
        //    public Integer createdBy;
//    public Integer modifiedBy;
//    public Integer positionBoundaryNumber;
//    public Integer expiryRelationTypeId;
//    public Integer rewardOrganizationId;
////    public String fullPath;
//    public String orgCodePath;
        public Integer orgLevelManage;
        //    public String generateOrgOrder;
//    public Boolean isInsuranceOrg;
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    public LocalDateTime startWorkingDate;
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
////    public LocalDateTime endWorkingDate;
//    public Integer fileManagerOrgId;
//    public String districtCode;
//    public String wardCode;
        public String orgNameLevel1;
        public String orgNameLevel2;
        public String orgNameLevel3;
//    public Boolean isIntermediateUnit;
    }

    public static class DepartmentUnitResponse {
        public Long unitId;
        public String unitName;
        public Long departmentId;
        public String departmentName;
    }
}
