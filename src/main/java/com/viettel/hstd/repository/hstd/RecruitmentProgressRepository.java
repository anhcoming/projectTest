package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.core.repository.SoftJpaRepository;
import com.viettel.hstd.dto.hstd.RecruitmentProgressDTO;
import com.viettel.hstd.entity.hstd.RecruitmentProgressEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;

import javax.persistence.TemporalType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface RecruitmentProgressRepository extends SoftJpaRepository<RecruitmentProgressEntity, Long> {

    @Query(value = "select r.PROGRESS_ID as progressId, r.POSITION_ID as positionId, r.POSITION_CODE as positionCode, r.POSITION_NAME as positionName, " +
            "r.ORGANIZATION_ID as organizationId, r.ORGANIZATION_CODE as organizationCode, r.ORGANIZATION_NAME as organizationName," +
            "r.HR_PLAN as hrPlan, r.CURRENT_EMP as currentEmp, r.DEADLINE as deadline, r.RECRUITED as recruited, " +
            "case when r.RECRUITED >= r.HR_PLAN then 1 else 0 end as completionRate, r.DESCRIPTION as description, e.listEmployee as listEmployee, e.listEmailRecipients as listEmailRecipients " +
            "from RECRUITMENT_PROGRESS r inner join " +
            "(select e.RECRUITMENT_PROGRESS_ID, " +
            "LISTAGG(case when e.IS_HR = 1 then CONCAT(CONCAT(e.EMPLOYEE_CODE,' - '), e.EMPLOYEE_NAME) end,', ') WITHIN GROUP (ORDER BY e.PROGRESS_EMPLOYEE_ID) as listEmployee," +
            "LISTAGG(case when e.IS_HR = 0 then CONCAT(CONCAT(e.EMPLOYEE_CODE,' - '), e.EMPLOYEE_NAME) end,', ') WITHIN GROUP (ORDER BY e.PROGRESS_EMPLOYEE_ID) as listEmailRecipients " +
            "from RECRUITMENT_PROGRESS_EMPLOYEE e where e.DEL_FLAG = 0 GROUP BY e.RECRUITMENT_PROGRESS_ID) e " +
            "on r.PROGRESS_ID = e.RECRUITMENT_PROGRESS_ID where " +
            "r.DEL_FLAG = 0 and " +
            "(:positionId is null or r.POSITION_ID = TO_NUMBER(:positionId)) and " +
            "(:organizationId is null or r.ORGANIZATION_ID = TO_NUMBER(:organizationId)) and " +
            "(:completionRate is null or (TO_NUMBER(:completionRate) = 1 and r.RECRUITED >= r.HR_PLAN) or (TO_NUMBER(:completionRate) = 0 and r.RECRUITED < r.HR_PLAN)) and " +
            "(:fromDeadline is null or r.DEADLINE >= :fromDeadline) and " +
            "(:toDeadline is null or r.DEADLINE <= :toDeadline) and " +
            "(:completionRate is null or (TO_NUMBER(:completionRate) = 1 and r.RECRUITED >= r.HR_PLAN) or (TO_NUMBER(:completionRate) = 0 and r.RECRUITED < r.HR_PLAN)) and " +
            "(:employeeId is null or exists(select e.PROGRESS_EMPLOYEE_ID from RECRUITMENT_PROGRESS_EMPLOYEE e " +
            "where e.DEL_FLAG = 0 and e.EMPLOYEE_ID = TO_NUMBER(:employeeId) and e.IS_HR = 1 and e.RECRUITMENT_PROGRESS_ID = r.PROGRESS_ID)) and " +
            "(:employeeEmailRecipientId is null or exists(select e.PROGRESS_EMPLOYEE_ID from RECRUITMENT_PROGRESS_EMPLOYEE e " +
            "where e.DEL_FLAG = 0 and e.EMPLOYEE_ID = TO_NUMBER(:employeeEmailRecipientId) and e.IS_HR = 0 and e.RECRUITMENT_PROGRESS_ID = r.PROGRESS_ID))" +
            "ORDER BY r.DEADLINE DESC ",
            countQuery = "select count(r.PROGRESS_ID) from RECRUITMENT_PROGRESS_ID r where r.DEL_FLAG = 0 and " +
                    "(:positionId is null or r.POSITION_ID = TO_NUMBER(:positionId)) and " +
                    "(:organizationId is null or r.ORGANIZATION_ID = TO_NUMBER(:organizationId)) and " +
                    "(:completionRate is null or (TO_NUMBER(:completionRate) = 1 and r.RECRUITED >= r.HR_PLAN) or (TO_NUMBER(:completionRate) = 0 and r.RECRUITED < r.HR_PLAN)) and " +
                    "(:fromDeadline is null or r.DEADLINE >= :fromDeadline) and " +
                    "(:toDeadline is null or r.DEADLINE <= :toDeadline) and " +
                    "(:employeeId is null or exists (select e.PROGRESS_EMPLOYEE_ID from RECRUITMENT_PROGRESS_EMPLOYEE e " +
                    "where e.DEL_FLAG = 0 and e.EMPLOYEE_ID = TO_NUMBER(:employeeId) and e.IS_HR = 1 and e.RECRUITMENT_PROGRESS_ID = r.PROGRESS_ID)) and" +
                    "(:employeeEmailRecipientId is null or exists(select e.PROGRESS_EMPLOYEE_ID from RECRUITMENT_PROGRESS_EMPLOYEE e " +
                    "where e.DEL_FLAG = 0 and e.EMPLOYEE_ID = TO_NUMBER(:employeeEmailRecipientId) and e.IS_HR = 0 and e.RECRUITMENT_PROGRESS_ID = r.PROGRESS_ID))"
            , nativeQuery = true)
    Page<RecruitmentProgressDTO.Projection> search(Long positionId, Long organizationId, Integer completionRate, Long employeeId, Long employeeEmailRecipientId,
                                                   @Temporal(TemporalType.DATE) Date fromDeadline, @Temporal(TemporalType.DATE) Date toDeadline,
                                                   Pageable pageable);

    @Query(value = "select rp.PROGRESS_ID as id, rp.POSITION_ID as positionId, rp.POSITION_CODE as positionCode, rp.POSITION_NAME as positionName," +
            "rp.ORGANIZATION_ID as organizationId, rp.ORGANIZATION_CODE as organizationCode, rp.ORGANIZATION_NAME as organizationName," +
            "rp.HR_PLAN as hrPlan, rp.CURRENT_EMP as currentEmp, rp.DESCRIPTION as description, rp.DEADLINE as deadline, d.recruited as recruited " +
            "from RECRUITMENT_PROGRESS rp " +
            "inner join " +
            "(select d.RECRUITMENT_PROGRESS_ID as progressId, sum(d.RECRUITED) as recruited " +
            "from RECRUITMENT_PROGRESS_DETAIL d " +
            "where d.DEL_FLAG = 0 " +
            "group by d.RECRUITMENT_PROGRESS_ID) d " +
            "on rp.PROGRESS_ID = d.progressId " +
            "where rp.DEL_FLAG = 0 " +
            "and rp.DEADLINE > :currentDate", nativeQuery = true)
    List<RecruitmentProgressDTO.DailyUpdateProjection> getDailyUpdate(LocalDate currentDate);

    boolean existsByPositionCodeAndOrganizationCodeAndDeadline(String positionCode, String organizationCode, LocalDate deadline);
}
