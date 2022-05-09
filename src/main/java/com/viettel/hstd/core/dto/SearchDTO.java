package com.viettel.hstd.core.dto;

import com.viettel.hstd.constant.Operation;
import com.viettel.hstd.constant.SearchType;
import com.viettel.hstd.core.constant.ConstantConfig;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class SearchDTO {
    public int page = 0;
    public int size = 10;
//    public String keySearch;
    public Boolean pagedFlag = false;
    public List<OrderDTO> sortList = new ArrayList<>();
    public List<SearchCriteria> criteriaList = new ArrayList<>();


    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDTO {
        public String property;
        public String direction;
    }

    @Data
    @Getter
    @Setter
    @NoArgsConstructor
    public static class SearchCriteria {
        private String field;
        private Operation operation;
        private Object value;
        private SearchType type = SearchType.STRING;
        private Boolean andFlag = true;


        public SearchCriteria(String field, Operation operation, Object value) {
            this.field = field;
            this.operation = operation;
            this.value = value;
        }

        public SearchCriteria(String field, Operation operation, Object value, SearchType type, boolean andFlag) {
            this.field = field;
            this.operation = operation;
            this.value = value;
            this.type = type;
            this.andFlag = andFlag;
        }
    }
}

