package com.viettel.hstd.controller;


import com.viettel.hstd.constant.TerminateStatusConstant;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.UploadDTO;
import com.viettel.hstd.dto.hstd.InterviewSessionCvDTO;
import com.viettel.hstd.dto.hstd.TerminateContractDTO.*;
import com.viettel.hstd.exception.BadRequestException;
import com.viettel.hstd.service.inf.FileService;
import com.viettel.hstd.service.inf.TerminateContractService;
import com.viettel.hstd.util.FolderExtension;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController()
@RequestMapping("terminate-contract")
@Tag(name = "Terminate Contract")
public class TerminateContractController {
    @Autowired
    private TerminateContractService terminateContractService;
    @Autowired
    FileService fileService;
    @Autowired
    Message message;
    @Autowired
    private FolderExtension folderExtension;

    @PostMapping("search")
    public BaseResponse<List<TerminateContractResponse>> search(@RequestBody SearchDTO searchDTO) {
        Page<TerminateContractResponse> list = terminateContractService.findPage(searchDTO);
        return SearchUtils.getResponseFromPage(list);
    }

    @GetMapping("/{id}")
    public BaseResponse<TerminateContractResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<TerminateContractResponse>()
                .success(terminateContractService.findOneById(id));
    }

    @PostMapping
    public BaseResponse<TerminateContractResponse> create(@Valid @RequestBody TerminateContractRequest request) {
        TerminateContractResponse response = terminateContractService.create(request);
        if (response == null) {
            return new BaseResponse
                    .ResponseBuilder<TerminateContractResponse>()
                    .failed(null, "Không thể thực hiện thao tác này");
        }
        return new BaseResponse
                .ResponseBuilder<TerminateContractResponse>()
                .success(response);
    }

    @PutMapping("/{id}")
    public BaseResponse<TerminateContractResponse> update(@PathVariable Long id, @Valid @RequestBody TerminateContractRequest request) {
        return new BaseResponse
                .ResponseBuilder<TerminateContractResponse>()
                .success(terminateContractService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(terminateContractService.delete(id));
    }

    /**
     * Cap nhat quyet dinh cham dut hop dong lao dong
     *
     * @param request
     * @return
     */
    @PostMapping("/update-decision")
    public BaseResponse<TerminateContractResponse> updateDecision(@RequestBody TerminateContractRequest request) {
        return new BaseResponse
                .ResponseBuilder<TerminateContractResponse>()
                .success(terminateContractService.updateDecision(request));
    }

    @PostMapping("/create-sev-allowance")
    public BaseResponse<FileDTO.FileResponse> createSevAllowance(@RequestBody TerminateContractRequest request) {
        return new BaseResponse
                .ResponseBuilder<FileDTO.FileResponse>()
                .success(terminateContractService.createSevAllowance(request));
    }

    @PostMapping("/confirm-quit")
    public BaseResponse<TerminateContractResponse> confirmQuit(@RequestBody TerminateContractRequest request) {
        TerminateContractResponse response = terminateContractService.confrimQuit(request);
        if (response == null) {
            return new BaseResponse
                    .ResponseBuilder<TerminateContractResponse>()
                    .failed(null, "Bạn không có quyền thực hiện thao tác này");
        }
        return new BaseResponse
                .ResponseBuilder<TerminateContractResponse>()
                .success(response);
    }

    /**
     * lay danh sach don xin nghi cho quan ly truc tiep
     *
     * @param searchDTO
     * @return
     */
    @PostMapping("/get-data-by-manager")
    public BaseResponse<List<TerminateContractResponse>> getDataByManager(@RequestBody SearchDTO searchDTO) {
        if (searchDTO == null) {
            searchDTO = new SearchDTO();
        }
        if (searchDTO.criteriaList == null) {
            searchDTO.criteriaList = new ArrayList<>();
        }
        SearchDTO.SearchCriteria manager = new SearchDTO.SearchCriteria();
        manager.setField("managerId");
        searchDTO.criteriaList.add(manager);
        Page<TerminateContractResponse> list = terminateContractService.findPage(searchDTO);
        return SearchUtils.getResponseFromPage(list);
    }

    @PostMapping("/export-excel")
    public BaseResponse<FileDTO.FileResponse> exportExcel(@RequestBody SearchDTO searchDTO) {
        return new BaseResponse
                .ResponseBuilder<FileDTO.FileResponse>()
                .success(terminateContractService.exportExcel(searchDTO));
    }

    /**
     * Xuat danh sach nhan vien cho nghi viec
     *
     * @param searchDTO
     * @return
     */
    @PostMapping("/export-waiting-leave")
    public BaseResponse<FileDTO.FileResponse> exportWaitingLeave(@RequestBody SearchDTO searchDTO) {
        return new BaseResponse
                .ResponseBuilder<FileDTO.FileResponse>()
                .success(terminateContractService.exportIdleTime(searchDTO));
    }

    /**
     * gui don xin nghi viec
     *
     * @param request
     * @return
     */
    @PostMapping("/send-resignation")
    public BaseResponse<TerminateContractResponse> sendResignation(@RequestBody TerminateContractRequest request) {
        TerminateContractResponse response = terminateContractService.sendResignation(request);
        if (response == null) {
            return new BaseResponse
                    .ResponseBuilder<TerminateContractResponse>()
                    .failed(null, "Bạn không có quyền thực hiện thao tác này");
        }
        return new BaseResponse
                .ResponseBuilder<TerminateContractResponse>()
                .success(response);
    }

    @PostMapping("/merge-sev-allowance")
    public BaseResponse<FileDTO.FileResponse> mergeSevAllowance(@RequestBody List<Long> ids) {
        return new BaseResponse
                .ResponseBuilder<FileDTO.FileResponse>()
                .success(terminateContractService.mergeTerminateContractOnlyPdf(ids));
    }
}
