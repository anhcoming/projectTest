package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.dto.hstd.RecruitmentProgressDTO;
import com.viettel.hstd.entity.hstd.EmailAlertRecipientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public interface EmailAlertRecipientRepository extends JpaRepository<EmailAlertRecipientEntity, Long> {

    List<EmailAlertRecipientEntity> findByEmailAlertConfigId(Long emailAlertConfigId);

    @Modifying
    @Query("update EmailAlertRecipientEntity e set e.delFlag = true where e.emailAlertConfigId = :emailAlertConfigId")
    void deleteAllByEmailAlertConfigId(Long emailAlertConfigId);

    @Query(value = "select ROWNUM as numberOrder, r.EMPLOYEE_NAME as recipient, r.EMPLOYEE_EMAIL as employeeEmail, " +
            "r.POSITION_NAME as positionName, r.ORGANIZATION_NAME as organizationName, r.HR_PLAN as hrPlan, " +
            "r.CURRENT_EMP as currentEmp, r.RECRUITED as recruited, r.listEmployee as employeeName, " +
            "r.DESCRIPTION as description, r.deadline as deadline, r.completionRate as completionRate " +
            "from (select ear.EMPLOYEE_NAME," +
            "             ear.EMPLOYEE_EMAIL," +
            "             eac.POSITION_NAME," +
            "             eac.ORGANIZATION_NAME," +
            "             rp_agg.HR_PLAN," +
            "             rp_agg.CURRENT_EMP," +
            "             rp_agg.RECRUITED," +
            "             rp_agg.listEmployee," +
            "             rp_agg.DESCRIPTION," +
            "             TO_CHAR(rp_agg.DEADLINE, 'DD/MM/YYYY') as deadline, " +
            "             rp_agg.completionRate " +
            "      from EMAIL_ALERT_RECIPIENT ear " +
            "               inner join EMAIL_ALERT_CONFIG EAC on ear.EMAIL_ALERT_CONFIG_ID = EAC.EMAIL_ALERT_CONFIG_ID " +
            "               inner join (select rp.HR_PLAN, " +
            "                                  rp.RECRUITED, " +
            "                                  rp.CURRENT_EMP, " +
            "                                  rp.POSITION_ID, " +
            "                                  rp.ORGANIZATION_ID, " +
            "                                  e.listEmployee, " +
            "                                  rp.DESCRIPTION, " +
            "                                  rp.DEADLINE, " +
            "                                  case when rp.RECRUITED >= rp.HR_PLAN then 'Hoàn thành' else 'Chậm tiến độ' end as completionRate " +
            "                           from RECRUITMENT_PROGRESS rp " +
            "                                    inner join (select e.RECRUITMENT_PROGRESS_ID, " +
            "                                                       LISTAGG(CONCAT(CONCAT(e.EMPLOYEE_CODE, ' - '), e.EMPLOYEE_NAME),', ') WITHIN GROUP (ORDER BY e.PROGRESS_EMPLOYEE_ID) as listEmployee " +
            "                                                from RECRUITMENT_PROGRESS_EMPLOYEE e " +
            "                                                where e.DEL_FLAG = 0 " +
            "                                                GROUP BY e.RECRUITMENT_PROGRESS_ID) e " +
            "                                               on rp.PROGRESS_ID = e.RECRUITMENT_PROGRESS_ID " +
            "                           where rp.DEL_FLAG = 0 and rp.deadline >= :fromDate) rp_agg " +
            "                          on (eac.POSITION_ID = rp_agg.POSITION_ID and eac.ORGANIZATION_ID = rp_agg.ORGANIZATION_ID) " +
            "      where ear.DEL_FLAG = 0 and eac.DEL_FLAG = 0) r", nativeQuery = true)
    Stream<RecruitmentProgressDTO.EmailResponseProjection> getEmailResponseForLeader(LocalDate fromDate);


}
