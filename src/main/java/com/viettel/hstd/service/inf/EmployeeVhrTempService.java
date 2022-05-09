package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.hstd.EmployeeVhrTempDTO.*;

public interface EmployeeVhrTempService extends CRUDService<EmployeeVhrTempRequest, EmployeeVhrTempResponse, Long> {
    EmployeeVhrTempResponse findByInterviewCvId(Long interviewCvId);

    int lock(EmployeeVhrTempLocking data);

    int unlock(EmployeeVhrTempLocking data);

    /**
     * Xac nhan day du ho so
     *
     * @param id
     * @return
     */
    int approval(Long id);

    /**
     * Ho so chua day du
     *
     * @param id
     * @return
     */
    int reject(Long id);

    EmployeeVhrTempResponse getProfile();

    boolean sendRequestToVHRToReceivedEmployeeCode(SendVhrCodeRequest request);

    EmployeeVhrAttachmentResponse getAttachment(Long employeeVhrTempId);
}
