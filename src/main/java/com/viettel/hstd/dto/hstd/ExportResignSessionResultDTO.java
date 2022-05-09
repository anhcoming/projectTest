package com.viettel.hstd.dto.hstd;

import java.util.ArrayList;
import java.util.List;

public class ExportResignSessionResultDTO {
    public Long id;
    public List<ResignSessionContractDTO.ExportBM09Response> exportBM09ResponseList = new ArrayList<>();
}
