package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class ResignPassStatusConverter implements AttributeConverter<ResignPassStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(ResignPassStatus resignPassStatus) {
        if (resignPassStatus == null) {
            return 0L;
        }
        return (long) resignPassStatus.getValue();
    }

    @Override
    public ResignPassStatus convertToEntityAttribute(Long value) {
        if (value == null) {
            return ResignPassStatus.NOT_EVALUATE_YET;
        }

        return Stream.of(ResignPassStatus.values())
            .filter(c -> c.getValue() == value)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
