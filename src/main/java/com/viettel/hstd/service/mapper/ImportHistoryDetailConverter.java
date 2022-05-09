package com.viettel.hstd.service.mapper;

import com.viettel.hstd.dto.hstd.ImportHistoryDetailDTO;
import com.viettel.hstd.entity.hstd.ImportHistoryDetailEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ImportHistoryDetailConverter {

    List<ImportHistoryDetailEntity> requestsToEntities(List<ImportHistoryDetailDTO.Request> requests);
}
