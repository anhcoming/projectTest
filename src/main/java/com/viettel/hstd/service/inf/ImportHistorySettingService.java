package com.viettel.hstd.service.inf;

import com.viettel.hstd.dto.hstd.ImportHistorySettingDTO;

import java.util.List;

public interface ImportHistorySettingService {
    List<ImportHistorySettingDTO.Response> search(ImportHistorySettingDTO.SearchCriteria searchCriteria);
}
