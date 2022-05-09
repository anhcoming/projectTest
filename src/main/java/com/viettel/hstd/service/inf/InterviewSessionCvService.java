package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.ExportInterviewResultDTO;
import com.viettel.hstd.dto.hstd.InterviewSessionCvDTO.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


public interface InterviewSessionCvService extends CRUDService<InterviewSessionCvRequest, InterviewSessionCvResponse, Long> {
    Boolean addOrUpdateListCv(Long interviewId, List<InterviewSessionCvRequest> lstEmp);

    List<InterviewSessionCvResponse> findByCvId(Long cvId);

    Boolean sendOffer(ArrayList<Long> ids, Long templateId);

    List<InterviewSessionCvResponse> findByInterviewSessionId(Long id);

    String getEmailContent(Long cvId, Long templateId);

    FileDTO.FileResponse exportExcel(SearchDTO searchDTO);

    InterviewSessionCvResponse findByTransCode(String transCode);

    InterviewSessionCvResponse updateData(Long id, InterviewSessionCvResponse response);

    FileDTO.FileResponse downloadVofficeSignedFile(Long id);

    InterviewSessionCvResponse updateInterviewReportFile(String fileName, Long id);

    FileDTO.FileResponse exportInterviewResult(ExportInterviewResultDTO input);


    FileDTO.FileResponse downloadInterviewResult(Long id);

    /*
    Update sth
     */
    InterviewSessionCvResponse updateSignedFile(String fileName, Long id);

    void updateNewFileAfterVO(Long id, String signedFile, String interviewReportFile);
}
