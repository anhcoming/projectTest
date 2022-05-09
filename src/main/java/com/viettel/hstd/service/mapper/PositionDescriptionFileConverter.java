package com.viettel.hstd.service.mapper;

import com.viettel.hstd.dto.hstd.PositionDescriptionFileDTO;
import com.viettel.hstd.entity.hstd.PositionDescriptionFileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PositionDescriptionFileConverter {

    @Mapping(target = "positionDescriptionId", source = "positionDescriptionId")
    PositionDescriptionFileEntity requestToEntity(PositionDescriptionFileDTO.Request request, Long positionDescriptionId);
}
