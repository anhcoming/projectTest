package com.viettel.hstd.service.inf;

import com.viettel.hstd.dto.hstd.PositionDescriptionRecipientDTO;
import org.apache.commons.lang3.tuple.Pair;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Map;

public interface PositionDescriptionRecipientService {
    List<PositionDescriptionRecipientDTO.Response> getRecipientsByPositionDescriptionId(Long positionDescriptionId);

    Map<Pair<String, String>, List<PositionDescriptionRecipientDTO.EmailResponse>> getEmailAlertPerEmployee();

    void sendEmailAlert() throws MessagingException;

    void sendMail(String template, Map<Pair<String, String>, List<PositionDescriptionRecipientDTO.EmailResponse>> map) throws MessagingException;
}
