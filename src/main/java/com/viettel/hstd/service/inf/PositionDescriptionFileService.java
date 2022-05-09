package com.viettel.hstd.service.inf;

import com.viettel.hstd.dto.hstd.PositionDescriptionFileDTO;

import java.util.List;

public interface PositionDescriptionFileService {
    List<PositionDescriptionFileDTO.Response> getListFileByPositionDescriptionId(Long positionDescriptionId);
}
