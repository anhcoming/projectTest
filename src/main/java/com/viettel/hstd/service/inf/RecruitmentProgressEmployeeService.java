package com.viettel.hstd.service.inf;

import com.viettel.hstd.dto.hstd.RecruitmentProgressDTO;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public interface RecruitmentProgressEmployeeService {
    Map<Pair<String, String>, List<RecruitmentProgressDTO.EmailResponse>> getEmailAlertPerEmployee();
}
