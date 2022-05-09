package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.dto.hstd.ImportHistorySettingDTO;
import com.viettel.hstd.service.inf.ImportHistorySettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImportHistorySettingController {

    private final ImportHistorySettingService importHistorySettingService;

    @PostMapping("/v1/import-history-settings/search")
    public BaseResponse<List<ImportHistorySettingDTO.Response>> search(@RequestBody ImportHistorySettingDTO.SearchCriteria searchCriteria) {
        return new BaseResponse
                .ResponseBuilder<List<ImportHistorySettingDTO.Response>>()
                .success(importHistorySettingService.search(searchCriteria));

    }

}
