package com.viettel.hstd.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.viettel.hstd.constant.ContractImportCategory;

import java.io.IOException;

public class ContractImportCategorySerializer extends JsonSerializer<ContractImportCategory> {

    @Override
    public void serialize(ContractImportCategory value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        final int genderInt = value.getValue();
        gen.writeNumber(genderInt);
    }
}
