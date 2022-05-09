package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.CvDTO;
import com.viettel.hstd.dto.hstd.InterviewSessionCvDTO.*;
import com.viettel.hstd.dto.hstd.InterviewSessionDTO.*;
import com.viettel.hstd.dto.hstd.EmployeeInterviewSessionDTO.*;
import com.viettel.hstd.dto.vps.EmployeeVhrDTO;
import com.viettel.hstd.service.inf.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import liquibase.pro.packaged.B;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("interview-session")
@Tag(name = "Interview Session")
public class InterViewSessionController {
    @Autowired
    private InterviewSessionService interviewSessionService;
    @Autowired
    private EmployeeInterviewSessionService employeeInterviewSessionService;
    @Autowired
    private InterviewSessionCvService interviewSessionCvService;

    @GetMapping
    public BaseResponse<List<InterviewSessionResponse>> findAll() {
        return SearchUtils.getResponseFromPage(interviewSessionService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<InterviewSessionResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(interviewSessionService.findPage(searchDTO));
    }

    /**
     * Them moi dot phong van
     *
     * @param request du lieu dau vao
     * @return
     */
    @PostMapping
    public BaseResponse<InterviewSessionResponse> create(@Valid @RequestBody InterviewSessionRequest request) {
        return new BaseResponse
                .ResponseBuilder<InterviewSessionResponse>()
                .success(interviewSessionService.create(request));
    }

    /**
     * Tim dot phong van theo ma
     *
     * @param id ma dot phong van
     * @return
     */
    @GetMapping("/{id}")
    public BaseResponse<InterviewSessionResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<InterviewSessionResponse>()
                .success(interviewSessionService.findOneById(id));
    }

    /**
     * Cap nhat dot phong van
     *
     * @param id      ma dot phong van
     * @param request du lieu cap nhat
     * @return
     */
    @PutMapping("/{id}")
    public BaseResponse<InterviewSessionResponse> update(@PathVariable Long id, @Valid @RequestBody InterviewSessionRequest request) {
        return new BaseResponse
                .ResponseBuilder<InterviewSessionResponse>()
                .success(interviewSessionService.update(id, request));
    }

    /**
     * Xoa dot phong van
     *
     * @param id ma dot phong van
     * @return
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(interviewSessionService.delete(id));
    }

    /**
     * Them moi hoac xoa nhan vien trong dot phong van
     *
     * @param id      ma dot phong van
     * @param request danh sach nhan vien trong dot phong van
     * @return
     */
    @PostMapping("add-employee")
    public BaseResponse<Boolean> addOrUpdateEmp(@RequestParam(name = "id", required = false) Long id, @RequestBody List<EmployeeInterviewSessionRequest> request) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(employeeInterviewSessionService.addOrUpdateListEmployee(id, request));
    }

    /**
     * Them moi ung vien cho dot phong van
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping("add-cv")
    public BaseResponse<Boolean> addOrUpdateCv(@RequestParam(name = "id", required = false) Long id, @RequestBody List<InterviewSessionCvRequest> request) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(interviewSessionCvService.addOrUpdateListCv(id, request));
    }

    /**
     * Danh sach ung vien
     *
     * @param id id cua dot phong van
     * @return
     */
    @GetMapping("get-candidate")
    public BaseResponse<List<InterviewSessionCvResponse>> getCandidate(@RequestParam(name = "id", required = true) Long id) {
        return new BaseResponse
                .ResponseBuilder<List<InterviewSessionCvResponse>>()
                .success(interviewSessionCvService.findByInterviewSessionId(id));
    }

    /**
     * Danh sach nhan vien cua dot phong van
     *
     * @param id
     * @return
     */
    @GetMapping("get-employee")
    public BaseResponse<List<EmployeeInterviewSessionResponse>> getEmployee(@RequestParam(name = "id", required = true) Long id) {
        return new BaseResponse
                .ResponseBuilder<List<EmployeeInterviewSessionResponse>>()
                .success(employeeInterviewSessionService.findByInterviewSessionId(id));
    }

    @PostMapping("is-existed")
    public BaseResponse<Boolean> checkExisted(@RequestBody InterviewExistedRequest request) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(interviewSessionService.checkExisted(request.positionId, request.startDate, request.endDate));
    }

    @PostMapping("/export-excel")
    public BaseResponse<FileDTO.FileResponse> exportExcel(@RequestBody InterviewSessionResponse response) {
        return new BaseResponse
                .ResponseBuilder<FileDTO.FileResponse>()
                .success(interviewSessionService.exportExcel(response));
    }
}
