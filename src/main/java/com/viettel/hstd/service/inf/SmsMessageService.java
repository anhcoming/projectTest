package com.viettel.hstd.service.inf;

public interface SmsMessageService {
    String sendSmsMessage(String phoneNumber, String message);
}
