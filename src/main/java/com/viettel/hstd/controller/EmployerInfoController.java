package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.EmployerInfoDTO.*;
import com.viettel.hstd.service.inf.EmployerInforService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("employer-info")
@Tag(name = "Employer Information")
public class EmployerInfoController {
    @Autowired
    private EmployerInforService employerInforService;

    @PostMapping("search")
    public BaseResponse<List<EmployerInfoResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(employerInforService.findPage(searchDTO));
    }

    @PostMapping
    public BaseResponse<EmployerInfoResponse> create(@Valid @RequestBody EmployerInfoRequest request) {
        return new BaseResponse
                .ResponseBuilder<EmployerInfoResponse>()
                .success(employerInforService.create(request));
    }

    @GetMapping("/{id}")
    public BaseResponse<EmployerInfoResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<EmployerInfoResponse>()
                .success(employerInforService.findOneById(id));
    }

    @PutMapping("/{id}")
    public BaseResponse<EmployerInfoResponse> update(@PathVariable Long id, @Valid @RequestBody EmployerInfoRequest request) {
        return new BaseResponse
                .ResponseBuilder<EmployerInfoResponse>()
                .success(employerInforService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(employerInforService.delete(id));
    }

}
