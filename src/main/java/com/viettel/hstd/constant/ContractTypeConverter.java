package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
        import javax.persistence.Converter;
        import java.util.stream.Stream;

@Converter(autoApply = true)
public class ContractTypeConverter implements AttributeConverter<ContractType, Long> {

    @Override
    public Long convertToDatabaseColumn(ContractType contractType) {
        if (contractType == null) {
            return 4L;
        }
        return (long) contractType.getValue();
    }

    @Override
    public ContractType convertToEntityAttribute(Long value) {
        if (value == null) {
            return ContractType.LABOR_CONTRACT;
        }

        return Stream.of(ContractType.values())
                .filter(c -> c.getValue() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}