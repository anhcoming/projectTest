package com.viettel.hstd.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.viettel.hstd.constant.AttachmentDocumentStatus;

import java.io.IOException;

public class AttachmentDocumentStatusSerializer extends JsonSerializer<AttachmentDocumentStatus> {
    @Override
    public void serialize(AttachmentDocumentStatus value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        final int attachmentDocumentStatus = value.getValue();
        gen.writeNumber(attachmentDocumentStatus);
    }
}
