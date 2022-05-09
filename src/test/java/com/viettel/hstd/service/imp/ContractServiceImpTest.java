package com.viettel.hstd.service.imp;

import com.viettel.hstd.constant.*;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.dto.hstd.ContractDTO;
import com.viettel.hstd.entity.hstd.ContractEntity;
import com.viettel.hstd.repository.hstd.ContractRepository;
import com.viettel.hstd.service.inf.ContractService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class ContractServiceImpTest {

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractRepository contractRepository;
    @Test
    void exportDanhSachNhanSu() {
//        contractService.exportDanhSachNhanSu();
    }

    @Test
    void findPage() {
//        SearchDTO request = new SearchDTO();
//        SearchDTO.SearchCriteria criteria =
//                new SearchDTO.SearchCriteria("resignSessionContractEntity.resignStatus", Operation.EQUAL, null, SearchType.NUMBER, false);
//        request.criteriaList.add(criteria);
//
//        criteria =
//                new SearchDTO.SearchCriteria("resignSessionContractEntity.resignSessionEntity.resignSessionId", Operation.EQUAL, 1162L, SearchType.NUMBER, false);
//        request.criteriaList.add(criteria);
//        criteria =
//                new SearchDTO.SearchCriteria("resignStatus", Operation.LESS, 1L, SearchType.NUMBER, true);
//        request.criteriaList.add(criteria);
//
//        criteria =
//                new SearchDTO.SearchCriteria("unitId", Operation.EQUAL, 9023081L, SearchType.NUMBER, true);
//        request.criteriaList.add(criteria);
//
//        criteria =
//                new SearchDTO.SearchCriteria("contractType", Operation.EQUAL, 5, SearchType.NUMBER, true);
//        request.criteriaList.add(criteria);
//
//        criteria =
//                new SearchDTO.SearchCriteria("expiredDate", Operation.GREATER_EQUAL, "12/02/2022", SearchType.DATE, true);
//        request.criteriaList.add(criteria);
//
//        criteria =
//                new SearchDTO.SearchCriteria("expiredDate", Operation.LESS_EQUAL, "28/02/2022", SearchType.DATE, true);
//        request.criteriaList.add(criteria);
//
//        Page<ContractDTO.ContractResponse> listContract = contractService.findPage(request);
//        if (!listContract.isEmpty()) {
//            for (ContractDTO.ContractResponse contract : listContract) {
//               if(contract.contractId.equals(5130L)){
//                   System.out.println(contract.toString());
//               }
//            }
//
//        }
    }

    @Test
    void resetLaborContract() {
        ContractEntity contractEntity = contractRepository.findById(13370L).orElse(null);
        if (contractEntity != null) {
            contractEntity.setResignStatus(ResignStatus.TEMP_CONTRACT_CREATE);
            contractEntity.setContractType(ContractType.LABOR_CONTRACT);
            contractEntity.setNewContractStatus(NewContractStatus.RECEIVED_FROM_VOFFICE);
            contractEntity.setSignatureFileEncodePath(null);
            contractEntity.setSignatureName(null);
            contractEntity.setSignedFileEncodePath(null);
            contractEntity.setSignedFile(null);
            contractEntity.setEmployeeSignedFile(null);
            contractEntity.setEmployeeSignedFileEncodePath(null);
            contractEntity.setIsActive(false);
            contractRepository.save(contractEntity);
        }
    }

    @Test
    void resetProbationContract() {
        ContractEntity contractEntity = contractRepository.findById(13370L).orElse(null);
        if (contractEntity != null) {
            contractEntity.setContractType(ContractType.PROBATIONARY_CONTRACT);
            contractEntity.setNewContractStatus(NewContractStatus.RECEIVED_CODE_FROM_VHR);
            contractEntity.setResignStatus(ResignStatus.NOT_IN_RESIGN_SESSION);
            contractEntity.setSignatureFileEncodePath(null);
            contractEntity.setSignatureName(null);
            contractEntity.setSignedFileEncodePath(null);
            contractEntity.setSignedFile(null);
            contractEntity.setEmployeeSignedFile(null);
            contractEntity.setEmployeeSignedFileEncodePath(null);
            contractRepository.save(contractEntity);
        }
    }
}