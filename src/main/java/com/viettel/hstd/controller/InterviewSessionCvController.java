package com.viettel.hstd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.*;
import com.viettel.hstd.dto.hstd.InterviewSessionCvDTO.*;
import com.viettel.hstd.service.inf.InterviewSessionCvService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@RestController()
@RequestMapping("interview-session-cv")
@Tag(name = "Interview Session CV")
public class InterviewSessionCvController {
    @Autowired
    private InterviewSessionCvService interviewSessionCvService;

    /**
     * Them ket qua phong van
     *
     * @param request du lieu dau vao
     * @return
     */
    @PostMapping
    public BaseResponse<InterviewSessionCvResponse> create(@Valid @RequestBody InterviewSessionCvRequest request) {
        return new BaseResponse
                .ResponseBuilder<InterviewSessionCvResponse>()
                .success(interviewSessionCvService.create(request));
    }

    /**
     * Cap nhat ket qua phong van
     *
     * @param id      ma ket qua phong van
     * @param request du lieu dau vao
     * @return
     */
    @PutMapping("/{id}")
    public BaseResponse<InterviewSessionCvResponse> update(@PathVariable Long id, @Valid @RequestBody InterviewSessionCvRequest request) {
        return new BaseResponse
                .ResponseBuilder<InterviewSessionCvResponse>()
                .success(interviewSessionCvService.update(id, request));
    }

    /**
     * Xem ket qua phong van theo id
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public BaseResponse<InterviewSessionCvResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<InterviewSessionCvResponse>()
                .success(interviewSessionCvService.findOneById(id));
    }

    /**
     * Lay danh sach ket qua phong van theo cv id
     *
     * @param id cv id
     * @return
     */
    @GetMapping("/get-interview-result/{id}")
    public BaseResponse<List<InterviewSessionCvResponse>> getInterviewResult(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<List<InterviewSessionCvResponse>>()
                .success(interviewSessionCvService.findByCvId(id));
    }

    /**
     * Danh sach ket qua phong van
     *
     * @param searchDTO
     * @return
     */
    @PostMapping("search")
    public BaseResponse<List<InterviewSessionCvResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(interviewSessionCvService.findPage(searchDTO));
    }

    @GetMapping("/get-email-content/{id}/{templateId}")
    public BaseResponse<String> getEmailContent(@PathVariable Long id, @PathVariable Long templateId) {
        return new BaseResponse
                .ResponseBuilder<String>()
                .success(interviewSessionCvService.getEmailContent(id, templateId));
    }


    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(interviewSessionCvService.delete(id));
    }

    /**
     * Xuat file ket qua tuyen dung nhan su
     *
     * @param request
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    @PostMapping("/export-word")
    public BaseResponse<FileDTO.FileResponse> exportWord(@RequestBody ExportInterviewResultDTO input,
                                                         HttpServletRequest request) throws IOException, DocumentException {
        return new BaseResponse.ResponseBuilder<FileDTO.FileResponse>().success(interviewSessionCvService.exportInterviewResult(input));
    }

    @PostMapping("/export-excel")
    public BaseResponse<FileDTO.FileResponse> exportExcel(@RequestBody SearchDTO searchDTO) {
        return new BaseResponse
                .ResponseBuilder<FileDTO.FileResponse>()
                .success(interviewSessionCvService.exportExcel(searchDTO));
    }

    @PostMapping("/download-signed-file")
    public BaseResponse<FileDTO.FileResponse> downloadVofficeSignedReport(@RequestBody InterviewSessionCvRequest request) {
        FileDTO.FileResponse result = interviewSessionCvService.downloadVofficeSignedFile(request.interviewSessionCvId);
        if (result == null) {
            return new BaseResponse
                    .ResponseBuilder<FileDTO.FileResponse>()
                    .failed(null, "File not found");
        }
        return new BaseResponse
                .ResponseBuilder<FileDTO.FileResponse>()
                .success(interviewSessionCvService.downloadVofficeSignedFile(request.interviewSessionCvId));
    }

    @PostMapping("/download-interview-result")
    public BaseResponse<FileDTO.FileResponse> downloadReport(@RequestBody InterviewSessionCvRequest request) {
        return new BaseResponse
                .ResponseBuilder<FileDTO.FileResponse>()
                .success(interviewSessionCvService.downloadInterviewResult(request.interviewSessionCvId));
    }

}
