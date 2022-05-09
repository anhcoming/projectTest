package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.hstd.InsuranceTerminateDTO.*;

import java.util.List;
import java.util.Set;

public interface InsuranceTerminateService extends CRUDService<InsuranceTerminateRequest, InsuranceTerminateResponse, Long> {
    void addContractToInsuranceSession(Set<Long> contractIds, Long insuranceSessionId);
    void removeContractToInsuranceSession(Set<Long> contractIds, Long insuranceSessionId);

    void startPreparingDocumentAndDecreasingAnnounce(List<Long> insuranceSessionIdSet);
    void failDecreasingAnnounce(Set<Long> insuranceSessionIdSet);
    void finishDecreasingAnnounce(Set<Long> insuranceSessionIdSet);
    void finishPreparingDocument(Set<Long> insuranceSessionIdSet);
    void startSentToBHXH(Set<Long> insuranceSessionIdSet);
    void failBHXH(Set<Long> insuranceSessionIdSet);
    void finishBHXH(Set<Long> insuranceSessionIdSet);
}
