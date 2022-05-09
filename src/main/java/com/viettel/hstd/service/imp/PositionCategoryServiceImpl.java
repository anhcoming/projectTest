package com.viettel.hstd.service.imp;

import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.dto.hstd.PositionCategoryDTO;
import com.viettel.hstd.entity.hstd.PositionCategoryEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.repository.hstd.PositionCategoryRepository;
import com.viettel.hstd.service.inf.PositionCategoryService;
import com.viettel.hstd.service.mapper.PositionCategoryConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PositionCategoryServiceImpl implements PositionCategoryService {

    private final PositionCategoryRepository positionCategoryRepository;

    private final PositionCategoryConverter positionCategoryConverter;

    private final Message message;

    @Override
    @Transactional(readOnly = true)
    public PositionCategoryDTO.Response findByPositionCode(String positionCode) {
        PositionCategoryEntity entity = positionCategoryRepository.findByPositionCode(positionCode).orElse(null);
        return positionCategoryConverter.entityToResponse(entity);
    }
}
