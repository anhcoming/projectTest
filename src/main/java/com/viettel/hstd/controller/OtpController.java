package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.OtpDTO;
import com.viettel.hstd.dto.hstd.ProvinceAreaDTO;
import com.viettel.hstd.dto.hstd.ReligionDTO;
import com.viettel.hstd.service.inf.OtpService;
import com.viettel.hstd.service.inf.ReligionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("otp")
@Tag(name = "OTP")
public class OtpController {
    @Autowired
    private OtpService otpService;

    @PostMapping("create")
    public BaseResponse<OtpDTO.OtpCreateResponse> create(@RequestBody OtpDTO.OtpCreateRequest request) {
        return new BaseResponse
                .ResponseBuilder<OtpDTO.OtpCreateResponse>()
                .success(otpService.create(request));
    }

    @PostMapping("submit")
    public BaseResponse<OtpDTO.OtpSubmitResponse> submit(@RequestBody OtpDTO.OtpRequest request) {
        return new BaseResponse
                .ResponseBuilder<OtpDTO.OtpSubmitResponse>()
                .success(otpService.submit(request));
    }

}
