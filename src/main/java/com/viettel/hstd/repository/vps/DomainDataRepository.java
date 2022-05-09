package com.viettel.hstd.repository.vps;

import com.viettel.hstd.entity.vps.DomainDataEntity;
import com.viettel.hstd.entity.vps.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface DomainDataRepository extends JpaRepository<DomainDataEntity, Long>,
        JpaSpecificationExecutor<DomainDataEntity> {
    List<DomainDataEntity> findAllByIsActive(Boolean isActive);

    Optional<DomainDataEntity> findFirstByDataCode(String domainCode);
}
