package com.viettel.hstd.controller;

import com.viettel.hstd.constant.ResignStatus;
import com.viettel.hstd.entity.hstd.ContractEntity;
import com.viettel.hstd.repository.hstd.ContractRepository;
import com.viettel.hstd.service.inf.LaborContractService;
import com.viettel.voffice.ws_autosign.service.FileAttachTranfer;
import liquibase.pro.packaged.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VOfficeResultControllerTest {

    @Autowired
    VOfficeResultController controller;
    @Autowired
    LaborContractService laborContractService;

    @Autowired
    ContractRepository contractRepository;

    @Test
    void returnSignReult() {
        List<ContractEntity> contractEntities = contractRepository.getListContractByTransCode("hsdt_resignLaborContract__20220212165528");
        List<ContractEntity> contractEntityList = new ArrayList<>();

        if (!contractEntities.isEmpty()) {
            Set<Long> contractIdList = contractEntities.stream().map(ContractEntity::getContractId).collect(Collectors.toSet());
            laborContractService.turnTempContractToActualContract(contractIdList);
            contractRepository.updateResignStatusByContractIdSet(contractIdList, ResignStatus.NOT_IN_RESIGN_SESSION);
            List<FileAttachTranfer> fileAttachTranfers = new ArrayList<>();
            FileAttachTranfer fileAttachTranfer = new FileAttachTranfer();
            fileAttachTranfer.setFileName("Hop dong lao dong Hoàng Hùng Thái.pdf");
            fileAttachTranfers.add(fileAttachTranfer);
            ContractEntity contractEntity = contractEntities.stream().
                    filter(contract -> contract.getContractFileEncodePath().contains(fileAttachTranfer.getFileName())).
                    findFirst().orElse(null);

            System.out.println("DONE");
        }
    }

}