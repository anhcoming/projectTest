package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class AttitudeConverter implements AttributeConverter<Attitude, Long> {

    @Override
    public Long convertToDatabaseColumn(Attitude attitude) {
        if (attitude == null) {
            return 0L;
        }
        return (long) attitude.getValue();
    }

    @Override
    public Attitude convertToEntityAttribute(Long value) {
        if (value == null) {
            return Attitude.NOT_EVALUATE_YET;
        }

        return Stream.of(Attitude.values())
            .filter(c -> c.getValue() == value)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}

