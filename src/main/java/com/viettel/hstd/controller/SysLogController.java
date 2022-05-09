package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.SysConfigDTO;
import com.viettel.hstd.dto.hstd.SysLogDTO;
import com.viettel.hstd.service.inf.SysLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("sys-log")
@Tag(name = "System Log")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    @PostMapping("search")
    public BaseResponse<List<SysLogDTO.SysLogResponse>> search(@RequestBody SearchDTO searchDTO) {
        Page<SysLogDTO.SysLogResponse> list = sysLogService.findPage(searchDTO);
        return SearchUtils.getResponseFromPage(list);
    }

    @GetMapping("/{id}")
    public BaseResponse<SysLogDTO.SysLogResponse> findOneById(@PathVariable Long id) {
        return new BaseResponse
                .ResponseBuilder<SysLogDTO.SysLogResponse>()
                .success(sysLogService.findOneById(id));
    }
}
