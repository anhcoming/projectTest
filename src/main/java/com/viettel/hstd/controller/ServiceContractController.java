package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ServiceContractDTO.*;
import com.viettel.hstd.service.inf.ServiceContractService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Hop dong dich vu
 */
@RestController()
@RequestMapping("service-contract")
@Tag(name = "Service Contract")
public class ServiceContractController {

    @Autowired
    private ServiceContractService serviceContractService;

    @PostMapping("search")
    public BaseResponse<List<ServiceContractResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(serviceContractService.findPage(searchDTO));
    }

    @GetMapping("/{id}")
    public BaseResponse<ServiceContractResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<ServiceContractResponse>()
                .success(serviceContractService.findOneById(id));
    }

    @PostMapping("/export-word")
    public BaseResponse<FileDTO.FileResponse> exportWord(@RequestBody ServiceContractRequest input) {
        return new BaseResponse.ResponseBuilder<FileDTO.FileResponse>()
                .success(serviceContractService.exportContract(input.contractId));
    }
}
