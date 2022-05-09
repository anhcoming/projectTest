package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
        import javax.persistence.Converter;
        import java.util.stream.Stream;

@Converter(autoApply = true)
public class AcceptJobStatusConverter implements AttributeConverter<AcceptJobStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(AcceptJobStatus acceptJobStatus) {
        if (acceptJobStatus == null) {
            return 0L;
        }
        return (long) acceptJobStatus.getValue();
    }

    @Override
    public AcceptJobStatus convertToEntityAttribute(Long value) {
        if (value == null) {
            return AcceptJobStatus.NOT_DECIDED_YET;
        }

        return Stream.of(AcceptJobStatus.values())
                .filter(c -> c.getValue() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}

