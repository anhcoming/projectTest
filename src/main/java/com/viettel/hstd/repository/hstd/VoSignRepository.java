package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.VoSignEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoSignRepository extends SoftJpaRepository<VoSignEntity, Long> {
    List<VoSignEntity> findByTransCode(String transCode);
}
