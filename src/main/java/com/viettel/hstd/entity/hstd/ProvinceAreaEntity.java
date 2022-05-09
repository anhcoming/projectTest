package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "PROVINCE_AREA")
@Getter
@Setter
@Where(clause = "DEL_FLAG = 0")
@SequenceGenerator(name = "province_area_gen", initialValue = 1, allocationSize = 1, sequenceName = "PROVINCE_AREA_SEQ")
public class ProvinceAreaEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "province_area_gen")
    @Column(name = "ID")
    private Long id;
    @Column(name = "CODE", nullable = true, length = 255)
    private String code;
    @Column(name = "NAME", nullable = false, length = 255)
    private String name;
    @Column(name = "PARENT_ID", nullable = true, precision = 0)
    private Long parentId;
    @Column(name = "STATUS", nullable = true, precision = 0)
    private Boolean status;
    @Column(name = "PATH", nullable = true, length = 255)
    private String path;
    @Column(name = "NAME_LEVEL1", nullable = true, length = 255)
    private String nameLevel1;
    @Column(name = "NAME_LEVEL2", nullable = true, length = 255)
    private String nameLevel2;
    @Column(name = "NAME_LEVEL3", nullable = true, length = 255)
    private String nameLevel3;
    @Column(name = "GROUP_ORDER", nullable = true, precision = 0)
    private Long groupOrder;
    @Column(name = "GROUP_LEVEL", nullable = true, precision = 0)
    private Long groupLevel = 1l;
    @Column(name = "PROVINCE_ID", nullable = true, precision = 0)
    private Long provinceId;
    @Column(name = "PROVINCE_CODE", nullable = true, length = 255)
    private String provinceCode;
    @Column(name = "AREA_ID", nullable = true, precision = 0)
    private Long areaId;
    @Column(name = "AREA_CODE", nullable = true, length = 255)
    private String areaCode;
    @Column(name = "SYS_GROUP_ID", nullable = true, precision = 0)
    private Long sysGroupId;
    @Column(name = "SYS_GROUP_NAME", nullable = true, length = 255)
    private String sysGroupName;
}
