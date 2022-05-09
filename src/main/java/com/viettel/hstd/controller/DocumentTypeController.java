package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.DocumentTypeDTO;
import com.viettel.hstd.service.inf.DocumentTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("document-type")
@Tag(name = "Document Type")
public class DocumentTypeController {
    @Autowired
    DocumentTypeService documentTypeService;

    @GetMapping
    public BaseResponse<List<DocumentTypeDTO.DocumentTypeResponse>> findAll() {
        return SearchUtils.getResponseFromPage(documentTypeService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<DocumentTypeDTO.DocumentTypeResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(documentTypeService.findPage(searchDTO));
    }

    @PostMapping
    public BaseResponse<DocumentTypeDTO.DocumentTypeResponse> create(@Valid @RequestBody DocumentTypeDTO.DocumentTypeRequest request) {
        DocumentTypeDTO.DocumentTypeResponse result = documentTypeService.create(request);
        if(result == null){
            return new BaseResponse
                    .ResponseBuilder<DocumentTypeDTO.DocumentTypeResponse>()
                    .failed(null, "Mã loại hồ sơ đã tồn tại");
        }

        return new BaseResponse
                .ResponseBuilder<DocumentTypeDTO.DocumentTypeResponse>()
                .success(result);
    }

    @GetMapping("/{id}")
    public BaseResponse<DocumentTypeDTO.DocumentTypeResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<DocumentTypeDTO.DocumentTypeResponse>()
                .success(documentTypeService.findOneById(id));
    }

    @PutMapping("/{id}")
    public BaseResponse<DocumentTypeDTO.DocumentTypeResponse> update(@PathVariable Long id, @Valid @RequestBody DocumentTypeDTO.DocumentTypeRequest request) {
        DocumentTypeDTO.DocumentTypeResponse result = documentTypeService.update(id, request);
        if(result == null){
            return new BaseResponse
                    .ResponseBuilder<DocumentTypeDTO.DocumentTypeResponse>()
                    .failed(null, "Mã loại hồ sơ đã tồn tại");
        }
        return new BaseResponse
                .ResponseBuilder<DocumentTypeDTO.DocumentTypeResponse>()
                .success(result);
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(documentTypeService.delete(id));
    }

    @PostMapping("is-existed")
    public BaseResponse<Boolean> isExisted(@RequestBody DocumentTypeDTO.DocumentTypeRequest request) {
        if (request == null) {
            request = new DocumentTypeDTO.DocumentTypeRequest();
        }
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(documentTypeService.isExisted(request.type, request.code, request.documentTypeId));
    }


}
