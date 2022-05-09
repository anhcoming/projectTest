package com.viettel.hstd.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TmpMapper {
    @Autowired
    public ObjectMapper objectMapper;
    @Autowired
    public ModelMapper modelMapper;

    @SuppressWarnings("unchecked")
    public <T> T map(Object objectFrom, T objectTo) {
        Class<T> clazz = (Class<T>) objectTo.getClass();
        T tmp = objectMapper.convertValue(objectFrom, clazz);
        modelMapper.map(tmp, objectTo);
        return objectTo;
    }

    public <T> T convertValue(Object fromValue, Class<T> toValueType) throws IllegalArgumentException {
        return this.objectMapper.convertValue(fromValue, toValueType);
    }
}
