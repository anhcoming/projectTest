package com.viettel.hstd.service.mapper;

import com.viettel.hstd.dto.hstd.RecruitmentProgressEmployeeDTO;
import com.viettel.hstd.entity.hstd.RecruitmentProgressEmployeeEntity;
import com.viettel.hstd.entity.vps.EmployeeVhrEntity;
import com.viettel.hstd.entity.vps.EmployeeVhrEntityFull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecruitmentProgressEmployeeConverter {
    RecruitmentProgressEmployeeEntity requestToEntity(RecruitmentProgressEmployeeDTO.Request request, Long recruitmentProgressId);

    @Mapping(target = "employeeEmail", source = "email")
    @Mapping(target = "employeeName", source = "fullname")
    RecruitmentProgressEmployeeDTO.Request vhrEntityToRequest(EmployeeVhrEntityFull employeeVhrEntity);

}
