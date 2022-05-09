package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
    import javax.persistence.Converter;
    import java.util.stream.Stream;

@Converter(autoApply = true)
public class OperationConverter implements AttributeConverter<Operation, String> {

    @Override
    public String convertToDatabaseColumn(Operation gender) {
        if (gender == null) {
            return ":";
        }
        return gender.getValue();
    }

    @Override
    public Operation convertToEntityAttribute(String value) {
        if (value == null) {
            return Operation.LIKE;
        }

        return Stream.of(Operation.values())
            .filter(c -> c.getValue() == value)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}

