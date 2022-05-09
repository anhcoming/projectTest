package com.viettel.hstd.service.imp;

import com.viettel.hstd.constant.ResignStatus;
import com.viettel.hstd.dto.hstd.ResignSessionDTO;
import com.viettel.hstd.entity.hstd.ContractEntity;
import com.viettel.hstd.entity.hstd.ResignSessionContractEntity;
import com.viettel.hstd.entity.hstd.ResignSessionEntity;
import com.viettel.hstd.repository.hstd.ContractRepository;
import com.viettel.hstd.repository.hstd.ResignSessionRepository;
import com.viettel.hstd.service.inf.ResignSessionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ResignSessionImpTest {
    @Autowired
    private ResignSessionService resignSessionService;

    @Autowired
    private ResignSessionRepository resignSessionRepository;

    @Autowired
    private ContractRepository contractRepository;

    private Long resignSessionId = 0L;
    private Long resignSessionContractId = 0L;
    private Long contractId = 0L;

//    @BeforeEach
//    void setup() {
//        ResignSessionEntity resignSessionEntity = new ResignSessionEntity();
//        ResignSessionContractEntity resignSessionContractEntity = new ResignSessionContractEntity();
//        resignSessionEntity.getResignSessionContractEntities().add(resignSessionContractEntity);
//        ContractEntity contractEntity = new ContractEntity();
//        resignSessionContractEntity.setContractEntity(contractEntity);
//
//        resignSessionEntity = resignSessionRepository.save(resignSessionEntity);
//        contractEntity = contractRepository.save(contractEntity);
//
//        resignSessionId = resignSessionEntity.getResignSessionId();
//        contractId = contractEntity.getContractId();
//    }

    @Test
    void exportBm() {
        System.out.println("Hi");

//        resignSessionService.exportBm07(808L);
//        resignSessionService.exportBm08(808L);

        System.out.println("Done");
    }

//    @Test
//    void updateResignStatusAndRelated() {
//
//        resignSessionService.updateResignStatusAndRelated(resignSessionId, ResignStatus.RECEIVED_BM_09_VOFFICE_AND_SUCCESS);
//
//        ResignSessionDTO.ResignSessionResponse response = resignSessionService.findOneById(resignSessionId);
//        Assertions.assertEquals(response.resignStatus, ResignStatus.RECEIVED_BM_09_VOFFICE_AND_SUCCESS);
//    }
}