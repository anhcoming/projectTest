package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.hstd.DocumentTypeDTO;
import com.viettel.hstd.dto.hstd.RecruitProfileAttachmentDTO.*;

import java.util.ArrayList;
import java.util.List;

public interface RecruitProfileAttachmentService extends CRUDService<RecruitProfileAttachmentRequest, RecruitProfileAttachmentResponse, Long> {
    List<RecruitProfileAttachmentResponse> findByUserId(Long userId);

    RecruitProfileAttachmentResponse approval(Long recProfileAttachmentId);

    RecruitProfileAttachmentResponse reject(Long recProfileAttachmentId);
}
