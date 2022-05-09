package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
        import com.viettel.hstd.core.dto.SearchDTO;
        import com.viettel.hstd.core.utils.SearchUtils;
        import com.viettel.hstd.dto.FileDTO;
        import com.viettel.hstd.dto.hstd.ResignSessionContractDTO;
        import com.viettel.hstd.dto.hstd.ResignSessionDTO.*;
        import com.viettel.hstd.repository.hstd.ResignSessionContractRepository;
        import com.viettel.hstd.service.inf.ResignSessionContractService;
        import com.viettel.hstd.service.inf.ResignSessionService;
        import io.swagger.v3.oas.annotations.tags.Tag;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.web.bind.annotation.*;

        import javax.validation.Valid;
        import java.util.List;

@RestController()
@RequestMapping("general-resign")
@Tag(name = "General Resign Session")
public class GeneralResignSessionController {
    @Autowired
    ResignSessionService resignSessionService;

    @Autowired
    ResignSessionContractService resignSessionContractService;

    @PostMapping("/labor/export-data-for-voffice-2")
    public BaseResponse<List<FileDTO.FileResponse>> exportFilesForLaborVoffice2(@RequestBody ResignSessionContractDTO.ResignContractAddToVofficeLaborRequest request) {
        return new BaseResponse
                .ResponseBuilder<List<FileDTO.FileResponse>>()
                .success(resignSessionContractService.exportFilesForLaborVoffice2(request));
    }

    @PostMapping("/probationary/export-data-for-voffice-2")
    public BaseResponse<List<FileDTO.FileResponse>> exportFilesForProbationaryVoffice2(@RequestBody ResignSessionContractDTO.ResignContractAddToVofficeProbationaryRequest request) {
        return new BaseResponse
                .ResponseBuilder<List<FileDTO.FileResponse>>()
                .success(resignSessionContractService.exportFilesForProbationaryVoffice2(request));
    }
}
