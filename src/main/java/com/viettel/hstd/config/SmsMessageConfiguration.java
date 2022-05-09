package com.viettel.hstd.config;

import com.viettel.hstd.dto.hstd.SmsMessageConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsMessageConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "sms")
    public SmsMessageConfig smsMessageConfig() {
        return new SmsMessageConfig();
    }
}