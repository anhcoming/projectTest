package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum Operation {
    GREATER(">"),
    LESS("<"),
    GREATER_EQUAL(">="),
    LESS_EQUAL("<="),
    EQUAL("="),
    LIKE(":"),
    IN("in"),
    NOT_LIKE("NOT LIKE"),
    NOT("NOT");

    private String value = ":";

    Operation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case ">": {
                return "lớn hơn";
            }
            case "<": {
                return "bé hơn";
            }
            case ">=": {
                return "lớn hơn hoặc bằng";
            }
            case "<=": {
                return "bé hơn hoặc bằng";
            }
            case "=": {
                return "bằng";
            }
            case "in": {
                return "trong";
            }
            case "NOT_LIKE": {
                return "không giống";
            }
            default: {
                return "giống";
            }
        }
    }

    public static Operation of(String operationString) {
        return Stream.of(Operation.values())
            .filter(p -> {
                return p.getValue().equals(operationString);
            })
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
