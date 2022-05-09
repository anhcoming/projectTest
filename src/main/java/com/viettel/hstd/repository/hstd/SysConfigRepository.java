package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.SysConfigEntity;

import java.util.Optional;

public interface SysConfigRepository extends SoftJpaRepository<SysConfigEntity, Long> {
    Optional<SysConfigEntity> findFirstByConfigKey(String key);
}
