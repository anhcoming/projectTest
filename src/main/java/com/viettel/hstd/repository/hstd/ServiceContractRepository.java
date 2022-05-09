package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.ServiceContractEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceContractRepository extends SoftJpaRepository<ServiceContractEntity, Long> {
}
