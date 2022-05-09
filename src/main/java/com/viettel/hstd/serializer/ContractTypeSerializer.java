package com.viettel.hstd.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
        import com.fasterxml.jackson.databind.JsonSerializer;
        import com.fasterxml.jackson.databind.SerializerProvider;
        import com.viettel.hstd.core.constant.FormatConstant;

        import java.io.IOException;
        import java.time.format.DateTimeFormatter;
        import com.viettel.hstd.constant.ContractType;


public class ContractTypeSerializer extends JsonSerializer<ContractType> {
    @Override
    public void serialize(ContractType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        final int contractTypeInt = value.getValue();
        gen.writeNumber(contractTypeInt);
    }
}