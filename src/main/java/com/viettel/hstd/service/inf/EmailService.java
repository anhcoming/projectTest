package com.viettel.hstd.service.inf;

import com.viettel.hstd.dto.hstd.EmailDTO;
import com.viettel.hstd.dto.hstd.MultipleEmailDTO;

import javax.mail.Multipart;
import java.util.ArrayList;

public interface EmailService {
    void sendMessage(String subject, Multipart multipart, String email);

    Boolean sendMessage(EmailDTO model);

    Boolean sendOffer(MultipleEmailDTO lstEmail);

    Boolean sendNotify(EmailDTO model);
}
