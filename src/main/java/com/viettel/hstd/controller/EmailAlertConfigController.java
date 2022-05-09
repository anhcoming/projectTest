package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.EmailAlertConfigDTO;
import com.viettel.hstd.service.inf.EmailAlertConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class EmailAlertConfigController {

    private final EmailAlertConfigService emailAlertConfigService;


    @PostMapping("/v1/email-alert-configs")
    public BaseResponse<Boolean> create(@Valid @RequestBody EmailAlertConfigDTO.Request request) {
        emailAlertConfigService.save(request);
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(true);
    }

    @PostMapping("/v1/email-alert-configs/search")
    public BaseResponse<List<EmailAlertConfigDTO.Response>> search(@Valid @RequestBody EmailAlertConfigDTO.SearchCriteria searchCriteria) {
        return SearchUtils.getResponseFromPage(emailAlertConfigService.search(searchCriteria));
    }

    @GetMapping("/v1/email-alert-configs/{id}")
    public BaseResponse<EmailAlertConfigDTO.SingleResponse> findById(@PathVariable @Min(0) Long id) {
        return new BaseResponse
                .ResponseBuilder<EmailAlertConfigDTO.SingleResponse>()
                .success(emailAlertConfigService.findById(id));
    }

    @PostMapping("/v1/email-alert-configs/{id}/delete")
    public BaseResponse<Void> deleteById(@PathVariable @Min(1) Long id){
        emailAlertConfigService.deleteById(id);
        return new BaseResponse
                .ResponseBuilder<Void>()
                .success(null);

    }

    @PostMapping("/v1/email-alert-configs/{id}/update")
    public BaseResponse<Boolean> update(@PathVariable @Min(1) Long id, @RequestBody @Valid EmailAlertConfigDTO.Request request){
        emailAlertConfigService.update(id, request);
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(true);

    }

}
