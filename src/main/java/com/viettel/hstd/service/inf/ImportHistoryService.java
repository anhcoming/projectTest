package com.viettel.hstd.service.inf;

import com.viettel.hstd.constant.ImportConstant;
import com.viettel.hstd.dto.hstd.ImportHistoryDTO;
import org.springframework.data.domain.Page;

public interface ImportHistoryService {
    void save(ImportHistoryDTO.Request request);

    ImportConstant.ImportStatus checkStatus(Long id);

    Page<ImportHistoryDTO.Response> search(ImportHistoryDTO.SearchCriteria searchCriteria);
}
