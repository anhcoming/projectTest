package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.FileDTO;
import com.viettel.hstd.dto.hstd.CvDTO;
import com.viettel.hstd.dto.hstd.EmailConfigDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CvService extends CRUDService<CvDTO.CvRequest, CvDTO.CvResponse, Long> {
    List<CvDTO.CVExcelResponse> importExcel(List<CvDTO.CvRequest> request);

    Boolean updateInterviewState(Long id, Boolean interviewState);
//    FileDTO.FileResponse importExcel(MultipartFile file);


}
