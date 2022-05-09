package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.entity.hstd.EmployeeMonthlyReviewEntity;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeMonthlyReviewRepository extends SoftJpaRepository<EmployeeMonthlyReviewEntity, Long> {
    @Query(value = "select emr from EmployeeMonthlyReviewEntity emr where emr.employeeId = ?1 and emr.month >= ?2 and emr.month <= ?3 order by emr.month ")
    List<EmployeeMonthlyReviewEntity> getSpecial(Long employeeId, LocalDate startDate, LocalDate endDate);

    @Query(value = "select AVG(Case when GRADE = 'A' then 40\n" +
            "                  else (case when GRADE = 'B' then 30\n" +
            "                             else (case when GRADE = 'C' then 30\n" +
            "                                        else 10 end ) end ) end)\n" +
            "from EMPLOYEE_MONTHLY_REVIEW\n" +
            "where EMPLOYEE_ID = ?1 and add_months(MONTH, 12)  >= trunc(?2, 'MM')", nativeQuery = true)
    Optional<Float> getAverageScore(Long employeeId, LocalDate evaluateMonth);

    @Query(value = "SELECT emc FROM EmployeeMonthlyReviewEntity emc where emc.employeeId = ?1 and  emc.month =?2 and emc.delFlag = false ")
    List<EmployeeMonthlyReviewEntity> getEmployeeMonthlyReviewEntitiesByEmployeeIdAndMonth(Long employeeId, LocalDate month);

}
