package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Table(name = "HISTORY_UPLOAD")
@SequenceGenerator(name = "history_upload_sequence", initialValue = 1, allocationSize = 1,  sequenceName = "HISTORY_UPLOAD_SEQ")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class HistoryUploadEntity extends EntityBase {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "history_upload_sequence")
    private Long id;
    @Column(name = "URL")
    private String url;
    @Column(name = "FILE_SIZE")
    private Long size;
    @Column(name = "TYPE")
    private String type;
    @Column(name = "TITLE")
    private String title;
}
