package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.dto.hstd.EmailDTO;
import com.viettel.hstd.dto.hstd.MultipleEmailDTO;
import com.viettel.hstd.service.inf.EmailService;
import com.viettel.hstd.service.inf.InterviewSessionCvService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController()
@RequestMapping("email")
@Tag(name = "Send email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/send-offer")
    public BaseResponse<Boolean> sendOffer(@RequestBody EmailDTO model) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(emailService.sendMessage(model));
    }


    /**
     * Gui email thong bao nhan viec
     *
     * @return
     */
    @PostMapping("send-multiple-offer")
    public BaseResponse<Boolean> sendEmailOffer(@RequestBody MultipleEmailDTO multipleEmail) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(emailService.sendOffer(multipleEmail));
    }

    /**
     * Gui email thong bao cho nhan vien nhan viec
     *
     * @param model du lieu dau vao
     * @return true or false
     */
    @PostMapping("/send-notify")
    public BaseResponse<Boolean> sendNotify(@RequestBody EmailDTO model) {
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(emailService.sendNotify(model));
    }

}
