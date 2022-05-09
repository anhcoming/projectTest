package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
        import javax.persistence.Converter;
        import java.util.stream.Stream;

@Converter(autoApply = true)
public class InterviewResultConverter implements AttributeConverter<InterviewResult, Long> {

    @Override
    public Long convertToDatabaseColumn(InterviewResult interviewResult) {
        if (interviewResult == null) {
            return 0L;
        }
        return (long) interviewResult.getValue();
    }

    @Override
    public InterviewResult convertToEntityAttribute(Long value) {
        if (value == null) {
            return InterviewResult.NOT_EVALUATE_YET;
        }

        return Stream.of(InterviewResult.values())
                .filter(c -> c.getValue() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}