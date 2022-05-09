package com.viettel.hstd.service.inf;

import com.viettel.hstd.dto.hstd.TrainingSessionDTO;
import org.springframework.data.domain.Page;

public interface TrainingSessionService {
    void save(TrainingSessionDTO.Request request);

    Page<TrainingSessionDTO.Response> search(TrainingSessionDTO.SearchCriteria searchCriteria);

    void delete(Long id);
}
