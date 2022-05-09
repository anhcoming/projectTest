package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.EmailCategoryDTO;
import com.viettel.hstd.dto.vps.DomainDataDTO.*;
import com.viettel.hstd.service.inf.DomainDataService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("domain-data")
@Tag(name = "Domain data")
public class DomainDataController {
    @Autowired
    private DomainDataService domainDataService;

    @GetMapping
    public BaseResponse<List<DomainDataResponse>> findAll() {
        return SearchUtils.getResponseFromPage(domainDataService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<DomainDataResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(domainDataService.findPage(searchDTO));
    }
}
