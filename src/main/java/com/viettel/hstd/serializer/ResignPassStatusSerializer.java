package com.viettel.hstd.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
    import com.fasterxml.jackson.databind.JsonSerializer;
    import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

import com.viettel.hstd.constant.ResignPassStatus;


public class ResignPassStatusSerializer extends JsonSerializer<ResignPassStatus> {
    @Override
    public void serialize(ResignPassStatus value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        final int genderInt = value.getValue();
        gen.writeNumber(genderInt);
    }
}
