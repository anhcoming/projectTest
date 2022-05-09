package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ResignSessionContractDTO.*;
import com.viettel.hstd.service.inf.EmployeeMonthlyReviewService;
import com.viettel.hstd.service.inf.ResignSessionContractService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("resign-session-contract")
@Tag(name = "Resign Session Contract + BM")
public class ResignSessionContractController {
    @Autowired
    ResignSessionContractService resignSessionContractService;

    @Autowired
    EmployeeMonthlyReviewService employeeMonthlyReviewService;

    @GetMapping
    public BaseResponse<List<ResignSessionContractResponse>> findAll() {
        return SearchUtils.getResponseFromPage(resignSessionContractService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<ResignSessionContractResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(resignSessionContractService.findPage(searchDTO));
    }

    @PostMapping
    public BaseResponse<ResignSessionContractResponse> create(@Valid @RequestBody ResignSessionContractRequest request) {
        return new BaseResponse
                .ResponseBuilder<ResignSessionContractResponse>()
                .success(resignSessionContractService.create(request));
    }

    @GetMapping("/{id}")
    public BaseResponse<ResignSessionContractResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<ResignSessionContractResponse>()
                .success(resignSessionContractService.findOneById(id));
    }

    @PutMapping("/{id}")
    public BaseResponse<ResignSessionContractResponse> update(@PathVariable Long id, @Valid @RequestBody ResignSessionContractRequest request) {
        return new BaseResponse
                .ResponseBuilder<ResignSessionContractResponse>()
                .success(resignSessionContractService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(resignSessionContractService.delete(id));
    }

    @PostMapping("/yearly-review/search")
    public BaseResponse<List<ResignBm07Response>> searchYearlyReview(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(resignSessionContractService.findPageBm07(searchDTO));
    }

    @PostMapping("/interview/search")
    public BaseResponse<List<ResignBm08Response>> searchInterview(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(resignSessionContractService.findPageBm08(searchDTO));
    }

    @PostMapping("/final-form/search")
    public BaseResponse<List<ExportBM09Response>> searchFinalForm(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(resignSessionContractService.findBM09Page(searchDTO));
    }

    @PostMapping("/bm03/search")
    public BaseResponse<List<ExportBM03Response>> searchBM03(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(resignSessionContractService.findBM03Page(searchDTO));
    }

    @PostMapping("/export-bm09")
    public BaseResponse<FileDTO.FileResponse> exportBM9(@RequestBody ExportBMRequest request) {
        return new BaseResponse
                .ResponseBuilder<FileDTO.FileResponse>()
                .success(resignSessionContractService.exportBm9(request));
    }

    @PostMapping("/export-word")
    public BaseResponse<FileDTO.FileResponse> exportBM9_old(@RequestBody ExportBMRequest request) {
        return new BaseResponse
                .ResponseBuilder<FileDTO.FileResponse>()
                .success(resignSessionContractService.exportBm9(request));
    }

    @PostMapping("/export-bm03")
    public BaseResponse<FileDTO.FileResponse> exportBM3(@RequestBody ExportBMRequest request) {
        return new BaseResponse
                .ResponseBuilder<FileDTO.FileResponse>()
                .success(resignSessionContractService.exportBm3(request));
    }
}