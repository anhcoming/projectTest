package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
        import javax.persistence.Converter;
        import java.util.stream.Stream;

@Converter(autoApply = true)
public class OtpTypeConverter implements AttributeConverter<OtpType, Long> {

    @Override
    public Long convertToDatabaseColumn(OtpType otpType) {
        if (otpType == null) {
            return 0L;
        }
        return (long) otpType.getValue();
    }

    @Override
    public OtpType convertToEntityAttribute(Long value) {
        if (value == null) {
            return OtpType.UNKNOWN;
        }

        return Stream.of(OtpType.values())
                .filter(c -> c.getValue() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}