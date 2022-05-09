package com.viettel.hstd.service.imp;

import com.viettel.hstd.dto.hstd.RecruitmentProgressDTO;
import com.viettel.hstd.repository.hstd.EmailAlertRecipientRepository;
import com.viettel.hstd.service.inf.EmailAlertRecipientService;
import com.viettel.hstd.service.mapper.RecruitmentProgressConverter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmailAlertRecipientServiceImpl implements EmailAlertRecipientService {

    private final EmailAlertRecipientRepository emailAlertRecipientRepository;

    private final RecruitmentProgressConverter recruitmentProgressConverter;

    @Override
    @Transactional(readOnly = true)
    public Map<Pair<String, String>, List<RecruitmentProgressDTO.EmailResponse>> getEmailAlertRecipientResponse() {
//        return emailAlertRecipientRepository.getEmailResponseForLeader(LocalDate.now())
//                .map(recruitmentProgressConverter::emailProjectionToEmailResponse)
//                .collect(Collectors.groupingBy(emailResponse -> Pair.of(emailResponse.getRecipient(), emailResponse.getEmployeeEmail())));
        return new HashMap<>();
    }
}
