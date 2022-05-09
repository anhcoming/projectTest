package com.viettel.hstd.repository.vps;

import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VhrFutureOrganizationRepository extends JpaRepository<VhrFutureOrganizationEntity, Long>,
        JpaSpecificationExecutor<VhrFutureOrganizationEntity> {
    VhrFutureOrganizationEntity findByOrganizationId(Long organizationId);

    List<VhrFutureOrganizationEntity> findByNameInAndOrgLevelManage(Set<String> nameSet, Integer orgLevelManage);

    @Query("select case when count(v) > 0 then true else false end from VhrFutureOrganizationEntity  v  where  v.orgParentId = ?1 and v.organizationId = ?2")
    Boolean checkDepartmentWithUnit(Long unitId, Long departmentId);

    Optional<VhrFutureOrganizationEntity> findFirstByCode(String code);

    Optional<VhrFutureOrganizationEntity> findFirstByCodeAndPathLike(String code, String path);
}
