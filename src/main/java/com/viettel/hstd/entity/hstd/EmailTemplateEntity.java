package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "EMAIL_TEMPLATE")
@SequenceGenerator(name = "EMAIL_TEMPLATE_GEN", initialValue = 1, allocationSize = 1, sequenceName = "EMAIL_TEMPLATE_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class EmailTemplateEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMAIL_TEMPLATE_GEN")
    @Column(name = "EMAIL_TEMPLATE_ID")
    private Long emailTemplateId;
    @Column(name = "NAME")
    private String name;
    @Column(name = "CONTENT")
    private String content;
    @Column(name = "TYPE")
    private Integer type;

    @Column(name = "FILE_NAME")
    private String fileName;
    @Column(name = "FILE_PATH")
    private String filePath;
}
