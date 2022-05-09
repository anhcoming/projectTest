package com.viettel.hstd.service.inf;

import com.viettel.hstd.dto.hstd.ImportHistoryDetailDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ImportHistoryDetailService {

    void saveBatch(List<ImportHistoryDetailDTO.Request> requests);

    Page<String> findRowContent(ImportHistoryDetailDTO.SearchCriteria searchCriteria);
}
