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
import com.viettel.hstd.constant.SearchType;
import com.viettel.hstd.core.constant.FormatConstant;

public class SearchTypeDeserializer extends JsonDeserializer<SearchType> {

    @Override
    public SearchType deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException {
        ObjectCodec oc = jp.getCodec();
        IntNode node = oc.readTree(jp);
        int genderString = node.numberValue().intValue();

        return SearchType.of(genderString);
    }
}
