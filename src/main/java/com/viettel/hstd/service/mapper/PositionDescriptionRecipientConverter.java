package com.viettel.hstd.service.mapper;

import com.viettel.hstd.dto.hstd.PositionDescriptionRecipientDTO;
import com.viettel.hstd.entity.hstd.PositionDescriptionRecipientEntity;
import com.viettel.hstd.entity.vps.EmployeeVhrEntityFull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PositionDescriptionRecipientConverter {

    @Mapping(source = "employeeVhr.fullname", target = "employeeName")
    @Mapping(source = "employeeVhr.email", target = "employeeEmail")
    @Mapping(source = "employeeVhr.mobileNumber", target = "employeeMobilePhone")
    PositionDescriptionRecipientDTO.Request employeeVhrToRequest(EmployeeVhrEntityFull employeeVhr);

    @Mapping(target = "positionDescriptionId", source = "positionDescriptionId")
    PositionDescriptionRecipientEntity requestToEntity(PositionDescriptionRecipientDTO.Request request, Long positionDescriptionId);

}
