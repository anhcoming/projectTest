package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.constant.ContractType;
import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.DocumentTypeEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public interface DocumentTypeRepository extends SoftJpaRepository<DocumentTypeEntity, Long> {
    ArrayList<DocumentTypeEntity> findByType(ContractType type);

    ArrayList<DocumentTypeEntity> findByTypeAndAndCode(ContractType type, String code);
    List<DocumentTypeEntity> findByTypeAndIsActive(ContractType contractType, boolean isActive);
}
