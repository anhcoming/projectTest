package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ResignSessionContractDTO;
import com.viettel.hstd.service.inf.ContractService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("report")
@Tag(name = "Report")
public class ReportController {
    @Autowired
    private ContractService contractService;

    @PostMapping("/danh-sach-nhan-su/export")
    public BaseResponse<FileDTO.FileResponse> exportDanhSachNhanSu() {
        return new BaseResponse
                .ResponseBuilder<FileDTO.FileResponse>()
                .success(contractService.exportDanhSachNhanSu());
    }
}
