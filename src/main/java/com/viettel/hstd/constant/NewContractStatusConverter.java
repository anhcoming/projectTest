package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
        import javax.persistence.Converter;
        import java.util.stream.Stream;

@Converter(autoApply = true)
public class NewContractStatusConverter implements AttributeConverter<NewContractStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(NewContractStatus newContractStatus) {
        if (newContractStatus == null) {
            return 0l;
        }
        return (long) newContractStatus.getValue();
    }

    @Override
    public NewContractStatus convertToEntityAttribute(Long value) {
        if (value == null) {
            return NewContractStatus.HAVENT_SENT_REQUEST_TO_VHR;
        }

        return Stream.of(NewContractStatus.values())
                .filter(c -> c.getValue() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}