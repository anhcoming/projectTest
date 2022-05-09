package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.RecruitmentProgressDetailDTO;
import com.viettel.hstd.service.inf.RecruitmentProgressDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class RecruitmentProgressDetailController {

    private final RecruitmentProgressDetailService recruitmentProgressDetailService;

    @PostMapping("/v1/progress-details/search-by-progress")
    public BaseResponse<List<RecruitmentProgressDetailDTO.Response>> searchByProgressId(@Valid @RequestBody RecruitmentProgressDetailDTO.SearchCriteria searchCriteria){
        return SearchUtils.getResponseFromPage(recruitmentProgressDetailService.searchByProgressId(searchCriteria));
    }

    @PostMapping("/v1/progress-details/save-batch")
    public BaseResponse<Boolean> saveBatch(@Valid @RequestBody List<RecruitmentProgressDetailDTO.Request> requests){
        recruitmentProgressDetailService.saveBatch(requests);
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(true);
    }
}
