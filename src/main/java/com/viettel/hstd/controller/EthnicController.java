package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.EthnicDTO.*;
import com.viettel.hstd.service.inf.EthnicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("ethnic")
@Tag(name = "Ethnic")
public class EthnicController {

    @Autowired
    private EthnicService ethnicService;

    @GetMapping
    public BaseResponse<List<EthnicResponse>> findAll() {
        return SearchUtils.getResponseFromPage(ethnicService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<EthnicResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(ethnicService.findPage(searchDTO));
    }

    @GetMapping("/{id}")
    public BaseResponse<EthnicResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<EthnicResponse>()
                .success(ethnicService.findOneById(id));
    }

    @PostMapping("check")
    public boolean search(@RequestBody String content) {
        return ethnicService.check(content);
    }

}