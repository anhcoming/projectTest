package com.viettel.hstd.service.imp;

import com.viettel.hstd.dto.hstd.TrainingSessionDTO;
import com.viettel.hstd.entity.hstd.TrainingSessionEntity;
import com.viettel.hstd.repository.hstd.TrainingSessionRepository;
import com.viettel.hstd.service.inf.TrainingSessionService;
import com.viettel.hstd.service.mapper.TrainingSessionConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TrainingSessionServiceImpl implements TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;

    private final TrainingSessionConverter trainingSessionConverter;

    @Override
    @Transactional
    public void save(TrainingSessionDTO.Request request) {
        TrainingSessionEntity entity = trainingSessionConverter.requestToEntity(request);
        trainingSessionRepository.save(entity);
    }

    @Override
    public Page<TrainingSessionDTO.Response> search(TrainingSessionDTO.SearchCriteria searchCriteria) {
        return trainingSessionRepository.search(searchCriteria.getName(), searchCriteria.getFromStartDate(), searchCriteria.getToStartDate(),
                searchCriteria.getFromFinishDate(), searchCriteria.getToFinishDate(),
                PageRequest.of(searchCriteria.getPage(), searchCriteria.getSize(), Sort.by(Sort.Order.desc("fromDate"))));
    }

    @Override
    public void delete(Long id) {
        trainingSessionRepository.deleteById(id);
    }
}
