package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
import java.util.stream.Stream;

public class ContractImportCategoryConverter implements AttributeConverter<ContractImportCategory, Long> {

    @Override
    public Long convertToDatabaseColumn(ContractImportCategory contractImportCategory) {
        if (contractImportCategory == null) {
            return 0L;
        }
        return (long) contractImportCategory.getValue();
    }

    @Override
    public ContractImportCategory convertToEntityAttribute(Long value) {
        if (value == null) {
            return ContractImportCategory.LABOR;
        }

        return Stream.of(ContractImportCategory.values())
                .filter(c -> c.getValue() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
