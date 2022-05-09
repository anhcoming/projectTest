package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum ResignType {
    LABOR(1),
    PROBATIONARY(2);

    private int value = 1;

    ResignType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case 1: {
                return "Tái ký nhân viên";
            }
            case 2: {
                return "Tái ký thử việc";
            }
            default: {
                return "Chưa xác định";
            }
        }
    }

    public static ResignType of(int resignTypeInt) {
        return Stream.of(ResignType.values())
                .filter(p -> p.getValue() == resignTypeInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static ResignType of(String resignTypeString) {

        int resignTypeInt = 1;
        try {
            resignTypeInt = Integer.parseInt(resignTypeString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalResignTypeInt = resignTypeInt;
        return Stream.of(ResignType.values())
                .filter(p -> p.getValue() == finalResignTypeInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
