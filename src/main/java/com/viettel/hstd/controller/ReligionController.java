package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
        import com.viettel.hstd.core.dto.SearchDTO;
        import com.viettel.hstd.core.utils.SearchUtils;
        import com.viettel.hstd.dto.hstd.ReligionDTO.*;
        import com.viettel.hstd.service.inf.ReligionService;
        import io.swagger.v3.oas.annotations.tags.Tag;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController()
@RequestMapping("religion")
@Tag(name = "Religion")
public class ReligionController {

    @Autowired
    private ReligionService religionService;

    @GetMapping
    public BaseResponse<List<ReligionResponse>> findAll() {
        return SearchUtils.getResponseFromPage(religionService.findPage(new SearchDTO()));
    }

    @PostMapping("search")
    public BaseResponse<List<ReligionResponse>> search(@RequestBody SearchDTO searchDTO) {
        return SearchUtils.getResponseFromPage(religionService.findPage(searchDTO));
    }

    @GetMapping("/{id}")
    public BaseResponse<ReligionResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<ReligionResponse>()
                .success(religionService.findOneById(id));
    }

}