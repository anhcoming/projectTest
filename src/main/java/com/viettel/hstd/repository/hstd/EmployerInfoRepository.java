package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.EmployerInfoEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployerInfoRepository extends SoftJpaRepository<EmployerInfoEntity, Long> {
    EmployerInfoEntity findByUnitId(Long unitId);

    Boolean existsByUnitId(Long unitId);
}
