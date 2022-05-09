package com.viettel.hstd.repository.vps;

import com.viettel.hstd.entity.vps.EmployeeVhrEntityFull;
        import org.springframework.data.jpa.repository.JpaRepository;
        import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
        import org.springframework.data.jpa.repository.Query;

        import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
        import java.util.Optional;

public interface EmployeeVhrRepositoryFull extends JpaRepository<EmployeeVhrEntityFull, Long>,
        JpaSpecificationExecutor<EmployeeVhrEntityFull> {
    @Query(value = "select  u from  EmployeeVhrEntityFull  u where u.employeeId IN ?1 ")
    List<EmployeeVhrEntityFull> findByEmployeeIdIn(List<Long> ids);

    Optional<EmployeeVhrEntityFull> findFirstByEmployeeCode(String code);

    @Query("select count(v) from EmployeeVhrEntityFull v inner join VhrFutureOrganizationEntity o on v.organizationId = o.organizationId " +
            "where v.status = '1' and v.positionId = :positionId and o.path like concat('/148841/148842/165317/9004482/',concat(:organizationId,'%'))")
    Integer countByPosIdAndPath(Long positionId, Long organizationId);

    Optional<EmployeeVhrEntityFull> findFirstByEmail(String email);

    List<EmployeeVhrEntityFull> findByEmployeeCodeIn(Collection<String> codes);
}