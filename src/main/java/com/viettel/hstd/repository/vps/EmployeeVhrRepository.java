package com.viettel.hstd.repository.vps;

import com.viettel.hstd.entity.vps.EmployeeVhrEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EmployeeVhrRepository extends JpaRepository<EmployeeVhrEntity, Long>,
        JpaSpecificationExecutor<EmployeeVhrEntity> {
    @Query(value = "select  u from  EmployeeVhrEntity  u where u.employeeId IN ?1 ")
    List<EmployeeVhrEntity> findByEmployeeIdIn(List<Long> ids);

    Optional<EmployeeVhrEntity> findFirstByEmployeeCode(String code);

    @Query("SELECT T.mobileNumber FROM EmployeeVhrEntity T WHERE T.employeeId = ?1")
    String getPhoneNumberByEmployeeId(Long employeeId);

    List<EmployeeVhrEntity> findByEmployeeCodeIn(Set<String> employeeCodeSet);

    Optional<EmployeeVhrEntity> findByEmployeeCode(String employeeCode);
}
