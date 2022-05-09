package com.viettel.hstd.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
        import com.fasterxml.jackson.databind.JsonSerializer;
        import com.fasterxml.jackson.databind.SerializerProvider;
        import com.viettel.hstd.core.constant.FormatConstant;

        import java.io.IOException;
        import java.time.format.DateTimeFormatter;
        import com.viettel.hstd.constant.DecreaseStatus;


public class DecreaseStatusSerializer extends JsonSerializer<DecreaseStatus> {
    @Override
    public void serialize(DecreaseStatus value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        final int insuranceStatusInt = value.getValue();
        gen.writeNumber(insuranceStatusInt);
    }
}