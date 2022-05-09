package com.viettel.hstd.service.mapper;

import com.viettel.hstd.dto.hstd.EmailAlertConfigDTO;
import com.viettel.hstd.dto.hstd.RecruitmentProgressDTO;
import com.viettel.hstd.entity.hstd.EmailAlertConfigEntity;
import com.viettel.hstd.entity.hstd.RecruitmentProgressEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmailAlertConfigConverter {
    EmailAlertConfigEntity requestToEntity(EmailAlertConfigDTO.Request request);
    EmailAlertConfigDTO.Response projectionToResponse(EmailAlertConfigDTO.Projection projections);

    @Mapping(source = "emailAlertConfigEntity.id", target = "emailAlertConfigId")
    EmailAlertConfigDTO.SingleResponse entityToSingleResponse(EmailAlertConfigEntity emailAlertConfigEntity);

}
