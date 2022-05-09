package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
        import javax.persistence.Converter;
        import java.util.stream.Stream;

@Converter(autoApply = true)
public class VOfficeSignTypeConverter implements AttributeConverter<VOfficeSignType, Long> {

    @Override
    public Long convertToDatabaseColumn(VOfficeSignType otpType) {
        if (otpType == null) {
            return 0L;
        }
        return (long) otpType.getValue();
    }

    @Override
    public VOfficeSignType convertToEntityAttribute(Long value) {
        if (value == null) {
            return VOfficeSignType.UNKNOWN;
        }

        return Stream.of(VOfficeSignType.values())
                .filter(c -> c.getValue() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}