package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
        import com.viettel.hstd.core.dto.SearchDTO;
        import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ContractDTO;
import com.viettel.hstd.dto.hstd.ContractDTO.*;
import com.viettel.hstd.dto.hstd.LaborContractDTO.*;
import com.viettel.hstd.service.inf.ContractService;
import com.viettel.hstd.service.inf.LaborContractService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

        import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("labor-contract")
@Tag(name = "Labor Contract")
public class LaborContractController {

    @Autowired
    private LaborContractService laborContractService;

    @Autowired
    private ContractService contractService;

    @GetMapping
//    @PreAuthorize("hasPermission('VIEW CONTRACT_MANAGEMENT', '')")
    public BaseResponse<List<LaborContractResponse>> findAll() {
        return SearchUtils.getResponseFromPage(laborContractService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
//    @PreAuthorize("hasPermission('VIEW CONTRACT_MANAGEMENT', '')")
    public BaseResponse<List<LaborContractResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(laborContractService.findPage(searchDTO));
    }

    @PostMapping
//    @PreAuthorize("hasPermission('INSERT CONTRACT_MANAGEMENT', '')")
    public BaseResponse<LaborContractResponse> create(@Valid @RequestBody LaborContractRequest request) {
        return new BaseResponse
                .ResponseBuilder<LaborContractResponse>()
                .success(laborContractService.create(request));
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasPermission('VIEW CONTRACT_MANAGEMENT', '')")
    public BaseResponse<LaborContractResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<LaborContractResponse>()
                .success(laborContractService.findOneById(id));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasPermission('UPDATE CONTRACT_MANAGEMENT', '')")
    public BaseResponse<LaborContractResponse> update(@PathVariable Long id, @Valid @RequestBody LaborContractRequest request) {
        return new BaseResponse
                .ResponseBuilder<LaborContractResponse>()
                .success(laborContractService.update(id, request));
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasPermission('DELETE CONTRACT_MANAGEMENT', '')")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(laborContractService.delete(id));
    }

    @PostMapping("about-to-expired/search")
//    @PreAuthorize("hasPermission('VIEW CONTRACT_MANAGEMENT', '')")
    public BaseResponse<List<LaborContractResponse>> searchExpiredContract(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(laborContractService.findAboutToExpiredContract(new SearchDTO()));
    }

    @PostMapping("create-temp-contract")
//    @PreAuthorize("hasPermission('INSERT CONTRACT_MANAGEMENT', '')")
    public BaseResponse<FileDTO.FileResponse> createTempContract(@RequestBody ContractExportRequest request) {
        return new BaseResponse
            .ResponseBuilder<FileDTO.FileResponse>()
            .success(laborContractService.createTempContract(request));
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
                .success(laborContractService.addEmployeeSignatureInResign(request));
    }

//    @PostMapping("/export-contract")
//    public BaseResponse<FileDTO.FileResponse> exportWord(@RequestBody ContractExportRequest request) {
//        return new BaseResponse.ResponseBuilder<FileDTO.FileResponse>()
//                .success(laborContractService.exportContractForEmployeeToSign(request));
//    }

    @PostMapping("/send-to-employee-to-sign")
    public BaseResponse<FileDTO.FileResponse> send2Employee2Sign(@RequestBody ContractExportRequest request) {
        return new BaseResponse.ResponseBuilder<FileDTO.FileResponse>()
                .success(laborContractService.sendContractToEmployeeInResign(request.contractId));
    }
}


