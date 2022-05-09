package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.RecruitmentProgressDTO;
import com.viettel.hstd.service.inf.RecruitmentProgressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "Recruitment Progress")
public class RecruitmentProgressController {

    private final RecruitmentProgressService recruitmentProgressService;

    @Value("classpath:template/Import_kehoachtuyendung.xlsx")
    private Resource resourceFile;

    @PostMapping(value = "/v1/recruitment-progress")
    public BaseResponse<Boolean> create(@RequestBody @Valid RecruitmentProgressDTO.RecruitmentProgressRequest request) {
        recruitmentProgressService.create(request);
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(true);

    }

    @PostMapping(value = "/v1/recruitment-progress/search")
    public BaseResponse<List<RecruitmentProgressDTO.Response>> search(@RequestBody @Valid RecruitmentProgressDTO.SearchCriteria criteria) {
        return SearchUtils.getResponseFromPage(recruitmentProgressService.search(criteria));
    }

    @PostMapping("/v1/recruitment-progress/{id}/delete")
    public BaseResponse<Void> deleteById(@PathVariable @Min(1) Long id) {
        recruitmentProgressService.deleteById(id);
        return new BaseResponse
                .ResponseBuilder<Void>()
                .success(null);
    }

    @PutMapping("/v1/recruitment-progress/{id}")
    public BaseResponse<Boolean> update(@PathVariable @Min(1) Long id,
                                        @RequestBody @Valid RecruitmentProgressDTO.RecruitmentProgressRequest request) {
        recruitmentProgressService.update(request, id);
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(true);

    }

    @GetMapping("/v1/recruitment-progress/{id}")
    public BaseResponse<RecruitmentProgressDTO.SingleResponse> findById(@PathVariable @Min(1) Long id) {
        return new BaseResponse
                .ResponseBuilder<RecruitmentProgressDTO.SingleResponse>()
                .success(recruitmentProgressService.getOne(id));
    }

    @GetMapping("/v1/test-send-mail")
    public BaseResponse<Void> sendMail() throws MessagingException {
        recruitmentProgressService.sendEmailAlert();
        return new BaseResponse
                .ResponseBuilder<Void>()
                .success(null);
    }

    @PostMapping("/v1/recruitment-progress/import-from-file")
    public BaseResponse<Long> importDataFromFile(@RequestBody RecruitmentProgressDTO.ImportRequest importRequest) throws IOException {
        Long result = recruitmentProgressService.importFromFile(importRequest.getFileUrl(), importRequest.getFileTitle());
        return new BaseResponse
                .ResponseBuilder<Long>()
                .success(result);
    }

    @GetMapping("/v1/recruitment-progress/download-template-import")
    public BaseResponse<String> downloadTemplateImport() throws IOException {
        byte[] data = Base64.getEncoder().encode(Files.readAllBytes(resourceFile.getFile().toPath()));

        return new BaseResponse
                .ResponseBuilder<String>()
                .success(new String(data, StandardCharsets.US_ASCII));

    }
}
