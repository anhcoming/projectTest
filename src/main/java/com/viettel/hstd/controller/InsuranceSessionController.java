package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.InsuranceSessionDTO.*;
import com.viettel.hstd.dto.hstd.ResignSessionDTO;
import com.viettel.hstd.service.inf.InsuranceSessionService;
import com.viettel.hstd.service.inf.ResignSessionContractService;
import com.viettel.hstd.service.inf.ResignSessionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("insurance-session")
@Tag(name = "Insurance Session")
public class InsuranceSessionController {
    @Autowired
    InsuranceSessionService insuranceSessionService;

    @GetMapping
    public BaseResponse<List<InsuranceSessionResponse>> findAll() {
        return SearchUtils.getResponseFromPage(insuranceSessionService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<InsuranceSessionResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(insuranceSessionService.findPage(searchDTO));
    }

    @PostMapping
    public BaseResponse<InsuranceSessionResponse> create(@Valid @RequestBody InsuranceSessionRequest request) {
        return new BaseResponse
                .ResponseBuilder<InsuranceSessionResponse>()
                .success(insuranceSessionService.create(request));
    }

    @GetMapping("/{id}")
    public BaseResponse<InsuranceSessionResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<InsuranceSessionResponse>()
                .success(insuranceSessionService.findOneById(id));
    }
}
