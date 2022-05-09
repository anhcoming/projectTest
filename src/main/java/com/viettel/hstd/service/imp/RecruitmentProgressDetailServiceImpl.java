package com.viettel.hstd.service.imp;

import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.dto.hstd.RecruitmentProgressDetailDTO;
import com.viettel.hstd.dto.hstd.RecruitmentProgressDetailFileDTO;
import com.viettel.hstd.entity.hstd.RecruitmentProgressDetailEntity;
import com.viettel.hstd.entity.hstd.RecruitmentProgressDetailFileEntity;
import com.viettel.hstd.entity.hstd.RecruitmentProgressEntity;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.repository.hstd.RecruitmentProgressDetailFileRepository;
import com.viettel.hstd.repository.hstd.RecruitmentProgressDetailRepository;
import com.viettel.hstd.service.inf.RecruitmentProgressDetailService;
import com.viettel.hstd.service.inf.RecruitmentProgressService;
import com.viettel.hstd.service.mapper.RecruitmentProgressDetailConverter;
import com.viettel.hstd.service.mapper.RecruitmentProgressDetailFileConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentProgressDetailServiceImpl implements RecruitmentProgressDetailService {

    private final RecruitmentProgressDetailRepository recruitmentProgressDetailRepository;

    private final RecruitmentProgressService recruitmentProgressService;

    private final RecruitmentProgressDetailConverter recruitmentProgressDetailConverter;

    private final RecruitmentProgressDetailFileConverter recruitmentProgressDetailFileConverter;

    private final RecruitmentProgressDetailFileRepository recruitmentProgressDetailFileRepository;

    private final Message message;

    @Override
    @Transactional(readOnly = true)
    public Page<RecruitmentProgressDetailDTO.Response> searchByProgressId(RecruitmentProgressDetailDTO.SearchCriteria criteria) {
        if (criteria.isRequestAll()) {
            return recruitmentProgressDetailRepository.searchByProgressId(criteria.getRecruitmentProgressId(), criteria.getRecruitmentDate(),
                    PageRequest.of(0, Integer.MAX_VALUE)).map(recruitmentProgressDetailConverter::responseProjectionToResponse);
        }
        return recruitmentProgressDetailRepository.searchByProgressId(criteria.getRecruitmentProgressId(), criteria.getRecruitmentDate(),
                PageRequest.of(criteria.getPage(), criteria.getSize())).map(recruitmentProgressDetailConverter::responseProjectionToResponse);

    }

    @Override
    @Transactional
    public void save(RecruitmentProgressDetailEntity entity) {
        recruitmentProgressDetailRepository.save(entity);

    }

    @Override
    @Transactional
    public void saveBatch(List<RecruitmentProgressDetailDTO.Request> requests) {
        if (!requests.isEmpty()) {
            List<RecruitmentProgressDetailEntity> entities = recruitmentProgressDetailConverter.toEntities(requests);
            Long progressId = requests.stream().findFirst()
                    .orElseThrow(() -> new BadRequestException(message.getMessage("message.invalid")))
                    .getRecruitmentProgressId();
            RecruitmentProgressEntity recruitmentProgressEntity = recruitmentProgressService.findById(progressId);
            int sumOfRecruited = entities.stream().map(RecruitmentProgressDetailEntity::getRecruited)
                    .reduce(0, Integer::sum);
            recruitmentProgressEntity.setRecruited(recruitmentProgressEntity.getRecruited() + sumOfRecruited);
            recruitmentProgressService.save(recruitmentProgressEntity);

            recruitmentProgressDetailRepository.saveAll(entities);

            //save all file
            int detailEntitiesSize = entities.size();
            List<RecruitmentProgressDetailFileEntity> fileEntities = new ArrayList<>();
            for (int element = 0; element < detailEntitiesSize; element++) {
                List<RecruitmentProgressDetailFileDTO.Request> fileRequest = requests.get(element).getFiles();
                Long detailId = entities.get(element).getId();
                fileEntities.addAll(fileRequest.stream()
                        .map(request -> recruitmentProgressDetailFileConverter.requestToEntity(request, detailId))
                        .collect(Collectors.toList()));
            }
            recruitmentProgressDetailFileRepository.saveAll(fileEntities);
        }

    }

}
