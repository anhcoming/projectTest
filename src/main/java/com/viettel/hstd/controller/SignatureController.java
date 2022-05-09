package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.SignatureDTO.*;
import com.viettel.hstd.service.inf.SignatureService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("signature")
@Tag(name = "Signature")
public class SignatureController {
    @Autowired
    SignatureService signatureService;

    @GetMapping
    public BaseResponse<List<SignatureResponse>> findAll() {
        return SearchUtils.getResponseFromPage(signatureService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<SignatureResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(signatureService.findPage(searchDTO));
    }

    @PostMapping
    public BaseResponse<SignatureResponse> create(@Valid @RequestBody SignatureRequest request) {
        return new BaseResponse
                .ResponseBuilder<SignatureResponse>()
                .success(signatureService.create(request));
    }

    @GetMapping("/{id}")
    public BaseResponse<SignatureResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<SignatureResponse>()
                .success(signatureService.findOneById(id));
    }

    @PutMapping("/{id}")
    public BaseResponse<SignatureResponse> update(@PathVariable Long id, @Valid @RequestBody SignatureRequest request) {
        return new BaseResponse
                .ResponseBuilder<SignatureResponse>()
                .success(signatureService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(signatureService.delete(id));
    }

}