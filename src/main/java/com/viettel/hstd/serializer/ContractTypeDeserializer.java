package com.viettel.hstd.serializer;

import java.io.IOException;

        import com.fasterxml.jackson.core.JsonParser;
        import com.fasterxml.jackson.core.ObjectCodec;
        import com.fasterxml.jackson.databind.DeserializationContext;
        import com.fasterxml.jackson.databind.JsonDeserializer;
        import com.fasterxml.jackson.databind.node.IntNode;
        import com.viettel.hstd.constant.ContractType;

public class ContractTypeDeserializer extends JsonDeserializer<ContractType> {

    @Override
    public ContractType deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        ObjectCodec oc = jp.getCodec();
        IntNode node = oc.readTree(jp);
        int genderString = node.numberValue().intValue();

        return ContractType.of(genderString);
    }
}