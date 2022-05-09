package com.viettel.hstd.service.imp;

import com.viettel.hstd.dto.hstd.ImportHistoryDetailDTO;
import com.viettel.hstd.repository.hstd.ImportHistoryDetailRepository;
import com.viettel.hstd.service.inf.ImportHistoryDetailService;
import com.viettel.hstd.service.mapper.ImportHistoryDetailConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportHistoryDetailServiceImpl implements ImportHistoryDetailService {

    private final ImportHistoryDetailRepository importHistoryDetailRepository;

    private final ImportHistoryDetailConverter importHistoryDetailConverter;

    @Override
    public void saveBatch(List<ImportHistoryDetailDTO.Request> requests) {
        importHistoryDetailRepository.saveAll(importHistoryDetailConverter.requestsToEntities(requests));
    }

    @Override
    public Page<String> findRowContent(ImportHistoryDetailDTO.SearchCriteria searchCriteria) {
        return importHistoryDetailRepository.search(searchCriteria.getImportHistoryId(),
                PageRequest.of(searchCriteria.getPage(), searchCriteria.getSize()));
    }

}
