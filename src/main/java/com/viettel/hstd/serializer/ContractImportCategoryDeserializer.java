package com.viettel.hstd.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import com.viettel.hstd.constant.ContractImportCategory;

import java.io.IOException;

public class ContractImportCategoryDeserializer extends JsonDeserializer<ContractImportCategory> {
    @Override
    public ContractImportCategory deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec oc = p.getCodec();
        IntNode node = oc.readTree(p);
        int genderString = node.numberValue().intValue();

        return ContractImportCategory.of(genderString);
    }
}
