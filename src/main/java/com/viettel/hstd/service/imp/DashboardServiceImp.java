package com.viettel.hstd.service.imp;

import com.viettel.hstd.constant.TerminateStatusConstant;
import com.viettel.hstd.dto.hstd.DashboardDTO.*;
import com.viettel.hstd.repository.hstd.TerminateContractRepository;
import com.viettel.hstd.security.AuthenticationFacade;
import com.viettel.hstd.security.sso.SSoResponse;
import com.viettel.hstd.service.inf.DashboardService;
import com.viettel.hstd.service.inf.TerminateContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class DashboardServiceImp implements DashboardService {

    @Autowired
    TerminateContractService terminateContractService;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private TerminateContractRepository terminateContractRepository;

    @Override
    public DashboardTopResponse getDashboardTop() {
        DashboardTopResponse response = new DashboardTopResponse();

        SSoResponse ssoResponse = (SSoResponse) authenticationFacade.getAuthentication().getPrincipal();
        Set<Integer> statusSet = new HashSet<>();
        statusSet.add(TerminateStatusConstant.pending);
        response.numResignationHaventVOYet = terminateContractRepository.countByManagerIdAndStatusIn(ssoResponse.getId(), statusSet);

        return response;
    }
}
