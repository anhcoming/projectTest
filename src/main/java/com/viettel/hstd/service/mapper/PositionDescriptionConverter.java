package com.viettel.hstd.service.mapper;

import com.viettel.hstd.dto.hstd.PositionDescriptionDTO;
import com.viettel.hstd.entity.hstd.PositionDescriptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface PositionDescriptionConverter {

    @Mapping(target = "hasDescription", expression = "java(convertHasDescription(row.getHasDescription()))")
    @Mapping(target = "experienceRequirement", expression = "java(Double.valueOf(row.getExperienceRequirement()))")
    PositionDescriptionDTO.Request excelRowToRequest(PositionDescriptionDTO.PositionDescriptionExcelRow row);

    PositionDescriptionEntity requestToEntity(PositionDescriptionDTO.Request request);

    List<PositionDescriptionEntity> requestsToEntities(List<PositionDescriptionDTO.Request> requests);

    PositionDescriptionDTO.Response entityToResponse(PositionDescriptionEntity entity);

    default Boolean convertHasDescription(String hasDescriptionStr) {
        return Objects.nonNull(hasDescriptionStr) && hasDescriptionStr.trim().equals("x");
    }

    PositionDescriptionDTO.SingleResponse entityToSingleResponse(PositionDescriptionEntity entity);
}
