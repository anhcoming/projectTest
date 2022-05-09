package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.constant.ImportConstant;
import com.viettel.hstd.core.entity.EntityBase;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "IMPORT_HISTORY_SETTING")
@Where(clause = "DEL_FLAG = 0")
@SQLDelete(sql = "update IMPORT_HISTORY_SETTING set DEL_FLAG = 1 where SETTING_ID = ?")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportHistorySettingEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IMPORT_HISTORY_SETTING_GEN")
    @SequenceGenerator(name = "IMPORT_HISTORY_SETTING_GEN", allocationSize = 1, initialValue = 1, sequenceName = "IMPORT_HISTORY_SETTING_SEQ")
    @Column(name = "SETTING_ID", nullable = false)
    private Long id;

    @Column(name = "IMPORT_CODE", nullable = false)
    @Enumerated(EnumType.STRING)
    private ImportConstant.ImportCode importCode;

    @Column(name = "IMPORT_TITLE")
    private String importTitle;

    @Column(name = "IMPORT_FIELD")
    private String importField;

    @Column(name = "IMPORT_VALUE_TITLE")
    private String importValueTitle;

    @Column(name = "IMPORT_TYPE")
    @Enumerated(EnumType.STRING)
    private ImportConstant.ImportType importType;

    @Column(name = "IMPORT_FORMAT")
    private String importFormat;
}
