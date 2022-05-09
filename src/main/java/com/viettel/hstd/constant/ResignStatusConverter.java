package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
    import javax.persistence.Converter;
    import java.util.stream.Stream;

@Converter(autoApply = true)
public class ResignStatusConverter implements AttributeConverter<ResignStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(ResignStatus resignStatus) {
        if (resignStatus == null) {
            return -1L;
        }
        return new Long(resignStatus.getValue()) ;
    }

    @Override
    public ResignStatus convertToEntityAttribute(Long value) {
        if (value == null) {
            return ResignStatus.NOT_IN_RESIGN_SESSION;
        }

        return Stream.of(ResignStatus.values())
            .filter(c -> c.getValue() == value)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
