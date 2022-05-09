package com.viettel.hstd.service.imp;

import com.viettel.hstd.constant.VOfficeSignType;
import com.viettel.hstd.dto.hstd.VOfficeSignDTO;
import com.viettel.hstd.dto.hstd.VOfficeSignDataDTO;
import com.viettel.hstd.repository.hstd.VoLogRepository;
import com.viettel.hstd.service.inf.VoLogService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VoLogServiceImpTest {

    @Autowired
    private VoLogService voLogService;

    @Autowired
    private VoLogRepository voLogRepository;

    @Test
    void logVOSent() {
        VOfficeSignDTO vOfficeSignDTO = new VOfficeSignDTO();
        vOfficeSignDTO.type = VOfficeSignType.RESIGN_FORM_03;

        List<VOfficeSignDataDTO> vOfficeSignDataDTOList = new ArrayList<>();
        VOfficeSignDataDTO vOfficeSignDataDTO = new VOfficeSignDataDTO();
        vOfficeSignDataDTO.id = 0L;
        vOfficeSignDataDTO.filePath = "filePath";
        vOfficeSignDataDTO.data = new HashMap<>();
        String data = "";
        for (int i = 0; i < 1000; i++) {
            data += "A3BDC7267AED9289283BDA82";
        }
        vOfficeSignDataDTO.data.put("testData", data);
        vOfficeSignDataDTOList.add(vOfficeSignDataDTO);
        vOfficeSignDTO.signData = vOfficeSignDataDTOList;

        voLogService.logVOSent(vOfficeSignDTO);

        Assertions.assertTrue(true);
    }

    @Test
    void logVOReceive() {
        String data = "";
        for (int i = 0; i < 1000; i++) {
            data += "A3BDC7267AED9289283BDA82";
        }
        voLogService.logVOReceive(data);

        Assertions.assertTrue(true);
    }
}