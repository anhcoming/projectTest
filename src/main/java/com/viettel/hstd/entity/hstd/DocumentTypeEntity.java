package com.viettel.hstd.entity.hstd;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.viettel.hstd.constant.ContractType;
import com.viettel.hstd.core.entity.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DOCUMENT_TYPE")
@SequenceGenerator(name = "DOCUMENT_TYPE_GEN", initialValue = 1, allocationSize = 1, sequenceName = "DOCUMENT_TYPE_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class DocumentTypeEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCUMENT_TYPE_GEN")
    @Column(name = "DOCUMENT_TYPE_ID")
    private Long documentTypeId;

    @Column(name = "CODE")
    private String code;

    @Column(name = "NAME")
    private String name;

    @Column(name = "OBLIGATE_FLAG")
    private Boolean obligateFlag;

    // Loại hợp đồng
    @Column(name = "TYPE")
    private ContractType type;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive;
}
