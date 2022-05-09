package com.viettel.hstd.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class VOConfig {

    @Value("${app.voffice.ca_wsUrl}")
    public String ca_wsUrl;

    @Value("${app.voffice.ca_appCode}")
    public String ca_appCode;

    @Value("${app.voffice.ca_appPass}")
    public String ca_appPass;

    @Value("${app.voffice.ca_encrypt_key}")
    public String ca_encrypt_key;

    @Value("${app.voffice.ca_sender}")
    public String ca_sender;

    @Value("${app.voffice.userVoffice}")
    public String userVoffice;

    @Value("${app.voffice.passVoffice}")
    public String passVoffice;

    @Value("${app.voffice.docTypeId}")
    public Long docTypeId;

    @Value("${app.voffice.areaId}")
    public Long areaId;

}
