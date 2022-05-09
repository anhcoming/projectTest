package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.FileAttachmentEntity;
import com.viettel.hstd.entity.hstd.FreelanceContractEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface FreelanceContractRepository extends SoftJpaRepository<FreelanceContractEntity, Long> {
}
