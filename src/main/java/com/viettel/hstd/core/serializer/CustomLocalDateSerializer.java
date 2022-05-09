package com.viettel.hstd.core.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
        import com.fasterxml.jackson.databind.JsonSerializer;
        import com.fasterxml.jackson.databind.SerializerProvider;
        import com.viettel.hstd.core.constant.FormatConstant;

        import java.io.IOException;
        import java.time.LocalDate;
        import java.time.format.DateTimeFormatter;

public class CustomLocalDateSerializer extends JsonSerializer<LocalDate> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FormatConstant.LOCAL_DATE_FORMAT);

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        final String dateString = value.format(this.formatter);
        gen.writeString(dateString);
    }
}