package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.dto.hstd.RecruitmentProgressDTO;
import com.viettel.hstd.dto.hstd.RecruitmentProgressEmployeeDTO;
import com.viettel.hstd.entity.hstd.RecruitmentProgressEmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public interface RecruitmentProgressEmployeeRepository extends JpaRepository<RecruitmentProgressEmployeeEntity , Long> {

    @Modifying
    @Query("update RecruitmentProgressEmployeeEntity e set e.delFlag = true where e.delFlag = false and e.recruitmentProgressId = :progressId")
    void deleteAllByRecruitmentProgressId(Long progressId);

    @Query("select new com.viettel.hstd.dto.hstd.RecruitmentProgressEmployeeDTO$Response(e.employeeId, e.employeeCode, e.employeeName, e.employeeEmail, e.isHr) " +
            "from RecruitmentProgressEmployeeEntity e where e.delFlag = false and e.recruitmentProgressId = :progressId")
    List<RecruitmentProgressEmployeeDTO.Response> findByRecruitmentProgressId(Long progressId);

    @Query(value = "select e.EMPLOYEE_NAME as employeeName, " +
            "e.EMPLOYEE_EMAIL as employeeEmail," +
            "r.POSITION_NAME as positionName," +
            "r.ORGANIZATION_NAME as organizationName," +
            "r.HR_PLAN as hrPlan, " +
            "r.RECRUITED as recruited," +
            "r.CURRENT_EMP as currentEmp," +
            "r.DESCRIPTION as description," +
            "case when r.RECRUITED >= r.HR_PLAN then 'Hoàn thành' else 'Chậm tiến độ' end as completionRate," +
            "TO_CHAR(r.DEADLINE, 'DD/MM/YYYY') as deadline," +
            "recipients.listRecipients as listRecipients " +
            "from RECRUITMENT_PROGRESS_EMPLOYEE e " +
            "inner join RECRUITMENT_PROGRESS r on e.RECRUITMENT_PROGRESS_ID = r.PROGRESS_ID " +
            "left join (select listagg(e.EMPLOYEE_EMAIL, ',') within group ( order by e.PROGRESS_EMPLOYEE_ID) as listRecipients, e.RECRUITMENT_PROGRESS_ID " +
            "from RECRUITMENT_PROGRESS_EMPLOYEE e " +
            "where e.DEL_FLAG = 0 " +
            "and e.IS_HR = 0 " +
            "group by e.RECRUITMENT_PROGRESS_ID) recipients " +
            "on e.RECRUITMENT_PROGRESS_ID = recipients.RECRUITMENT_PROGRESS_ID " +
            "where e.DEL_FLAG = 0 " +
            "and r.DEL_FLAG = 0 " +
            "and e.IS_HR = 1 " +
            "and r.deadline >= :fromDate and r.RECRUITED < r.HR_PLAN ", nativeQuery = true)
    Stream<RecruitmentProgressDTO.EmailResponseProjection> findAllStream(LocalDate fromDate);
}
