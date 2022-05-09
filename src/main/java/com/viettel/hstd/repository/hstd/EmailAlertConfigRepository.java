package com.viettel.hstd.repository.hstd;

import com.viettel.hstd.dto.hstd.EmailAlertConfigDTO;
import com.viettel.hstd.entity.hstd.EmailAlertConfigEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmailAlertConfigRepository extends JpaRepository<EmailAlertConfigEntity, Long> {

    @Query(value = "select config.EMAIL_ALERT_CONFIG_ID as emailAlertConfigId, config.POSITION_ID as positionId, config.POSITION_CODE as positionCode, " +
            "config.POSITION_NAME as positionName, config.ORGANIZATION_ID as organizationId, " +
            "config.ORGANIZATION_CODE as organizationCode, config.ORGANIZATION_NAME as organizationName, r.email as emailRecipients " +
            "from EMAIL_ALERT_CONFIG config " +
            "inner join (select recipient.EMAIL_ALERT_CONFIG_ID," +
            "LISTAGG(recipient.EMPLOYEE_EMAIL, ', ') WITHIN GROUP (ORDER BY recipient.EMAIL_ALERT_CONFIG_ID) as email " +
            "from EMAIL_ALERT_RECIPIENT recipient where recipient.DEL_FLAG = 0 " +
            "group by recipient.EMAIL_ALERT_CONFIG_ID) r " +
            "on (config.EMAIL_ALERT_CONFIG_ID = r.EMAIL_ALERT_CONFIG_ID) " +
            "where config.DEL_FLAG = 0 and " +
            "(:positionId is null or config.POSITION_ID = TO_NUMBER(:positionId)) and " +
            "(:organizationId is null or config.ORGANIZATION_ID = TO_NUMBER(:organizationId)) and " +
            "(:employeeId is null or exists(select e.EMAIL_ALERT_RECIPIENT_ID from EMAIL_ALERT_RECIPIENT e " +
            "where e.DEL_FLAG = 0 and e.EMPLOYEE_ID = TO_NUMBER(:employeeId) and e.EMAIL_ALERT_CONFIG_ID = config.EMAIL_ALERT_CONFIG_ID))",
            countQuery = "select count(config.EMAIL_ALERT_CONFIG_ID) from EMAIL_ALERT_CONFIG config " +
                    "where config.DEL_FLAG = 0 and " +
                    "(:positionId is null or config.POSITION_ID = TO_NUMBER(:positionId)) and " +
                    "(:organizationId is null or config.ORGANIZATION_ID = TO_NUMBER(:organizationId)) and " +
                    "(:employeeId is null or exists(select e.EMAIL_ALERT_RECIPIENT_ID from EMAIL_ALERT_RECIPIENT e " +
                    "where e.DEL_FLAG = 0 and e.EMPLOYEE_ID = TO_NUMBER(:employeeId) and e.EMAIL_ALERT_CONFIG_ID = config.EMAIL_ALERT_CONFIG_ID))"
            , nativeQuery = true)
    Page<EmailAlertConfigDTO.Projection> search(Long positionId, Long organizationId, Long employeeId, Pageable pageable);

    boolean existsByPositionIdAndOrganizationId(Long positionId, Long organizationId);
}
