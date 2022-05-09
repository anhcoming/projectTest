package com.viettel.hstd.service.imp;

import com.viettel.hstd.dto.hstd.RecruitmentProgressDTO;
import com.viettel.hstd.repository.hstd.RecruitmentProgressEmployeeRepository;
import com.viettel.hstd.service.inf.RecruitmentProgressEmployeeService;
import com.viettel.hstd.service.mapper.RecruitmentProgressConverter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentProgressEmployeeServiceImpl implements RecruitmentProgressEmployeeService {

    private final RecruitmentProgressEmployeeRepository recruitmentProgressEmployeeRepository;

    private final RecruitmentProgressConverter recruitmentProgressConverter;


    @Override
    @Transactional(readOnly = true)
    public Map<Pair<String, String>, List<RecruitmentProgressDTO.EmailResponse>> getEmailAlertPerEmployee() {

        return recruitmentProgressEmployeeRepository.findAllStream(LocalDate.now())
                .map(recruitmentProgressConverter::emailProjectionToEmailResponse)
                .collect(Collectors.groupingBy(emailResponse -> Pair.of(emailResponse.getEmployeeName(), emailResponse.getEmployeeEmail())));
    }
}
