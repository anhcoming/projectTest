package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.CvDTO.*;
import com.viettel.hstd.dto.hstd.CvDTO;
import com.viettel.hstd.service.inf.CvService;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.service.inf.CvService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("cv")
@Tag(name = "CV")
public class CVController {

    @Autowired
    CvService cvService;

    @GetMapping
    public BaseResponse<List<CvDTO.CvResponse>> findAll() {
        return SearchUtils.getResponseFromPage(cvService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<CvDTO.CvResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(cvService.findPage(searchDTO));
    }

    @PostMapping
    public BaseResponse<CvDTO.CvResponse> create(@Valid @RequestBody CvDTO.CvRequest request) {
        return new BaseResponse
                .ResponseBuilder<CvDTO.CvResponse>()
                .success(cvService.create(request));
    }

    @PostMapping("import-excel")
    public BaseResponse<List<CvDTO.CVExcelResponse>> importExcel(@Valid @RequestBody List<CvDTO.CvRequest> request) {
        return new BaseResponse
                .ResponseBuilder<List<CvDTO.CVExcelResponse>>()
                .success(cvService.importExcel(request));
    }

    @GetMapping("/{id}")
    public BaseResponse<CvDTO.CvResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<CvDTO.CvResponse>()
                .success(cvService.findOneById(id));
    }

    @PutMapping("/{id}")
    public BaseResponse<CvDTO.CvResponse> update(@PathVariable Long id, @Valid @RequestBody CvDTO.CvRequest request) {
        return new BaseResponse
                .ResponseBuilder<CvDTO.CvResponse>()
                .success(cvService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(cvService.delete(id));
    }


//    @PostMapping("import-excel")
//    public BaseResponse<FileDTO.FileResponse> importExcel(@RequestParam(value = "file") MultipartFile file) {
//        BaseResponse
//                .ResponseBuilder<FileDTO.FileResponse> response = new BaseResponse
//                .ResponseBuilder<>();
//        return response.success(cvService.importExcel(file));
//    }
}

