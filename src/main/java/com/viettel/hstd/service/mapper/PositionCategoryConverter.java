package com.viettel.hstd.service.mapper;

import com.viettel.hstd.dto.hstd.PositionCategoryDTO;
import com.viettel.hstd.entity.hstd.PositionCategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PositionCategoryConverter {

    PositionCategoryDTO.Response entityToResponse(PositionCategoryEntity entity);
}
