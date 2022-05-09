package com.viettel.hstd.service.imp;

import com.viettel.hstd.constant.ResignStatus;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ContractDTO;
import com.viettel.hstd.dto.hstd.ProbationaryContractDTO;
import com.viettel.hstd.entity.hstd.ContractEntity;
import com.viettel.hstd.entity.hstd.ProbationaryContractEntity;
import com.viettel.hstd.repository.hstd.ContractRepository;
import com.viettel.hstd.repository.hstd.ProbationaryContractRepository;
import com.viettel.hstd.service.inf.ProbationaryContractService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@Transactional
class ProbationaryContractServiceImpTest {

    @Autowired
    private ProbationaryContractService probationaryContractService;

    @Test
    void createContractFileFromEntity() {
//        ContractDTO.ContractExportRequest request = new ContractDTO.ContractExportRequest();
//        request.contractId = 1798L;
//        FileDTO.FileResponse fileResponse = probationaryContractService.createContractFileFromEntity(request);
//
//        System.out.println(fileResponse.filePath);
    }

    @Test
    void exportContract() {
//        ProbationaryContractDTO.ProbationaryExportContractRequest request = new ProbationaryContractDTO.ProbationaryExportContractRequest();
//        request.contractId = 1798L;
//        FileDTO.FileResponse fileResponse = probationaryContractService.exportContract(request);
//
//        System.out.println(fileResponse.filePath);
    }
}