package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.DocumentRetirementEntity;
import com.viettel.hstd.entity.hstd.DocumentTypeEntity;

import java.util.ArrayList;

public interface DocumentRetirementRepository extends SoftJpaRepository<DocumentRetirementEntity, Long> {
    ArrayList<DocumentRetirementEntity> findByCode( String code);
}
