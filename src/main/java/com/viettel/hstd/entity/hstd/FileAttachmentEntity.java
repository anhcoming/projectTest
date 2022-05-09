package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "FILE_ATTACHMENT")
@SequenceGenerator(name = "FILE_ATTACHMENT_GEN", initialValue = 1, allocationSize = 1, sequenceName = "FILE_ATTACHMENT_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class FileAttachmentEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FILE_ATTACHMENT_GEN")
    @Column(name = "FILE_ATTACHMENT_ID")
    private Long fileAttachmentId;
    /**
     * Loai file dinh kem
     */
    @Column(name = "FILE_TYPE")
    private Integer fileType;
    /*
    File dinh kem cho doi tuong nao?
     */
    @Column(name = "FILE_ITEM_ID")
    private Long fileItemId;
    /*
    Ten file dinh kem
     */
    @Column(name = "FILE_NAME")
    private String fileName;
    /*
    Duong dan file dinh kem
     */
    @Column(name = "FILE_PATH")
    private String filePath;
}
