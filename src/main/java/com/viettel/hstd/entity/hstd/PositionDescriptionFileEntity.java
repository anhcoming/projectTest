package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "POSITION_DESCRIPTION_FILE")
@Where(clause = "DEL_FLAG = 0")
@SQLDelete(sql = "update POSITION_DESCRIPTION_FILE set DEL_FLAG = 1 where POSITION_DESCRIPTION_FILE_ID = ?")
public class PositionDescriptionFileEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POSITION_DESCRIPTION_FILE_GEN")
    @SequenceGenerator(name = "POSITION_DESCRIPTION_FILE_GEN", allocationSize = 1, initialValue = 1, sequenceName = "POSITION_DESCRIPTION_FILE_SEQ")
    @Column(name = "POSITION_DESCRIPTION_FILE_ID", nullable = false)
    private Long id;

    @Column(name = "FILE_TITLE", nullable = false)
    private String fileTitle;

    @Column(name = "FILE_URL", nullable = false)
    private String fileUrl;

    @Column(name = "POSITION_DESCRIPTION_ID", nullable = false)
    private Long positionDescriptionId;

}
