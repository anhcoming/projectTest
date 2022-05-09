package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.hstd.EmployeeInterviewSessionDTO.*;
import com.viettel.hstd.dto.vps.EmployeeVhrDTO;

import java.util.List;

public interface EmployeeInterviewSessionService extends CRUDService<EmployeeInterviewSessionRequest, EmployeeInterviewSessionResponse, Long> {
    Boolean addOrUpdateListEmployee(Long interviewId, List<EmployeeInterviewSessionRequest> lstEmp);

    List<EmployeeInterviewSessionResponse> findByInterviewSessionId(Long id);
}
