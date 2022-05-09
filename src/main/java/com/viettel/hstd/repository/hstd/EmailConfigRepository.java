package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.EmailConfigEntity;

import java.util.List;

public interface EmailConfigRepository extends SoftJpaRepository<EmailConfigEntity, Long> {
    List<EmailConfigEntity> findByEmail(String fromEmail);
}
