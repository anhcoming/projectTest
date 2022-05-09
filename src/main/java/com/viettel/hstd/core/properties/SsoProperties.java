package com.viettel.hstd.core.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EnableConfigurationProperties(SsoProperties.class)
@ConfigurationProperties(
        prefix = "app.sso"
)
public class SsoProperties {
    private String loginUrl;
    private String logoutUrl;
    private String validateUrl;
    private String vpsServiceUrl;
    private String passportServiceUrl;
    private String loginMethod;
    private String domainCode;
    private String service;
    private String errorUrl;
    private String allowUrl;
    private Boolean useModifyHeader;
    private Boolean frondEnd;
}
