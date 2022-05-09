package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.dto.hstd.ContractImportDTO;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.ContractImportService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController()
@RequestMapping("contract-import")
@Tag(name = "Contract import")
@Slf4j
public class ContractImportController {

    @Autowired
    private ContractImportService contractImportService;

    @PostMapping("excel")
    public BaseResponse<ContractImportDTO.ContractImportResponse> importExcel(@Parameter(hidden = true) @AuthenticationPrincipal SSoResponse sSoResponse, @Valid @RequestBody ContractImportDTO.ContractImportRequest request) {
        return new BaseResponse.ResponseBuilder<ContractImportDTO.ContractImportResponse>().success(contractImportService.importContractFromExcel(sSoResponse, request));
    }
}
