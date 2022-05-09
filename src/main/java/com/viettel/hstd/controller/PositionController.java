package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.vps.PositionDTO.*;
import com.viettel.hstd.service.inf.PositionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("position")
@Tag(name = "Position")
public class PositionController {

    @Autowired
    private PositionService positionService;

    @GetMapping
    public BaseResponse<List<PositionResponse>> findAll() {
        return SearchUtils.getResponseFromPage(positionService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<PositionResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(positionService.findPage(searchDTO));
    }

    @GetMapping("/{id}")
    public BaseResponse<PositionResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<PositionResponse>()
                .success(positionService.findOneById(id));
    }

}
