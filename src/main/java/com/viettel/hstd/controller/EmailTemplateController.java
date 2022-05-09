package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.EmailTemplateDTO.*;
import com.viettel.hstd.service.inf.EmailTemplateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("email-template")
@Tag(name = "Email Template")
public class EmailTemplateController {

    @Autowired
    private EmailTemplateService emailTemplateService;

    @GetMapping
    public BaseResponse<List<EmailTemplateResponse>> findAll() {
        return SearchUtils.getResponseFromPage(emailTemplateService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<EmailTemplateResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(emailTemplateService.findPage(searchDTO));
    }

    @PostMapping
    public BaseResponse<EmailTemplateResponse> create(@Valid @RequestBody EmailTemplateRequest request) {
        return new BaseResponse
                .ResponseBuilder<EmailTemplateResponse>()
                .success(emailTemplateService.create(request));
    }

    @GetMapping("/{id}")
    public BaseResponse<EmailTemplateResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<EmailTemplateResponse>()
                .success(emailTemplateService.findOneById(id));
    }

    @PutMapping("/{id}")
    public BaseResponse<EmailTemplateResponse> update(@PathVariable Long id, @Valid @RequestBody EmailTemplateRequest request) {
        return new BaseResponse
                .ResponseBuilder<EmailTemplateResponse>()
                .success(emailTemplateService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(emailTemplateService.delete(id));
    }

    /**
     * Lay danh sach mau email theo loai
     *
     * @param type loai email template
     * @return
     */
    @GetMapping("find-by-type/{type}")
    public BaseResponse<List<EmailTemplateResponse>> findByType(@PathVariable Integer type) {
        return new BaseResponse
                .ResponseBuilder<List<EmailTemplateResponse>>()
                .success(emailTemplateService.findByType(type));
    }
}
