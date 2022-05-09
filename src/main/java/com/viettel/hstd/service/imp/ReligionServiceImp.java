package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
        import com.viettel.hstd.core.config.Message;
        import com.viettel.hstd.core.dto.SearchDTO;
        import com.viettel.hstd.core.utils.MapUtils;
        import com.viettel.hstd.core.utils.SearchUtils;
        import com.viettel.hstd.dto.hstd.ReligionDTO;
        import com.viettel.hstd.entity.hstd.ReligionEntity;
        import com.viettel.hstd.exception.NotFoundException;
        import com.viettel.hstd.repository.hstd.ReligionRepository;
        import com.viettel.hstd.service.inf.ReligionService;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.data.domain.Page;
        import org.springframework.data.domain.PageRequest;
        import org.springframework.data.domain.Pageable;
        import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ReligionServiceImp extends BaseService implements ReligionService {

    @Autowired
    ReligionRepository religionRepository;

    @Autowired
    Message message;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MapUtils mapUtils;

    @Override
    public ReligionDTO.ReligionResponse findOneById(Long id) {
        ReligionEntity emailConfigEntity = religionRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(emailConfigEntity, ReligionDTO.ReligionResponse.class);
    }

    @Override
    public Boolean delete(Long id) {
        if (!religionRepository.existsById(id))
            throw new NotFoundException(message.getMessage("message.not_found"));
        religionRepository.softDelete(id);
        addLog("EMAIL_CONFIG");
        return true;
    }

    @Override
    public Page<ReligionDTO.ReligionResponse> findPage(SearchDTO searchRequest) {
        Pageable pageable = SearchUtils.getPageable(searchRequest);
        Specification<ReligionEntity> specs = SearchUtils.getSpecifications(searchRequest);

        Page<ReligionEntity> list;
        if (searchRequest.pagedFlag) {
            list = religionRepository.findAll(specs, pageable);
        } else {
            Pageable p = PageRequest.of(0, Integer.MAX_VALUE);
            list = religionRepository.findAll(p);
        }

        return list.map(obj ->
                this.objectMapper.convertValue(obj, ReligionDTO.ReligionResponse.class)
        );
    }

    @Override
    public ReligionDTO.ReligionResponse update(Long id, ReligionDTO.ReligionRequest request) {
        ReligionEntity emailConfigEntity = religionRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        ReligionEntity newE = objectMapper.convertValue(request, ReligionEntity.class);
        mapUtils.customMap(newE, emailConfigEntity);
        emailConfigEntity.setReligionId(id);
        emailConfigEntity = religionRepository.save(emailConfigEntity);
        addLog("UPDATE");
        return objectMapper.convertValue(emailConfigEntity, ReligionDTO.ReligionResponse.class);
    }
}