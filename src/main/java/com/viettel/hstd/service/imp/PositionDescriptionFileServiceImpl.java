package com.viettel.hstd.service.imp;

import com.viettel.hstd.dto.hstd.PositionDescriptionFileDTO;
import com.viettel.hstd.repository.hstd.PositionDescriptionFileRepository;
import com.viettel.hstd.service.inf.PositionDescriptionFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionDescriptionFileServiceImpl implements PositionDescriptionFileService {

    private final PositionDescriptionFileRepository positionDescriptionFileRepository;

    @Override
    public List<PositionDescriptionFileDTO.Response> getListFileByPositionDescriptionId(Long positionDescriptionId) {
        return positionDescriptionFileRepository.getListFileByPositionDescriptionId(positionDescriptionId);
    }
}
