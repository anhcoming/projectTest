package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.RecruiteeAccountDTO.*;
import com.viettel.hstd.service.inf.RecruiteeAccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("sys-user")
@Tag(name = "System users")
public class RecruiteeAccountController {
    @Autowired
    private RecruiteeAccountService recruiteeAccountService;

    @GetMapping("/{id}")
    public BaseResponse<RecruiteeAccountResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<RecruiteeAccountResponse>()
                .success(recruiteeAccountService.findOneById(id));
    }


    @PostMapping("search")
    public BaseResponse<List<RecruiteeAccountResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(recruiteeAccountService.findPage(searchDTO));
    }

    @PostMapping("create-account")
    public BaseResponse<RecruiteeAccountResponse> createAccount(@RequestBody Long interviewSessionCvId) {
        return new BaseResponse
                .ResponseBuilder<RecruiteeAccountResponse>()
                .success(recruiteeAccountService.createAccount(interviewSessionCvId));
    }

}
