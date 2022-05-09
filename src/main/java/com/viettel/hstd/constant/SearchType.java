package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum SearchType {
    APP_PARAM(1),
    DATETIME(2),
    STRING(3),
    NUMBER(4),
    BOOLEAN(5),
    DATE(6),
    FLOAT(7),
    CUSTOM_TABLE(8),
    HASH_MAP(9);

    private int value = 3;

    SearchType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case 1: {
                return "App param";
            }
            case 2: {
                return "Ngày giờ";
            }
            case 4: {
                return "Số";
            }
            case 5: {
                return "Đúng sai";
            }
            case 6: {
                return "Ngày";
            }
            case 7: {
                return "Số thực";
            }
            case 8: {
                return "Bảng";
            }
            case 9: {
                return "Hash map";
            }
            default: {
                return "Ký tự";
            }
        }
    }

    public static SearchType of(int genderInt) {
        return Stream.of(SearchType.values())
            .filter(p -> p.getValue() == genderInt)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    public static SearchType of(String genderString) {

        int genderInt = 1;
        try {
            genderInt = Integer.parseInt(genderString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalSearchTypeInt = genderInt;
        return Stream.of(SearchType.values())
            .filter(p -> p.getValue() == finalSearchTypeInt)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
