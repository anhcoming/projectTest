package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.hstd.RecruiteeAccountDTO.*;
import com.viettel.hstd.entity.hstd.InterviewSessionCvEntity;
import com.viettel.hstd.entity.hstd.RecruiteeAccountEntity;

public interface RecruiteeAccountService extends CRUDService<RecruiteeAccountRequest, RecruiteeAccountResponse, Long> {
    RecruiteeAccountResponse validate(String account, String password);

    RecruiteeAccountResponse createAccount(Long interviewCvId);
}
