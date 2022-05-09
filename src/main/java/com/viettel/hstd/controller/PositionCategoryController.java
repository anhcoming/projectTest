package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.dto.hstd.PositionCategoryDTO;
import com.viettel.hstd.service.inf.PositionCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PositionCategoryController {

    private final PositionCategoryService positionCategoryService;

    @GetMapping("/v1/position-categories/find-by-code")
    public BaseResponse<PositionCategoryDTO.Response> findByPositionCode(@RequestParam String positionCode){
        return new BaseResponse
                .ResponseBuilder<PositionCategoryDTO.Response>()
                .success(positionCategoryService.findByPositionCode(positionCode));
    }
}
