package com.viettel.hstd.entity.hstd.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "PROVINCE_AREA_SHORT")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceAreaShortEntity {
    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    private Long id;
    @Column(name = "CODE", nullable = true, length = 255)
    private String code;
    @Column(name = "NAME", nullable = false, length = 255)
    private String name;
    @Column(name = "PARENT_ID", nullable = true, precision = 0)
    private Long parentId;
    @Column(name = "PROVINCE_CODE", nullable = true, length = 255)
    private String provinceCode;

}
