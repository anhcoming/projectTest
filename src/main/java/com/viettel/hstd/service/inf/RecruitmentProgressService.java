package com.viettel.hstd.service.inf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.viettel.hstd.dto.hstd.RecruitmentProgressDTO;
import com.viettel.hstd.entity.hstd.RecruitmentProgressEntity;
import org.springframework.data.domain.Page;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface RecruitmentProgressService {
    RecruitmentProgressEntity findById(Long id);

    RecruitmentProgressDTO.SingleResponse getOne(Long id);

    void save(RecruitmentProgressEntity entity);

    Page<RecruitmentProgressDTO.Response> search(RecruitmentProgressDTO.SearchCriteria criteria);

    void deleteById(Long id);

    void create(RecruitmentProgressDTO.RecruitmentProgressRequest request);

    void update(RecruitmentProgressDTO.RecruitmentProgressRequest request, Long id);

    void sendEmailAlert() throws MessagingException;

    Long importFromFile(String fileUrl, String fileTitle) throws IOException;

    void saveImportHistoryFailed(RecruitmentProgressDTO.Event event, List<RecruitmentProgressDTO.RecruitmentProgressExcelRow> rowsFormatErrors) throws JsonProcessingException;

    void saveAll(RecruitmentProgressDTO.Event event, List<RecruitmentProgressDTO.RecruitmentProgressRequest> requests);
}
