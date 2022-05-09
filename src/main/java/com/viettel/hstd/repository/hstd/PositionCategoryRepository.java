package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.entity.hstd.PositionCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PositionCategoryRepository extends JpaRepository<PositionCategoryEntity, Long> {
    Optional<PositionCategoryEntity> findByPositionCode(String positionCode);
}
