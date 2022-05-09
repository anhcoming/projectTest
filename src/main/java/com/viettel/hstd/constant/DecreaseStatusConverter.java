package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
        import javax.persistence.Converter;
        import java.util.stream.Stream;

@Converter(autoApply = true)
public class DecreaseStatusConverter implements AttributeConverter<DecreaseStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(DecreaseStatus insuranceStatus) {
        if (insuranceStatus == null) {
            return 1L;
        }
        return (long) insuranceStatus.getValue();
    }

    @Override
    public DecreaseStatus convertToEntityAttribute(Long value) {
        if (value == null) {
            return DecreaseStatus.NOT_IN_DECREASE_PROGRESS;
        }

        return Stream.of(DecreaseStatus.values())
                .filter(c -> c.getValue() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}