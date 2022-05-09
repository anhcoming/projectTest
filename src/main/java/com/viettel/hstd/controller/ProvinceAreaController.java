package com.viettel.hstd.controller;

import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.dto.hstd.ProvinceAreaDTO.*;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.ProvinceAreaService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("province-area")
@Tag(name = "Province Area")
public class ProvinceAreaController {
    @Autowired
    ProvinceAreaService provinceAreaService;

    @GetMapping
    public BaseResponse<List<ProvinceAreaResponse>> findAll() {
        return SearchUtils.getResponseFromPage(provinceAreaService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<ProvinceAreaResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(provinceAreaService.findPage(searchDTO));
    }

    @PostMapping
    public BaseResponse<ProvinceAreaResponse> create(@Valid @RequestBody ProvinceAreaRequest request) {
        return new BaseResponse
                .ResponseBuilder<ProvinceAreaResponse>()
                .success(provinceAreaService.create(request));
    }

    @GetMapping("/{id}")
    public BaseResponse<ProvinceAreaResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<ProvinceAreaResponse>()
                .success(provinceAreaService.findOneById(id));
    }

    @PutMapping("/{id}")
    public BaseResponse<ProvinceAreaResponse> update(@PathVariable Long id, @Valid @RequestBody ProvinceAreaRequest request) {
        return new BaseResponse
                .ResponseBuilder<ProvinceAreaResponse>()
                .success(provinceAreaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(provinceAreaService.delete(id));
    }

    @GetMapping("/tree")
    public BaseResponse<?> getTree() {
        return new BaseResponse.ResponseBuilder<>()
                .success(provinceAreaService.getTree());
    }

    @GetMapping("/treeshort")
    public BaseResponse<?> getTreeShort(@Parameter(hidden = true) @AuthenticationPrincipal SSoResponse sSoResponse) {
        return new BaseResponse.ResponseBuilder<>()
                .success(provinceAreaService.getTreeShort(sSoResponse));
    }

    @GetMapping("/children/{id}")
    public BaseResponse<?> getChildren(@PathVariable Long id) {
        return new BaseResponse.ResponseBuilder<>()
                .success(provinceAreaService.getChildren(id));
    }
}
