package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.EmailTemplateEntity;

import java.util.ArrayList;

public interface EmailTemplateRepository extends SoftJpaRepository<EmailTemplateEntity, Long> {

    ArrayList<EmailTemplateEntity> findByType(Integer type);
}
