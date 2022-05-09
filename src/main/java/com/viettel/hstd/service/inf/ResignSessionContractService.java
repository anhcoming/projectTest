package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ResignSessionContractDTO;
import com.viettel.hstd.dto.hstd.ResignSessionContractDTO.*;
import com.viettel.hstd.entity.hstd.ResignSessionContractEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ResignSessionContractService extends CRUDService<ResignSessionContractRequest, ResignSessionContractResponse, Long> {
    Page<ResignBm07Response> findPageBm07(SearchDTO searchRequest);
    ResignBm07Response convertEntityToBm07Response(ResignSessionContractEntity entity);
    ResignBm08Response convertEntityToBm08Response(ResignSessionContractEntity entity);

    Page<ResignBm08Response> findPageBm08(SearchDTO searchRequest);

    Page<ExportBM09Response> findBM09Page(SearchDTO searchRequest);

    FileDTO.FileResponse exportBm9(ExportBMRequest input);

    FileDTO.FileResponse exportBm3(ExportBMRequest input);

    Page<ExportBM03Response> findBM03Page(SearchDTO searchRequest);

    List<FileDTO.FileResponse> exportFilesForLaborVoffice2(ResignContractAddToVofficeLaborRequest request);
    List<FileDTO.FileResponse>exportFilesForProbationaryVoffice2(ResignContractAddToVofficeProbationaryRequest request);

}
