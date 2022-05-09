package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class ContractDurationConverter implements AttributeConverter<ContractDuration, Long> {

    @Override
    public Long convertToDatabaseColumn(ContractDuration contractDuration) {
        if (contractDuration == null) {
            return 0L;
        }
        return (long) contractDuration.getValue();
    }

    @Override
    public ContractDuration convertToEntityAttribute(Long value) {
        if (value == null) {
            return ContractDuration.ONE_YEAR;
        }

        return Stream.of(ContractDuration.values())
            .filter(c -> c.getValue() == value)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
