package com.viettel.hstd.core.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.hstd.core.custom.CustomLocalDateTimeDeserializer;
import com.viettel.hstd.core.custom.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class EntityBase implements Serializable {
    private static final long serialVersionUID = 1232323L;

    @Column(name = "DEL_FLAG", nullable = false)
    protected Boolean delFlag = false;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @CreatedDate
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    protected LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    @LastModifiedDate
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    protected LocalDateTime updatedAt;

    @Column(name = "CREATED_BY", nullable = false, updatable = false)
    @CreatedBy
    protected Long createdBy;

    @Column(name = "UPDATED_BY", nullable = false)
    @LastModifiedBy
    protected Long updatedBy;

    @Column(name = "IS_ACTIVE", nullable = false)
    protected Boolean isActive = true;
}
