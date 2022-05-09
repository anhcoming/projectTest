package com.viettel.hstd.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
    import com.fasterxml.jackson.databind.JsonSerializer;
    import com.fasterxml.jackson.databind.SerializerProvider;

    import java.io.IOException;

    import com.viettel.hstd.constant.ResignVofficeStatus;


public class ResignVofficeStatusSerializer extends JsonSerializer<ResignVofficeStatus> {
    @Override
    public void serialize(ResignVofficeStatus value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        final int genderInt = value.getValue();
        gen.writeNumber(genderInt);
    }
}
