package com.viettel.hstd.entity.hstd;

import com.viettel.hstd.core.entity.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "EMAIL_CATEGORY")
@SequenceGenerator(name = "EMAIL_CATEGORY_GEN", initialValue = 1, allocationSize = 1, sequenceName = "EMAIL_CATEGORY_SEQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_FLAG = 0")
public class EmailCategoryEntity extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMAIL_CATEGORY_GEN")
    @Column(name = "EMAIL_CATEGORY_ID")
    private Long emailCategoryId;

    @Column(name = "EMAIL_SEND")
    private String emailSend;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "EMAIL_TEMPLATE_ID")
    private Long emailTemplateId;

    @Column(name = "IS_STATUS")
    private Boolean isStatus = false;

    @Column(name = "EMAIL_CONFIG_ID")
    private Long emailConfigId;

    @Column(name = "CREATED_NAME")
    private String createdName;
}
