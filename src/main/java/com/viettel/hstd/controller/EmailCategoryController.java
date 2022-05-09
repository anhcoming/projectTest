package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.EmailCategoryDTO;
import com.viettel.hstd.service.inf.EmailCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("email-category")
@Tag(name = "Email Category")
public class EmailCategoryController {
    @Autowired
    EmailCategoryService emailCategoryService;

    @GetMapping
    public BaseResponse<List<EmailCategoryDTO.EmailCategoryResponse>> findAll() {
        return SearchUtils.getResponseFromPage(emailCategoryService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<EmailCategoryDTO.EmailCategoryResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(emailCategoryService.findPage(searchDTO));
    }

    @PostMapping
    public BaseResponse<EmailCategoryDTO.EmailCategoryResponse> create(@Valid @RequestBody EmailCategoryDTO.EmailCategoryRequest request) {
        return new BaseResponse
                .ResponseBuilder<EmailCategoryDTO.EmailCategoryResponse>()
                .success(emailCategoryService.create(request));
    }

    @GetMapping("/{id}")
    public BaseResponse<EmailCategoryDTO.EmailCategoryResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<EmailCategoryDTO.EmailCategoryResponse>()
                .success(emailCategoryService.findOneById(id));
    }

    @PutMapping("/{id}")
    public BaseResponse<EmailCategoryDTO.EmailCategoryResponse> update(@PathVariable Long id, @Valid @RequestBody EmailCategoryDTO.EmailCategoryRequest request) {
        return new BaseResponse
                .ResponseBuilder<EmailCategoryDTO.EmailCategoryResponse>()
                .success(emailCategoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(emailCategoryService.delete(id));
    }
}
