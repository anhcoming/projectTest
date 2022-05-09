package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.constant.NewContractStatus;
import com.viettel.hstd.core.repository.SoftJpaRepository;
        import com.viettel.hstd.entity.hstd.ProbationaryContractEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProbationaryContractRepository extends SoftJpaRepository<ProbationaryContractEntity, Long> {
        @Query("UPDATE ProbationaryContractEntity t set t.newContractStatus = ?2 where t.contractId = ?1")
        @Modifying
        @Transactional
        void updateNewContractStatus(Long contractId, NewContractStatus newContractStatusInt);
}