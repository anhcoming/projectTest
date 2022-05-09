package com.viettel.hstd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;

@Configuration
public class ValidatorConfiguration {

    @Bean(name = "customValidator")
    public Validator validator(){
        return Validation.buildDefaultValidatorFactory().getValidator();
    }
}
