package com.viettel.hstd.controller;


import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.FreelanceContractDTO.*;
import com.viettel.hstd.service.inf.FreelanceContractService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Hop dong giao khoan
 */
@RestController()
@RequestMapping("freelance-contract")
@Tag(name = "Freelance Contract")
public class FreelanceContractController {


    @Autowired
    private FreelanceContractService freelanceContractService;

    @PostMapping("search")
    public BaseResponse<List<FreelanceContractResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(freelanceContractService.findPage(searchDTO));
    }

    @GetMapping("/{id}")
    public BaseResponse<FreelanceContractResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<FreelanceContractResponse>()
                .success(freelanceContractService.findOneById(id));
    }

    @PostMapping("/export-word")
    public BaseResponse<FileDTO.FileResponse> exportWord(@RequestBody FreelanceContractRequest input) {
        return new BaseResponse.ResponseBuilder<FileDTO.FileResponse>()
                .success(freelanceContractService.exportContract(input.contractId));
    }
}
