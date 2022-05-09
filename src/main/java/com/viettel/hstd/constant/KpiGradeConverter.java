package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
        import javax.persistence.Converter;
        import java.util.stream.Stream;

@Converter(autoApply = true)
public class KpiGradeConverter implements AttributeConverter<KpiGrade, String> {

    @Override
    public String convertToDatabaseColumn(KpiGrade kpiGrade) {
        if (kpiGrade == null) {
            return "KĐG";
        }
        return kpiGrade.getValue();
    }

    @Override
    public KpiGrade convertToEntityAttribute(String value) {
        if (value == null) {
            return KpiGrade.UNKNOWN;
        }

        return Stream.of(KpiGrade.values())
                .filter(c -> c.getValue().equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
