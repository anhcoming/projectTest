package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "IMPORT_HISTORY_DETAIL", indexes = {
        @Index(name = "import_history_idx", columnList = "IMPORT_HISTORY_ID")
})
@Where(clause = "DEL_FLAG = 0")
@SQLDelete(sql = "update IMPORT_HISTORY_DETAIL set DEL_FLAG = 1 where DETAIL_ID = ?")
@Getter
@Setter
public class ImportHistoryDetailEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IMPORT_HISTORY_DETAIL_GEN")
    @SequenceGenerator(name = "IMPORT_HISTORY_DETAIL_GEN", allocationSize = 1, initialValue = 1, sequenceName = "IMPORT_HISTORY_DETAIL_SEQ")
    @Column(name = "DETAIL_ID", nullable = false)
    private Long id;

    @Column(name = "IMPORT_HISTORY_ID", nullable = false)
    private Long importHistoryId;

    @Column(name = "ROW_CONTENT", nullable = false)
    private String rowContent;

}
