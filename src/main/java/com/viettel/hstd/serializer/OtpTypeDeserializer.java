package com.viettel.hstd.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import com.viettel.hstd.constant.OtpType;

public class OtpTypeDeserializer extends JsonDeserializer<OtpType> {

    @Override
    public OtpType deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        ObjectCodec oc = jp.getCodec();
        IntNode node = oc.readTree(jp);
        int otpTypeString = node.numberValue().intValue();

        return OtpType.of(otpTypeString);
    }
}
