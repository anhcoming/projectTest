package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.EmailConfigDTO;
import com.viettel.hstd.service.inf.EmailConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("email-config")
@Tag(name = "Email Config")
public class EmailConfigController {
    @Autowired
    EmailConfigService emailConfigService;

    @GetMapping
    public BaseResponse<List<EmailConfigDTO.EmailConfigResponse>> findAll() {
        return SearchUtils.getResponseFromPage(emailConfigService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<EmailConfigDTO.EmailConfigResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(emailConfigService.findPage(searchDTO));
    }

    @PostMapping
    public BaseResponse<EmailConfigDTO.EmailConfigResponse> create(@Valid @RequestBody EmailConfigDTO.EmailConfigRequest request) {
        return new BaseResponse
                .ResponseBuilder<EmailConfigDTO.EmailConfigResponse>()
                .success(emailConfigService.create(request));
    }

    @GetMapping("/{id}")
    public BaseResponse<EmailConfigDTO.EmailConfigResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<EmailConfigDTO.EmailConfigResponse>()
                .success(emailConfigService.findOneById(id));
    }

    @PutMapping("/{id}")
    public BaseResponse<EmailConfigDTO.EmailConfigResponse> update(@PathVariable Long id, @Valid @RequestBody EmailConfigDTO.EmailConfigRequest request) {
        return new BaseResponse
                .ResponseBuilder<EmailConfigDTO.EmailConfigResponse>()
                .success(emailConfigService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(emailConfigService.delete(id));
    }
}
