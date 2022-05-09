package com.viettel.hstd.serializer;

import java.io.IOException;
        import java.time.format.DateTimeFormatter;

        import com.fasterxml.jackson.core.JsonParser;
        import com.fasterxml.jackson.core.ObjectCodec;
        import com.fasterxml.jackson.databind.DeserializationContext;
        import com.fasterxml.jackson.databind.JsonDeserializer;
        import com.fasterxml.jackson.databind.JsonNode;
        import com.fasterxml.jackson.databind.node.IntNode;
        import com.fasterxml.jackson.databind.node.TextNode;
        import com.viettel.hstd.constant.InsuranceStatus;
        import com.viettel.hstd.core.constant.FormatConstant;

public class InsuranceStatusDeserializer extends JsonDeserializer<InsuranceStatus> {

    @Override
    public InsuranceStatus deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        ObjectCodec oc = jp.getCodec();
        IntNode node = oc.readTree(jp);
        int insuranceStatusString = node.numberValue().intValue();

        return InsuranceStatus.of(insuranceStatusString);
    }
}