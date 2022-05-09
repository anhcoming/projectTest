package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
        import javax.persistence.Converter;
        import java.util.stream.Stream;

@Converter(autoApply = true)
public class ResignTypeConverter implements AttributeConverter<ResignType, Long> {

    @Override
    public Long convertToDatabaseColumn(ResignType resignType) {
        if (resignType == null) {
            return 1L;
        }
        return (long) resignType.getValue();
    }

    @Override
    public ResignType convertToEntityAttribute(Long value) {
        if (value == null) {
            return ResignType.LABOR;
        }

        return Stream.of(ResignType.values())
                .filter(c -> c.getValue() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}