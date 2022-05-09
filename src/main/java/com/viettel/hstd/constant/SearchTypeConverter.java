package com.viettel.hstd.constant;

import javax.persistence.AttributeConverter;
    import javax.persistence.Converter;
    import java.util.stream.Stream;

@Converter(autoApply = true)
public class SearchTypeConverter implements AttributeConverter<SearchType, Long> {

    @Override
    public Long convertToDatabaseColumn(SearchType searchType) {
        if (searchType == null) {
            return 3L;
        }
        return (long) searchType.getValue();
    }

    @Override
    public SearchType convertToEntityAttribute(Long value) {
        if (value == null) {
            return SearchType.STRING;
        }

        return Stream.of(SearchType.values())
            .filter(c -> c.getValue() == value)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
