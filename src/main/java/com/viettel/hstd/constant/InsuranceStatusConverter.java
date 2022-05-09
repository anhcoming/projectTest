package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
        import javax.persistence.Converter;
        import java.util.stream.Stream;

@Converter(autoApply = true)
public class InsuranceStatusConverter implements AttributeConverter<InsuranceStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(InsuranceStatus insuranceStatus) {
        if (insuranceStatus == null) {
            return 1L;
        }
        return (long) insuranceStatus.getValue();
    }

    @Override
    public InsuranceStatus convertToEntityAttribute(Long value) {
        if (value == null) {
            return InsuranceStatus.NOT_IN_INSURANCE_PROGRESS;
        }

        return Stream.of(InsuranceStatus.values())
                .filter(c -> c.getValue() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}