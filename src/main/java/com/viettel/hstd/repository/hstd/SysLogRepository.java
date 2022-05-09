package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.SysConfigEntity;
import com.viettel.hstd.entity.hstd.SysLogEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SysLogRepository extends SoftJpaRepository<SysLogEntity, Long> {
    boolean existsByCreatedAtGreaterThanEqual(LocalDateTime localDateTime);
}
