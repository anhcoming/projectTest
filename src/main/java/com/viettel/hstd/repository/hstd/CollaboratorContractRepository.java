package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.CollaboratorContractEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface CollaboratorContractRepository extends SoftJpaRepository<CollaboratorContractEntity, Long> {
}
