package com.viettel.hstd.service.mapper;

import com.viettel.hstd.dto.hstd.TrainingSessionDTO;
import com.viettel.hstd.entity.hstd.TrainingSessionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingSessionConverter {

    TrainingSessionEntity requestToEntity(TrainingSessionDTO.Request request);

}
