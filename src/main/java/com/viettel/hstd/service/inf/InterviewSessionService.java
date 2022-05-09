package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.InterviewSessionDTO.*;

import java.time.LocalDateTime;

public interface InterviewSessionService extends CRUDService<InterviewSessionRequest, InterviewSessionResponse, Long> {
    Boolean checkExisted(Long positionId, LocalDateTime startDate, LocalDateTime endDate);

    void updateCvThatInterviewed(Long interviewSessionId);

    FileDTO.FileResponse exportExcel(InterviewSessionResponse request);
}
