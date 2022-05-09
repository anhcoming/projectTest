package com.viettel.hstd.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.viettel.hstd.core.constant.FormatConstant;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import com.viettel.hstd.constant.SearchType;


public class SearchTypeSerializer extends JsonSerializer<SearchType> {
    @Override
    public void serialize(SearchType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        final int genderInt = value.getValue();
        gen.writeNumber(genderInt);
    }
}
