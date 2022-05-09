package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.PositionDescriptionDTO;
import com.viettel.hstd.service.inf.PositionDescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PositionDescriptionController {

    @Value("classpath:template/Import_MoTaCongViec.xlsx")
    private Resource resourceFile;

    private final PositionDescriptionService positionDescriptionService;

    @GetMapping("/v1/position-descriptions/download-template-import")
    public BaseResponse<String> downloadTemplateImport() throws IOException {
        byte[] data = Base64.getEncoder().encode(Files.readAllBytes(resourceFile.getFile().toPath()));

        return new BaseResponse
                .ResponseBuilder<String>()
                .success(new String(data, StandardCharsets.US_ASCII));

    }

    @PostMapping("/v1/position-descriptions/import-from-file")
    public BaseResponse<Long> importFromFile(@RequestBody PositionDescriptionDTO.ImportRequest importRequest) throws IOException {
        Long importId = positionDescriptionService.importExcel(importRequest.getExcelFileTitle(),
                importRequest.getExcelFileUrl(), importRequest.getAttachmentFileUrl());
        return new BaseResponse
                .ResponseBuilder<Long>()
                .success(importId);
    }

    @PostMapping(value = "/v1/position-descriptions/search")
    public BaseResponse<List<PositionDescriptionDTO.Response>> search(@RequestBody @Valid PositionDescriptionDTO.SearchCriteria criteria) {
        return SearchUtils.getResponseFromPage(positionDescriptionService.search(criteria));
    }

    @PostMapping("/v1/position-descriptions/{id}/delete")
    public BaseResponse<Void> deleteById(@PathVariable @Min(1) Long id) {
        positionDescriptionService.deleteById(id);
        return new BaseResponse
                .ResponseBuilder<Void>()
                .success(null);
    }

    @PostMapping("/v1/position-descriptions")
    public BaseResponse<Boolean> create(@RequestBody PositionDescriptionDTO.Request request) {
        positionDescriptionService.save(request);
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(true);
    }

    @GetMapping("/v1/position-descriptions/{id}")
    public BaseResponse<PositionDescriptionDTO.SingleResponse> findById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<PositionDescriptionDTO.SingleResponse>()
                .success(positionDescriptionService.findById(id));
    }

    @PutMapping("/v1/position-descriptions/{id}")
    public BaseResponse<Boolean> update(@PathVariable Long id, @RequestBody PositionDescriptionDTO.Request request) {
        positionDescriptionService.update(id, request);
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(true);
    }
}
