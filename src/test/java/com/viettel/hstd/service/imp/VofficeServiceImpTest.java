package com.viettel.hstd.service.imp;

import com.google.gson.Gson;
import com.viettel.hstd.constant.VOfficeSignType;
import com.viettel.hstd.controller.VOfficeSignController;
import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.dto.hstd.VOfficeSignDTO;
import com.viettel.hstd.dto.hstd.VOfficeSignDataDTO;
import com.viettel.hstd.entity.hstd.VoLogEntity;
import com.viettel.hstd.repository.hstd.VoLogRepository;
import com.viettel.hstd.repository.hstd.VoSignRepository;
import com.viettel.hstd.service.inf.VofficeService;
import com.viettel.voffice.ws_autosign.service.FileAttachTranfer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("real-local")
class VofficeServiceImpTest {

    @Autowired
    private VofficeService vofficeService;

    @Autowired
    private VoLogRepository voLogRepository;

    @Autowired
    private VOfficeSignController vOfficeSignController;

    @Test
    void sentVoffice() {
        VoLogEntity voLogEntity = voLogRepository.findById(861L).orElse(null);
        if (voLogEntity != null) {
            String content = voLogEntity.getContent();
            VOfficeSignDTO vOfficeSignDTO = new Gson().fromJson(content, VOfficeSignDTO.class);
            BaseResponse<String> result = vOfficeSignController.returnSignReult(vOfficeSignDTO);
        }
    }

    @Test
    void testSentVoffice() {
    }

    @Test
    void addAttachments() {
        VoLogEntity voLogEntity = voLogRepository.findById(861L).orElse(null);
        if (voLogEntity != null) {
            String content = voLogEntity.getContent();
            VOfficeSignDTO vOfficeSignDTO = new Gson().fromJson(content, VOfficeSignDTO.class);
            List<FileAttachTranfer> fileAttachTranfers = vofficeService.addAttachments(null, vOfficeSignDTO.attachmentList);
            System.out.println(new Gson().toJson(fileAttachTranfers));
        }
    }

    @Test
    void handleSevAllowance() {
    }
}