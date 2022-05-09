package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.*;
import com.viettel.hstd.dto.hstd.ProbationaryContractDTO.*;
import com.viettel.hstd.service.inf.ProbationaryContractService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("probationary-contract")
@Tag(name = "Probationary Contract")
public class ProbationaryContractController {

    @Autowired
    private ProbationaryContractService probationaryContractService;

    @GetMapping
    public BaseResponse<List<ProbationaryContractResponse>> findAll() {
        return SearchUtils.getResponseFromPage(probationaryContractService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<ProbationaryContractResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(probationaryContractService.findPage(searchDTO));
    }

    @PostMapping
    public BaseResponse<ProbationaryContractResponse> create(@Valid @RequestBody ProbationaryContractRequest request) {
        return new BaseResponse
                .ResponseBuilder<ProbationaryContractResponse>()
                .success(probationaryContractService.create(request));
    }

    @GetMapping("/{id}")
    public BaseResponse<ProbationaryContractResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<ProbationaryContractResponse>()
                .success(probationaryContractService.findOneById(id));
    }

    @PutMapping("/{id}")
    public BaseResponse<ProbationaryContractResponse> update(@PathVariable Long id, @Valid @RequestBody ProbationaryContractRequest request) {
        return new BaseResponse
                .ResponseBuilder<ProbationaryContractResponse>()
                .success(probationaryContractService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(probationaryContractService.delete(id));
    }

    @PostMapping("/export-word")
    public BaseResponse<FileDTO.FileResponse> exportWord(@RequestBody ProbationaryExportContractRequest request) {
        return new BaseResponse.ResponseBuilder<FileDTO.FileResponse>()
                .success(probationaryContractService.exportContract(request));
    }

    @PutMapping("/update-new-contract-status")
    public BaseResponse<Boolean> update(@RequestBody ProbationaryContractDTO.ProbationaryNewStatusRequest request) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(probationaryContractService.updateNewContractStatus(request));
    }

    /**
     * Add chữ ký vào file hợp đồng
     *
     * @param request
     * @return
     */
    @PostMapping("/add-employee-signature-to-contract")
    public BaseResponse<FileDTO.FileResponse> addEmployeeSignatureToContract(@RequestBody ContractDTO.ContractAddEmployeeSignature request) {
        return new BaseResponse
                .ResponseBuilder<FileDTO.FileResponse>()
                .success(probationaryContractService.addEmployeeSignatureToContract(request));
    }

    @PostMapping("/send-to-employee-to-sign")
    public BaseResponse<FileDTO.FileResponse> send2Employee2Sign(@RequestBody ContractDTO.ContractExportRequest request) {
        return new BaseResponse.ResponseBuilder<FileDTO.FileResponse>()
                .success(probationaryContractService.sendContractToEmployeeInNewContract(request.contractId));
    }
}