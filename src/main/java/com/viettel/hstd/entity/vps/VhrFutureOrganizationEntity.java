package com.viettel.hstd.entity.vps;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

//     // --Cấp TCT
//    //Select * from vhr_future_organization Where "path" like '%9004482%' and "org_level_manage" = 1;
//    //--Cấp TT,CNKT,KCQ
//    //Select * from vhr_future_organization Where "path" like '%9004482%' and "org_level_manage" = 2;
//    //--Cấp phòng ban thuộc TT,CNKT,KCQ
//    //Select * from vhr_future_organization Where "path" like '%9004482%' and "org_level_manage" = 3;

@Entity
@Getter
@Setter
@Table(name = "VHR_FUTURE_ORGANIZATION")
@Where(clause = "\"path\" like '/148841/148842/165317/9004482/%' and \"is_active\" = 1 and (\"status\" = 1 or \"status\" = 2)")
public class VhrFutureOrganizationEntity {
    @Id
    @Column(name = "\"organization_id\"")
    private Long organizationId;
//    @Column(name = "\"has_employee\"")
//    private Boolean hasEmployee;
    @Column(name = "\"org_level\"")
    private Integer orgLevel;
//    @Column(name = "\"org_order\"")
//    private Integer orgOrder;
//    @Column(name = "\"enterprise_type_id\"")
//    private Long enterpriseTypeId;
    @Column(name = "\"code\"")
    private String code;
    @Column(name = "\"name\"")
    private String name;
//    @Column(name = "\"english_name\"")
//    private String englishName;
//    @Column(name = "\"abbreviation\"")
//    private String abbreviation;
//    @Column(name = "\"establish_decide_num\"")
//    private String establishDecideNum;
//    @Column(name = "\"decision_level\"")
//    private String decisionLevel;
//    @Column(name = "\"org_type_id\"")
//    private Integer orgTypeId;
//    @Column(name = "\"effective_start_date\"")
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    private LocalDateTime effectiveStartDate;
//    @Column(name = "\"effective_end_date\"")
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    private LocalDateTime effectiveEndDate;
//    @Column(name = "\"established_date\"")
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    private LocalDateTime establishedDate;
    @Column(name = "\"org_manager_id\"")
    private Integer orgManagerId;
    @Column(name = "\"org_parent_id\"")
    private Long orgParentId;
//    @Column(name = "\"direct_manager\"")
//    private String directManager;
//    @Column(name = "\"address\"")
//    private String address;
//    @Column(name = "\"nation_id\"")
//    private Integer nationId;
//    @Column(name = "\"province_id\"")
//    private Integer provinceId;
//    @Column(name = "\"business_code\"")
//    private String businessCode;
//    @Column(name = "\"tax_code\"")
//    private String taxCode;
//    @Column(name = "\"bank_account\"")
//    private String bankAccount;
//    @Column(name = "\"bank_id\"")
//    private Integer bankId;
//    @Column(name = "\"bank_agency\"")
//    private String bankAgency;
//    @Column(name = "\"phone_number\"")
//    private String phoneNumber;
//    @Column(name = "\"description\"")
//    private String description;
//    @Column(name = "\"created_time\"")
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    private LocalDateTime createdTime;
//    @Column(name = "\"modified_time\"")
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    private LocalDateTime modifiedTime;
//    @Column(name = "\"expiry_decide_num\"")
//    private String expiryDecideNum;
//    @Column(name = "\"expiry_description\"")
//    private String expiryDescription;
    @Column(name = "\"is_active\"")
    private Boolean isActive;
    @Column(name = "\"is_locked\"")
    private Boolean isLocked;
    @Column(name = "\"status\"")
    private Boolean status;
    @Column(name = "\"path\"")
    private String path;
//    @Column(name = "\"created_by\"")
//    private Integer createdBy;
//    @Column(name = "\"modified_by\"")
//    private Integer modifiedBy;
//    @Column(name = "\"position_boundary_number\"")
//    private Integer positionBoundaryNumber;
//    @Column(name = "\"expiry_relation_type_id\"")
//    private Integer expiryRelationTypeId;
//    @Column(name = "\"reward_organization_id\"")
//    private Integer rewardOrganizationId;
//    @Column(name = "\"full_path\"")
////    private String fullPath;
//    @Column(name = "\"org_code_path\"")
//    private String orgCodePath;
    @Column(name = "\"org_level_manage\"")
    private Integer orgLevelManage;
//    @Column(name = "\"generate_org_order\"")
//    private String generateOrgOrder;
//    @Column(name = "\"is_insurance_org\"")
//    private Boolean isInsuranceOrg;
//    @Column(name = "\"start_working_date\"")
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    private LocalDateTime startWorkingDate;
//    @Column(name = "\"end_working_date\"")
//    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
////    private LocalDateTime endWorkingDate;
//    @Column(name = "\"file_manager_org_id\"")
//    private Integer fileManagerOrgId;
//    @Column(name = "\"district_code\"")
//    private String districtCode;
//    @Column(name = "\"ward_code\"")
//    private String wardCode;
    @Column(name = "\"org_name_level1\"")
    private String orgNameLevel1;
    @Column(name = "\"org_name_level2\"")
    private String orgNameLevel2;
    @Column(name = "\"org_name_level3\"")
    private String orgNameLevel3;
//    @Column(name = "\"is_intermediate_unit\"")
//    private Boolean isIntermediateUnit;
}
