package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.EmailCategoryDTO;
import com.viettel.hstd.dto.hstd.SysConfigDTO.*;
import com.viettel.hstd.service.inf.SysConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("sys-config")
@Tag(name = "System Config")
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    @GetMapping
    public BaseResponse<List<SysConfigResponse>> findAll() {
        return SearchUtils.getResponseFromPage(sysConfigService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<SysConfigResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(sysConfigService.findPage(searchDTO));
    }

    @PostMapping
    public BaseResponse<SysConfigResponse> create(@Valid @RequestBody SysConfigRequest request) {
        return new BaseResponse
                .ResponseBuilder<SysConfigResponse>()
                .success(sysConfigService.create(request));
    }

    @GetMapping("/{id}")
    public BaseResponse<SysConfigResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<SysConfigResponse>()
                .success(sysConfigService.findOneById(id));
    }

    @PutMapping("/{id}")
    public BaseResponse<SysConfigResponse> update(@PathVariable Long id, @Valid @RequestBody SysConfigRequest request) {
        return new BaseResponse
                .ResponseBuilder<SysConfigResponse>()
                .success(sysConfigService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(sysConfigService.delete(id));
    }
}
