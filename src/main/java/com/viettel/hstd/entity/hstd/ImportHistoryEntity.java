package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.constant.ImportConstant;
import com.viettel.hstd.core.entity.EntityBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "IMPORT_HISTORY", indexes = {
        @Index(name = "import_history_setting_idx", columnList = "IMPORT_CODE"),
        @Index(name = "import_at_idx", columnList = "IMPORT_AT DESC")
})
@Where(clause = "DEL_FLAG = 0")
@SQLDelete(sql = "update IMPORT_HISTORY set DEL_FLAG = 1 where IMPORT_HISTORY_ID = ?")
@Getter
@Setter
public class ImportHistoryEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IMPORT_HISTORY_GEN")
    @SequenceGenerator(name = "IMPORT_HISTORY_GEN", allocationSize = 1, initialValue = 1, sequenceName = "IMPORT_HISTORY_SEQ")
    @Column(name = "IMPORT_HISTORY_ID", nullable = false)
    private Long id;

    @Column(name = "EMPLOYEE_ID")
    private Long employeeId;

    @Column(name = "EMPLOYEE_CODE")
    private String employeeCode;

    @Column(name = "EMPLOYEE_NAME")
    private String employeeName;

    @CreatedDate
    @Column(name = "IMPORT_AT", nullable = false, updatable = false)
    private LocalDateTime importAt;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private ImportConstant.ImportStatus importStatus;

    @Column(name = "STATUS_TITLE")
    private String statusTitle;

    @Column(name = "FILE_TITLE")
    private String fileTitle;

    @Column(name = "FILE_URL")
    private String fileUrl;

    @Column(name = "IMPORT_CODE", nullable = false)
    @Enumerated(EnumType.STRING)
    private ImportConstant.ImportCode importCode;

    @Column(name = "IMPORT_CODE_TITLE")
    private String importCodeTitle;

    @PrePersist
    @PreUpdate
    public void fillEnumValue() {
        if (Objects.nonNull(importStatus)) statusTitle = importStatus.title;
        if (Objects.nonNull(importCode)) importCodeTitle = importCode.title;
    }


}
