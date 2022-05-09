package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum Attitude {
    NOT_EVALUATE_YET(0),
    PASS(1),
    FAIL(2);

    private int value = 0;

    Attitude(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case 1: {
                return "Đạt";
            }
            case 2: {
                return "Không ";
            }
            default: {
                return "Chưa đánh giá";
            }
        }
    }

    public static Attitude of(int attitudeInt) {
        return Stream.of(Attitude.values())
            .filter(p -> p.getValue() == attitudeInt)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    public static Attitude of(String attitudeString) {

        int attitudeInt = 1;
        try {
            attitudeInt = Integer.parseInt(attitudeString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalAttitudeInt = attitudeInt;
        return Stream.of(Attitude.values())
            .filter(p -> p.getValue() == finalAttitudeInt)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
