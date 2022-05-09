package com.viettel.hstd.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
        import com.fasterxml.jackson.databind.JsonSerializer;
        import com.fasterxml.jackson.databind.SerializerProvider;
        import com.viettel.hstd.core.constant.FormatConstant;

        import java.io.IOException;
        import java.time.format.DateTimeFormatter;
        import com.viettel.hstd.constant.KpiGrade;


public class KpiGradeSerializer extends JsonSerializer<KpiGrade> {
    @Override
    public void serialize(KpiGrade value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        final String genderInt = value.getValue();
        gen.writeString(genderInt);
    }
}