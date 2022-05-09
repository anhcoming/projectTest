package com.viettel.hstd.controller;


import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.EmployeeVhrTempDTO;
import com.viettel.hstd.dto.hstd.EmployeeVhrTempDTO.*;
import com.viettel.hstd.service.inf.EmployeeVhrTempService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("employee-profile")
@Tag(name = "Employee Profile (Employee Vhr Temp)")
public class EmployeeVhrTempController {

    @Autowired
    private EmployeeVhrTempService employeeVhrTempService;

    @PostMapping("search")
    public BaseResponse<List<EmployeeVhrTempResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(employeeVhrTempService.findPage(searchDTO));
    }

    @GetMapping("/{id}")
    public BaseResponse<EmployeeVhrTempResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<EmployeeVhrTempResponse>()
                .success(employeeVhrTempService.findOneById(id));
    }

    @PutMapping("/{id}")
    public BaseResponse<EmployeeVhrTempResponse> update(@PathVariable Long id, @Valid @RequestBody EmployeeVhrTempDTO.EmployeeVhrTempRequest request) {
        return new BaseResponse
                .ResponseBuilder<EmployeeVhrTempResponse>()
                .success(employeeVhrTempService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(employeeVhrTempService.delete(id));
    }

    @PostMapping("lock")
    public BaseResponse<Integer> lock(@RequestBody EmployeeVhrTempLocking data) {
        return new BaseResponse
                .ResponseBuilder<Integer>()
                .success(employeeVhrTempService.lock(data));
    }

    @PostMapping("unlock")
    public BaseResponse<Integer> unlock(@RequestBody EmployeeVhrTempLocking data) {
        return new BaseResponse
                .ResponseBuilder<Integer>()
                .success(employeeVhrTempService.unlock(data));
    }

    @PostMapping("approval")
    public BaseResponse<Integer> approval(@RequestBody EmployeeVhrTempRequest data) {
        return new BaseResponse
                .ResponseBuilder<Integer>()
                .success(employeeVhrTempService.approval(data.employeeVhrTempId));
    }

    @PostMapping("reject")
    public BaseResponse<Integer> reject(@RequestBody EmployeeVhrTempRequest data) {
        return new BaseResponse
                .ResponseBuilder<Integer>()
                .success(employeeVhrTempService.reject(data.employeeVhrTempId));
    }

    @PostMapping("send-vhr-code-request")
    public BaseResponse<Boolean> sendVhrCodeRequest(@RequestBody SendVhrCodeRequest request) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(employeeVhrTempService.sendRequestToVHRToReceivedEmployeeCode(request));
    }

    @GetMapping("get-attachment/{id}")
    public BaseResponse<EmployeeVhrAttachmentResponse> getAttachment(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<EmployeeVhrAttachmentResponse>()
                .success(employeeVhrTempService.getAttachment(id));
    }

}
