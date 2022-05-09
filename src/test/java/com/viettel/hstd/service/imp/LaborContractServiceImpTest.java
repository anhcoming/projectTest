package com.viettel.hstd.service.imp;

import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ContractDTO;
import com.viettel.hstd.service.inf.LaborContractService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class LaborContractServiceImpTest {
    @Autowired
    LaborContractService laborContractService;

    @Test
    void exportContract() {
//        ContractDTO.ContractExportRequest request = new ContractDTO.ContractExportRequest();
//        request.contractId = 958L;
//        FileDTO.FileResponse fileResponse = laborContractService.createTempContract(request);
//
//        System.out.println(fileResponse.filePath);
    }

    @Test
    void delete() {
        laborContractService.delete(10588L);
    }
}