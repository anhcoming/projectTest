package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.DocumentRetirementDTO.*;
import com.viettel.hstd.service.inf.DocumentRetirementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("document-retirement")
@Tag(name = "Document Retirement")
public class DocumentRetirementController {

    @Autowired
    DocumentRetirementService documentRetirementService;

    @GetMapping
    public BaseResponse<List<DocumentRetirementResponse>> findAll() {
        return SearchUtils.getResponseFromPage(documentRetirementService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<DocumentRetirementResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(documentRetirementService.findPage(searchDTO));
    }

    @PostMapping
    public BaseResponse<DocumentRetirementResponse> create(@Valid @RequestBody DocumentRetirementRequest request) {
        DocumentRetirementResponse result = documentRetirementService.create(request);
        if (result == null) {
            return new BaseResponse
                    .ResponseBuilder<DocumentRetirementResponse>()
                    .failed(null, "Mã hồ sơ nghỉ việc đã tồn tại");
        }

        return new BaseResponse
                .ResponseBuilder<DocumentRetirementResponse>()
                .success(result);
    }

    @GetMapping("/{id}")
    public BaseResponse<DocumentRetirementResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<DocumentRetirementResponse>()
                .success(documentRetirementService.findOneById(id));
    }

    @PutMapping("/{id}")
    public BaseResponse<DocumentRetirementResponse> update(@PathVariable Long id, @Valid @RequestBody DocumentRetirementRequest request) {
        DocumentRetirementResponse result = documentRetirementService.update(id, request);
        if (result == null) {
            return new BaseResponse
                    .ResponseBuilder<DocumentRetirementResponse>()
                    .failed(null, "Mã hồ sơ nghỉ việc đã tồn tại");
        }
        return new BaseResponse
                .ResponseBuilder<DocumentRetirementResponse>()
                .success(result);
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(documentRetirementService.delete(id));
    }

    @PostMapping("is-existed")
    public BaseResponse<Boolean> isExisted(@RequestBody DocumentRetirementRequest request) {
        if (request == null) {
            request = new DocumentRetirementRequest();
        }
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(documentRetirementService.isExisted(request.code, request.documentTypeId));
    }
}
