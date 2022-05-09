package com.viettel.hstd.service.mapper;

import com.viettel.hstd.dto.hstd.ImportHistoryDTO;
import com.viettel.hstd.entity.hstd.ImportHistoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImportHistoryConverter {
    ImportHistoryEntity requestToEntity(ImportHistoryDTO.Request request);
}
