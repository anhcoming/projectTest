package com.viettel.hstd.service.mapper;

import com.viettel.hstd.dto.hstd.EmailAlertConfigDTO;
import com.viettel.hstd.dto.hstd.EmailAlertRecipientDTO;
import com.viettel.hstd.entity.hstd.EmailAlertConfigEntity;
import com.viettel.hstd.entity.hstd.EmailAlertRecipientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmailAlertRecipientConverter {
    List<EmailAlertRecipientEntity> requestToEntities(List<EmailAlertRecipientDTO.Request> request);

    @Mapping(source = "emailAlertConfigId", target = "emailAlertConfigId")
    EmailAlertRecipientEntity requestToEntity(EmailAlertRecipientDTO.Request request, Long emailAlertConfigId);

    List<EmailAlertRecipientDTO.Response> entitiesToResponses(List<EmailAlertRecipientEntity> entities);

    @Mapping(source = "entity.employeeEmail", target = "email")
    EmailAlertRecipientDTO.Response entityToResponse(EmailAlertRecipientEntity entity);


}
