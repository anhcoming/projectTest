package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "RECRUITMENT_PROGRESS_DETAIL_FILE")
@Getter
@Setter
@Where(clause = "DEL_FLAG = 0")
@SQLDelete(sql = "update RECRUITMENT_PROGRESS_DETAIL_FILE set DEL_FLAG = 1 where DETAIL_FILE_ID = ?")
public class RecruitmentProgressDetailFileEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECRUITMENT_PROGRESS_DETAIL_FILE_GEN")
    @SequenceGenerator(name = "RECRUITMENT_PROGRESS_DETAIL_FILE_GEN", allocationSize = 1, initialValue = 1, sequenceName = "RECRUITMENT_PROGRESS_DETAIL_FILE_SEQ")
    @Column(name = "DETAIL_FILE_ID", nullable = false)
    private Long id;

    @Column(name = "FILE_TITLE")
    private String fileTitle;

    @Column(name = "FILE_URL")
    private String fileUrl;

    @Column(name = "PROGRESS_DETAIL_ID", nullable = false)
    private Long detailId;

}
