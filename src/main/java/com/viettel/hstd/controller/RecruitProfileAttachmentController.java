package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.dto.hstd.DocumentTypeDTO;
import com.viettel.hstd.dto.hstd.ProvinceAreaDTO;
import com.viettel.hstd.dto.hstd.RecruitProfileAttachmentDTO.*;
import com.viettel.hstd.service.inf.RecruitProfileAttachmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController()
@RequestMapping("upload-profile")
@Tag(name = "Upload profile")
public class RecruitProfileAttachmentController {
    @Autowired
    private RecruitProfileAttachmentService recruitProfileAttachmentService;

    @GetMapping("/{id}")
    public BaseResponse<RecruitProfileAttachmentResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<RecruitProfileAttachmentResponse>()
                .success(recruitProfileAttachmentService.findOneById(id));
    }

    @GetMapping("/get-profile-by-user/{id}")
    public BaseResponse<List<RecruitProfileAttachmentResponse>> getProfileByUserId(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<List<RecruitProfileAttachmentResponse>>()
                .success(recruitProfileAttachmentService.findByUserId(id));
    }

    @PutMapping("/{id}")
    public BaseResponse<RecruitProfileAttachmentResponse> update(@PathVariable Long id, @Valid @RequestBody RecruitProfileAttachmentRequest request) {
        return new BaseResponse
                .ResponseBuilder<RecruitProfileAttachmentResponse>()
                .success(recruitProfileAttachmentService.update(id, request));
    }

    @PostMapping("/approval/{id}")
    public BaseResponse<RecruitProfileAttachmentResponse> approval(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<RecruitProfileAttachmentResponse>()
                .success(recruitProfileAttachmentService.approval(id));
    }

    @PostMapping("/reject/{id}")
    public BaseResponse<RecruitProfileAttachmentResponse> reject(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<RecruitProfileAttachmentResponse>()
                .success(recruitProfileAttachmentService.reject(id));
    }
}
