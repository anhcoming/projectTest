package com.viettel.hstd.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
        import com.fasterxml.jackson.databind.JsonSerializer;
        import com.fasterxml.jackson.databind.SerializerProvider;
        import com.viettel.hstd.core.constant.FormatConstant;

        import java.io.IOException;
        import java.time.format.DateTimeFormatter;
        import com.viettel.hstd.constant.VOfficeSignType;


public class VOfficeSignTypeSerializer extends JsonSerializer<VOfficeSignType> {
    @Override
    public void serialize(VOfficeSignType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        final int otpTypeInt = value.getValue();
        gen.writeNumber(otpTypeInt);
    }
}