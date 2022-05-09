package com.viettel.hstd.controller;

import com.itextpdf.text.DocumentException;
import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.CollaboratorContractDTO.*;
import com.viettel.hstd.dto.hstd.ExportInterviewResultDTO;
import com.viettel.hstd.service.inf.CollaboratorContractService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Hop dong cong tac vien
 */
@RestController()
@RequestMapping("collaborator-contract")
@Tag(name = "Collaborator Contract")
public class CollaboratorContractController {
    @Autowired
    private CollaboratorContractService collaboratorContractService;

    @PostMapping("search")
    public BaseResponse<List<CollaboratorContractResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(collaboratorContractService.findPage(searchDTO));
    }

    @GetMapping("/{id}")
    public BaseResponse<CollaboratorContractResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<CollaboratorContractResponse>()
                .success(collaboratorContractService.findOneById(id));
    }

    @PostMapping("/export-word")
    public BaseResponse<FileDTO.FileResponse> exportWord(@RequestBody CollaboratorContractRequest input) {
        return new BaseResponse.ResponseBuilder<FileDTO.FileResponse>()
                .success(collaboratorContractService.exportContract(input.contractId));
    }
}
