package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.dto.hstd.PositionDescriptionFileDTO;
import com.viettel.hstd.service.inf.PositionDescriptionFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PositionDescriptionFileController {

    private final PositionDescriptionFileService positionDescriptionFileService;

    @GetMapping("/v1/position-description-files/position-descriptions/{positionDescriptionId}")
    public BaseResponse<List<PositionDescriptionFileDTO.Response>> getListFile(@PathVariable("positionDescriptionId") Long positionDescriptionId){
        return new BaseResponse
                .ResponseBuilder<List<PositionDescriptionFileDTO.Response>>()
                .success(positionDescriptionFileService.getListFileByPositionDescriptionId(positionDescriptionId));
    }

}
