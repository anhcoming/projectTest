package com.viettel.hstd.dto.hstd;

import com.viettel.hstd.core.dto.SearchDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class EmailAlertConfigDTO {

    @Getter
    @Setter
    public static class Request{
        private Long positionId;
        private String positionCode;
        private String positionName;
        private Long organizationId;
        private String organizationCode;
        private String organizationName;
        private Long emailConfigId;
        private Long emailTemplateId;
        private List<EmailAlertRecipientDTO.Request> recipients;
    }

    public interface Projection{
        Long getEmailAlertConfigId();
        Long getPositionId();
        String getPositionCode();
        String getPositionName();
        Long getOrganizationId();
        String getOrganizationCode();
        String getOrganizationName();
        String getEmailRecipients();
    }

    @Getter
    @Setter
    public static class SearchCriteria {
        private Long positionId;
        private Long organizationId;
        private Long employeeId;
        private Integer page = 0;
        private Integer size = 10;
        private List<SearchDTO.OrderDTO> sortList;

    }

    @Getter
    @Setter
    public static class Response{
        private Long emailAlertConfigId;
        private Long positionId;
        private String positionCode;
        private String positionName;
        private Long organizationId;
        private String organizationCode;
        private String organizationName;
        private String emailRecipients;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class SingleResponse{
        private final Long emailAlertConfigId;
        private final Long positionId;
        private final String positionCode;
        private final String positionName;
        private final Long organizationId;
        private final String organizationCode;
        private final String organizationName;
        private final Long emailConfigId;
        private final Long emailTemplateId;
        private List<EmailAlertRecipientDTO.Response> listEmployee;
    }

}
