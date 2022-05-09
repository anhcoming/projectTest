package com.viettel.hstd.service.imp;

import com.viettel.hstd.dto.hstd.ResignSessionContractDTO;
import com.viettel.hstd.entity.hstd.ResignSessionContractEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.ResignSessionContractRepository;
import com.viettel.hstd.service.inf.ResignSessionContractService;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForArray;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ResignSessionContractServiceTest {

    @Autowired
    private ResignSessionContractService resignSessionContractService;
    @Autowired
    private ResignSessionContractRepository resignSessionContractRepository;

    @Test
    @DisplayName("get bm07")
    @EnabledIf(expression = "#{environment.acceptsProfiles('dev')}", loadContext = true)
    void getBm07() {
        ResignSessionContractEntity resignContractEntity = resignSessionContractRepository.findById(801L)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đợt tái ký"));
        ResignSessionContractDTO.ResignBm07Response response = resignSessionContractService.convertEntityToBm07Response(resignContractEntity);

        System.out.println("Done");
    }

}