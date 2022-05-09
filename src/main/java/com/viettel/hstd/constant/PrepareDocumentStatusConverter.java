package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
        import javax.persistence.Converter;
        import java.util.stream.Stream;

@Converter(autoApply = true)
public class PrepareDocumentStatusConverter implements AttributeConverter<PrepareDocumentStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(PrepareDocumentStatus insuranceStatus) {
        if (insuranceStatus == null) {
            return 1L;
        }
        return (long) insuranceStatus.getValue();
    }

    @Override
    public PrepareDocumentStatus convertToEntityAttribute(Long value) {
        if (value == null) {
            return PrepareDocumentStatus.NOT_IN_PREPARE_DOCUMENT_PROGRESS;
        }

        return Stream.of(PrepareDocumentStatus.values())
                .filter(c -> c.getValue() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}