package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
    import javax.persistence.Converter;
    import java.util.stream.Stream;

@Converter(autoApply = true)
public class ResignVofficeStatusConverter implements AttributeConverter<ResignVofficeStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(ResignVofficeStatus resignPassStatus) {
        if (resignPassStatus == null) {
            return 0L;
        }
        return (long) resignPassStatus.getValue();
    }

    @Override
    public ResignVofficeStatus convertToEntityAttribute(Long value) {
        if (value == null) {
            return ResignVofficeStatus.NOT_SEND_YET;
        }

        return Stream.of(ResignVofficeStatus.values())
            .filter(c -> c.getValue() == value)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
