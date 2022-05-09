package com.viettel.hstd.dto.hstd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SmsMessageConfig {
    private String user;
    private String pass;
    private String CPCode;
    private String ServiceID;
    private String url;
}

