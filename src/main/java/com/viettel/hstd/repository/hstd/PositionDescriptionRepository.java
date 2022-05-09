package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.entity.hstd.PositionDescriptionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PositionDescriptionRepository extends JpaRepository<PositionDescriptionEntity, Long> {

    @Query(value = "select case when count(p.POSITION_DESCRIPTION) > 0 then 'true' else 'false' end from POSITION_DESCRIPTION p " +
            "where p.UNIT_CODE = :unitCode and p.POSITION_CODE = :positionCode and " +
            "(:departmentCode is null or p.DEPARTMENT_CODE = :departmentCode) and " +
            "(:groupCode is null or p.GROUP_CODE = :groupCode) and " +
            "p.DEL_FLAG = 0 and ROWNUM <= 1", nativeQuery = true)
    boolean existsRequest(String unitCode, String departmentCode, String groupCode, String positionCode);


    @Query("from PositionDescriptionEntity p where p.delFlag = false and " +
            "(:unitId is null or p.unitId = :unitId) and " +
            "(:departmentId is null or p.departmentId = :departmentId) and " +
            "(:groupId is null or p.groupId = :groupId) and " +
            "(:positionId is null or p.positionId = :positionId) and " +
            "(:hasDescription is null or p.hasDescription = :hasDescription)")
    Page<PositionDescriptionEntity> search(Long unitId, Long departmentId, Long groupId, Long positionId, Boolean hasDescription, Pageable pageable);
}
