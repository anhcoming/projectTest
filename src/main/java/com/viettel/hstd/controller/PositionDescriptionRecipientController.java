package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.dto.hstd.PositionDescriptionFileDTO;
import com.viettel.hstd.dto.hstd.PositionDescriptionRecipientDTO;
import com.viettel.hstd.service.inf.PositionDescriptionRecipientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PositionDescriptionRecipientController {

    private final PositionDescriptionRecipientService positionDescriptionRecipientService;

    @GetMapping("/v1/position-description-recipients/position-descriptions/{positionDescriptionId}")
    public BaseResponse<List<PositionDescriptionRecipientDTO.Response>> getListFile(@PathVariable("positionDescriptionId") Long positionDescriptionId) {
        return new BaseResponse
                .ResponseBuilder<List<PositionDescriptionRecipientDTO.Response>>()
                .success(positionDescriptionRecipientService.getRecipientsByPositionDescriptionId(positionDescriptionId));
    }

    @GetMapping("/v1/position-description-recipients/test-send-email")
    public BaseResponse<Void> sendEmail() throws MessagingException {
        positionDescriptionRecipientService.sendEmailAlert();
        return new BaseResponse
                .ResponseBuilder<Void>()
                .success(null);
    }
}
