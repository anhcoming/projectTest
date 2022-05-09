package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ResignSessionContractDTO;
import com.viettel.hstd.dto.hstd.ResignSessionDTO.*;
import com.viettel.hstd.repository.hstd.ResignSessionContractRepository;
import com.viettel.hstd.service.inf.ResignSessionContractService;
import com.viettel.hstd.service.inf.ResignSessionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("resign")
@Tag(name = "Resign Session")
public class ResignSessionController {
    @Autowired
    ResignSessionService resignSessionService;

    @Autowired
    ResignSessionContractService resignSessionContractService;

    @GetMapping
    public BaseResponse<List<ResignSessionResponse>> findAll() {
        return SearchUtils.getResponseFromPage(resignSessionService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<ResignSessionResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(resignSessionService.findPage(searchDTO));
    }

    @PostMapping
    public BaseResponse<ResignSessionResponse> create(@Valid @RequestBody ResignSessionRequest request) {
        return new BaseResponse
                .ResponseBuilder<ResignSessionResponse>()
                .success(resignSessionService.create(request));
    }

    @GetMapping("/{id}")
    public BaseResponse<ResignSessionResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<ResignSessionResponse>()
                .success(resignSessionService.findOneById(id));
    }

    @PutMapping("/{id}")
    public BaseResponse<ResignSessionResponse> update(@PathVariable Long id, @Valid @RequestBody ResignSessionRequest request) {
        return new BaseResponse
                .ResponseBuilder<ResignSessionResponse>()
                .success(resignSessionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(resignSessionService.delete(id));
    }

    @PostMapping("/add-to-voffice-2/labor")
    public BaseResponse<Boolean> addToVOffice2Labor(@RequestBody ResignSessionContractDTO.ResignContractAddToVofficeLaborRequest request) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(resignSessionService.addContractToVoffice2(request));
    }

    @PostMapping("/add-to-voffice-2/probationary")
    public BaseResponse<Boolean> addToVOffice2Probationary(@RequestBody ResignSessionContractDTO.ResignContractAddToVofficeProbationaryRequest request) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(resignSessionService.addContractToVoffice2(request));
    }

    @PostMapping("/bm07/export")
    public BaseResponse<FileDTO.FileResponse> exportBm07(@RequestBody ResignSessionContractDTO.ExportBMRequest request) {
        return new BaseResponse
                .ResponseBuilder<FileDTO.FileResponse>()
                .success(resignSessionService.exportBm07(request.resignSessionId));
    }

    @PostMapping("/bm08/export")
    public BaseResponse<FileDTO.FileResponse> exportBm08(@RequestBody ResignSessionContractDTO.ExportBMRequest request) {
        return new BaseResponse
                .ResponseBuilder<FileDTO.FileResponse>()
                .success(resignSessionService.exportBm08(request.resignSessionId));
    }
}