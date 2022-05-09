package com.viettel.hstd.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class Message {
    @Autowired
    MessageSource messageSource;

    public String getMessage(String key){
        return messageSource.getMessage(key, null, Locale.ENGLISH);
    }

    public String getMessage(String key, Object[] value){
        return messageSource.getMessage(key, value, Locale.ENGLISH);
    }
}
