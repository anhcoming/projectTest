package com.viettel.hstd.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
        import com.fasterxml.jackson.databind.JsonSerializer;
        import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

import com.viettel.hstd.constant.NewContractStatus;


public class NewContractStatusSerializer extends JsonSerializer<NewContractStatus> {
    @Override
    public void serialize(NewContractStatus value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        final int genderInt = value.getValue();
        gen.writeNumber(genderInt);
    }
}