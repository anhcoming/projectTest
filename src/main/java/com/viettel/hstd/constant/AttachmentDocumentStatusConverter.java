package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class AttachmentDocumentStatusConverter implements AttributeConverter<AttachmentDocumentStatus, Long> {
    @Override
    public Long convertToDatabaseColumn(AttachmentDocumentStatus attachmentDocumentStatus) {
        if (attachmentDocumentStatus == null) {
            return 0L;
        }
        return (long) attachmentDocumentStatus.getValue();
    }


    @Override
    public AttachmentDocumentStatus convertToEntityAttribute(Long value) {
        if (value == null) {
            return AttachmentDocumentStatus.PENDING;
        }
        return Stream.of(AttachmentDocumentStatus.values())
                .filter(c -> c.getValue() == value)
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
