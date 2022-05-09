package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.ImportHistoryDetailDTO;
import com.viettel.hstd.service.inf.ImportHistoryDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImportHistoryDetailController {

    private final ImportHistoryDetailService importHistoryDetailService;

    @PostMapping("/v1/import-history-details/rows")
    public BaseResponse<List<String>> findRowByHistoryId(@RequestBody ImportHistoryDetailDTO.SearchCriteria searchCriteria) {
        return SearchUtils.getResponseFromPage(importHistoryDetailService.findRowContent(searchCriteria));
    }

}
