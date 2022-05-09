package com.viettel.hstd.service.imp;

import com.viettel.hstd.constant.Operation;
import com.viettel.hstd.constant.SearchType;
import com.viettel.hstd.controller.ResignSessionContractController;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ResignSessionContractDTO;
import com.viettel.hstd.service.inf.ResignSessionContractService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
@Profile("real-local")
class ResignSessionContractImpTest {
    @Autowired
    ResignSessionContractService resignSessionContractService;

    @Test
    void exportFilesForLaborVoffice2() {
    }

    @Test
    void exportFilesForProbationaryVoffice2() {
//        ResignSessionContractDTO.ResignContractAddToVofficeProbationaryRequest request =
//                new ResignSessionContractDTO.ResignContractAddToVofficeProbationaryRequest();
//        request.isAll = true;
//        request.startDate = LocalDate.of(2022, 2, 12);
//        request.endDate = LocalDate.of(2022, 2, 28);
//        request.resignIdList = new ArrayList<>();
//        List<FileDTO.FileResponse> fileResponses = resignSessionContractService.exportFilesForProbationaryVoffice2(request);
//        System.out.println("Done");
    }

    @Test
    void findBM03Page() {
    }
}