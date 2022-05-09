package com.viettel.hstd.controller;

import com.viettel.hstd.constant.ImportConstant;
import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.ImportHistoryDTO;
import com.viettel.hstd.dto.hstd.PositionDescriptionDTO;
import com.viettel.hstd.service.inf.ImportHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImportHistoryController {

    private final ImportHistoryService importHistoryService;


    @GetMapping("/v1/import-histories/check-status/{id}")
    public BaseResponse<ImportConstant.ImportStatus> checkStatusById(@PathVariable("id") Long id) {
        return new BaseResponse
                .ResponseBuilder<ImportConstant.ImportStatus>()
                .success(importHistoryService.checkStatus(id));
    }

    @PostMapping("/v1/import-histories/search")
    public BaseResponse<List<ImportHistoryDTO.Response>> search(@RequestBody ImportHistoryDTO.SearchCriteria criteria) {
        return SearchUtils.getResponseFromPage(importHistoryService.search(criteria));
    }


}
