package com.viettel.hstd.service.inf;

import com.viettel.hstd.dto.hstd.PositionCategoryDTO;

public interface PositionCategoryService {
    PositionCategoryDTO.Response findByPositionCode(String positionCode);
}
