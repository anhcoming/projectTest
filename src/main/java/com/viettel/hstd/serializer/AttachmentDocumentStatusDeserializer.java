package com.viettel.hstd.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import com.viettel.hstd.constant.AttachmentDocumentStatus;
import com.viettel.hstd.constant.ContractType;

import java.io.IOException;

public class AttachmentDocumentStatusDeserializer  extends JsonDeserializer<AttachmentDocumentStatus> {

    @Override
    public AttachmentDocumentStatus deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        ObjectCodec oc = jp.getCodec();
        IntNode node = oc.readTree(jp);
        int genderString = node.numberValue().intValue();

        return AttachmentDocumentStatus.of(genderString);
    }
}
