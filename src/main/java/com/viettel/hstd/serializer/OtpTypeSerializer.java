package com.viettel.hstd.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
        import com.fasterxml.jackson.databind.JsonSerializer;
        import com.fasterxml.jackson.databind.SerializerProvider;
        import com.viettel.hstd.core.constant.FormatConstant;

        import java.io.IOException;
        import java.time.format.DateTimeFormatter;
        import com.viettel.hstd.constant.OtpType;


public class OtpTypeSerializer extends JsonSerializer<OtpType> {
    @Override
    public void serialize(OtpType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        final int otpTypeInt = value.getValue();
        gen.writeNumber(otpTypeInt);
    }
}
