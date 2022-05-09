package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.dto.hstd.CvDTO;
import com.viettel.hstd.dto.hstd.DashboardDTO.*;
import com.viettel.hstd.service.inf.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("dashboard")
@Tag(name = "Dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("top")
    public BaseResponse<DashboardTopResponse> importExcel() {
        return new BaseResponse
                .ResponseBuilder<DashboardTopResponse>()
                .success(dashboardService.getDashboardTop());
    }
}
