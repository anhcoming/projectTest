package com.viettel.hstd.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
        import com.fasterxml.jackson.databind.JsonSerializer;
        import com.fasterxml.jackson.databind.SerializerProvider;
        import com.viettel.hstd.core.constant.FormatConstant;

        import java.io.IOException;
        import java.time.format.DateTimeFormatter;
        import com.viettel.hstd.constant.InterviewResult;

public class InterviewResultSerializer extends JsonSerializer<InterviewResult> {
    @Override
    public void serialize(InterviewResult value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        final int interviewResultInt = value.getValue();
        gen.writeNumber(interviewResultInt);
    }
}