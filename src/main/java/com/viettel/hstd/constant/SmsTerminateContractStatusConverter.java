package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class SmsTerminateContractStatusConverter implements AttributeConverter<SmsTerminateContractStatus, Long> {
    @Override
    public Long convertToDatabaseColumn(SmsTerminateContractStatus smsTerminateContractStatus) {
        if (smsTerminateContractStatus == null) {
            return 1L;
        }
        return (long) smsTerminateContractStatus.getValue();
    }

    @Override
    public SmsTerminateContractStatus convertToEntityAttribute(Long value) {
        if (value == null) {
            return SmsTerminateContractStatus.SEND_REQUEST;
        }

        return Stream.of(SmsTerminateContractStatus.values())
                .filter(c -> c.getValue() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
