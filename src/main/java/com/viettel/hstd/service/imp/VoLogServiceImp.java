package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.viettel.hstd.dto.hstd.VOfficeSignDTO;
import com.viettel.hstd.entity.hstd.VoLogEntity;
import com.viettel.hstd.repository.hstd.VoLogRepository;
import com.viettel.hstd.service.inf.VoLogService;
import org.springframework.stereotype.Service;

@Service
public class VoLogServiceImp implements VoLogService {

    private final VoLogRepository voLogRepository;

    public VoLogServiceImp(VoLogRepository voLogRepository) {
        this.voLogRepository = voLogRepository;
    }

    @Override
    public void logVOSent(VOfficeSignDTO request) {
        VoLogEntity voLogEntity = new VoLogEntity();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            String json = ow.writeValueAsString(request);
            voLogEntity.setContent(json);
            voLogEntity.setType(1);
            voLogRepository.save(voLogEntity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void logVOReceive(String request) {
        VoLogEntity voLogEntity = new VoLogEntity();
        voLogEntity.setContent(request);
        voLogEntity.setType(2);
        voLogRepository.save(voLogEntity);
    }
}
