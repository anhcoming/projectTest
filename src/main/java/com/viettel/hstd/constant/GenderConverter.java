package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, Long> {

    @Override
    public Long convertToDatabaseColumn(Gender gender) {
        if (gender == null) {
            return 1L;
        }
        return (long) gender.getValue();
    }

    @Override
    public Gender convertToEntityAttribute(Long value) {
        if (value == null) {
            return Gender.MALE;
        }

        return Stream.of(Gender.values())
          .filter(c -> c.getValue() == value)
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }
}
