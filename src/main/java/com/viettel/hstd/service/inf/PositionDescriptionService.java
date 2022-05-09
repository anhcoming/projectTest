package com.viettel.hstd.service.inf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.viettel.hstd.dto.hstd.PositionDescriptionDTO;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface PositionDescriptionService {

    Long importExcel(String importFileTitle, String importFileUrl, String attachmentFileUrl) throws IOException;

    Page<PositionDescriptionDTO.Response> search(PositionDescriptionDTO.SearchCriteria criteria);

    void deleteById(Long id);

    void saveImportHistoryFailed(PositionDescriptionDTO.Event event, List<PositionDescriptionDTO.PositionDescriptionExcelRow> rowsFormatErrors) throws JsonProcessingException;

    void saveBatch(PositionDescriptionDTO.Event event, List<PositionDescriptionDTO.Request> requests);

    void save(PositionDescriptionDTO.Request request);

    PositionDescriptionDTO.SingleResponse findById(Long id);

    void update(Long id, PositionDescriptionDTO.Request request);
}
