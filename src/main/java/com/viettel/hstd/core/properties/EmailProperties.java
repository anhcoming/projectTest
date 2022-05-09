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
@EnableConfigurationProperties
@ConfigurationProperties(
        prefix = "app.send-email"
)
public class EmailProperties {
    private String email;
    private String password;
    private String protocol;
    private String host;
    private Integer port;
    private Boolean isActive;
    private Boolean isSsl;
}
