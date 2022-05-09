package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum Gender {
    UNKNOWN(0),
    MALE(1),
    FEMALE(2);

    private int value = 1;

    Gender(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case 1: {
                return "Nam";
            }
            case 2: {
                return "Nữ";
            }
            default: {
                return "Chưa xác định";
            }
        }
    }

    public static Gender of(int genderInt) {
        return Stream.of(Gender.values())
                .filter(p -> p.getValue() == genderInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static Gender of(String genderString) {

        int genderInt = 1;
        try {
            genderInt = Integer.parseInt(genderString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalGenderInt = genderInt;
        return Stream.of(Gender.values())
                .filter(p -> p.getValue() == finalGenderInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
