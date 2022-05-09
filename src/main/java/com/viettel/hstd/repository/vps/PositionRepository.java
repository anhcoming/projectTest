package com.viettel.hstd.repository.vps;

import com.viettel.hstd.entity.vps.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PositionRepository extends JpaRepository<PositionEntity, Long>,
        JpaSpecificationExecutor<PositionEntity> {
    @Query("select u from  PositionEntity  u where u.positionId IN ?1")
    ArrayList<PositionEntity> findByIdIn(ArrayList<Long> ids);

    @Query("select u.positionId from  PositionEntity  u where u.positionId IN ?1")
    List<Long> findByIdIn(List<Long> ids);

    @Query("select u from  PositionEntity  u where u.positionCode IN ?1")
    ArrayList<PositionEntity> findByPositionCodeIn(ArrayList<String> codes);

    Optional<PositionEntity> findFirstByPositionCode(String positionCode);

    List<PositionEntity> findByPositionNameIn(Set<String> positionNameSet);

    Optional<PositionEntity> findFirstByPositionName(String positionName);
}
