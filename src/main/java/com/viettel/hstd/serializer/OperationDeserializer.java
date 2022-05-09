package com.viettel.hstd.serializer;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.viettel.hstd.constant.Operation;
import com.viettel.hstd.core.constant.FormatConstant;

public class OperationDeserializer extends JsonDeserializer<Operation> {

    @Override
    public Operation deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException {
        ObjectCodec oc = jp.getCodec();
        TextNode node = oc.readTree(jp);
        String genderString = node.textValue();

        return Operation.of(genderString);
    }
}
