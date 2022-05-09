package com.viettel.hstd.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
    import com.fasterxml.jackson.databind.JsonSerializer;
    import com.fasterxml.jackson.databind.SerializerProvider;
    import com.viettel.hstd.core.constant.FormatConstant;

    import java.io.IOException;
    import java.time.format.DateTimeFormatter;
    import com.viettel.hstd.constant.ResignStatus;


public class ResignStatusSerializer extends JsonSerializer<ResignStatus> {
    @Override
    public void serialize(ResignStatus value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        final int genderInt = value.getValue();
        gen.writeNumber(genderInt);
    }
}
