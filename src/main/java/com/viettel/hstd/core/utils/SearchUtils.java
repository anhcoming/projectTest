package com.viettel.hstd.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.constant.Operation;
import com.viettel.hstd.constant.SearchType;
import com.viettel.hstd.core.constant.ConstantConfig;
import com.viettel.hstd.core.constant.FormatConstant;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.dto.SearchDTO.SearchCriteria;
import com.viettel.hstd.core.specification.EntityConfigSpec;
import com.viettel.hstd.core.dto.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchUtils {

    public static Pageable getPageable(SearchDTO searchRequest) {
        List<Sort.Order> orders = new ArrayList<>();
        searchRequest.sortList.forEach(sort -> {
            Sort.Order order = new Sort.Order(
                    sort.direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sort.property);
            orders.add(order);
        });

        return PageRequest.of(searchRequest.page, searchRequest.size, Sort.by(orders));
    }

    public static <T> Specification<T> getSpecifications(SearchDTO searchRequest) {
        Specification specs = Specification.where(null);
        if (searchRequest.criteriaList.size() == 0) {
            return specs;
        }
        List<SearchCriteria> staticCriteria = searchRequest.criteriaList;
        alterSearchLocalDateTime(staticCriteria);
        for (int i = 0; i < staticCriteria.size(); i++) {
            if (i == 0) {
                specs = new EntityConfigSpec<T>(
                        new SearchCriteria(staticCriteria.get(i).getField(),
                                staticCriteria.get(i).getOperation(),
                                staticCriteria.get(i).getValue(),
                                staticCriteria.get(i).getType(),
                                staticCriteria.get(i).getAndFlag()));
            } else {
                if (staticCriteria.get(i).getAndFlag()) {
                    specs = specs.and(new EntityConfigSpec<T>(
                            new SearchCriteria(staticCriteria.get(i).getField(),
                                    staticCriteria.get(i).getOperation(),
                                    staticCriteria.get(i).getValue(),
                                    staticCriteria.get(i).getType(),
                                    staticCriteria.get(i).getAndFlag())));
                } else {
                    specs = specs.or(new EntityConfigSpec<T>(
                            new SearchCriteria(staticCriteria.get(i).getField(),
                                    staticCriteria.get(i).getOperation(),
                                    staticCriteria.get(i).getValue(),
                                    staticCriteria.get(i).getType(),
                                    staticCriteria.get(i).getAndFlag())));
                }
            }

        }
        return specs;
    }

    private static void alterSearchLocalDateTime(List<SearchCriteria> listSearch) {
        List<SearchCriteria> newListSearch = new ArrayList<>();

        listSearch.forEach(obj -> {
            if (obj.getType() == SearchType.DATETIME && obj.getOperation().equals(Operation.EQUAL)) {
                obj.setOperation(Operation.GREATER_EQUAL);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FormatConstant.LOCAL_DATE_TIME_FORMAT);
                LocalDateTime localDateTime = LocalDateTime.parse(obj.getValue().toString(), formatter);
                LocalDateTime startDay = localDateTime.toLocalDate().atStartOfDay();
                LocalDateTime endDay = startDay.plusDays(1);
                obj.setValue(startDay.format(formatter));

                SearchCriteria newSearch = new SearchCriteria();
                newSearch.setOperation(Operation.LESS);
                newSearch.setType(obj.getType());
                newSearch.setAndFlag(obj.getAndFlag());
                newSearch.setValue(endDay.format(formatter));
                newSearch.setField(obj.getField());
                newListSearch.add(newSearch);
            }
        });

        listSearch.addAll(newListSearch);
    }

    public static <RES> BaseResponse<List<RES>> getResponseFromPage(Page<RES> pages) {
        BaseResponse<List<RES>> response = new BaseResponse.ResponseBuilder<List<RES>>()
                .success(pages.getContent());
        response.page = pages.getNumber();
        response.size = pages.getSize();
        response.total = pages.getTotalElements();
        return response;
    }
}
