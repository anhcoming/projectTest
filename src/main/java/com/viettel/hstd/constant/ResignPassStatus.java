package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum ResignPassStatus {
    NOT_EVALUATE_YET(0),
    PASS(1),
    FAIL(2);

    private int value = 0;

    ResignPassStatus(int value) {
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
                return "Không đạt";
            }
            default: {
                return "Chưa đánh giá";
            }
        }
    }

    public static ResignPassStatus of(int resignFinalFormStatusInt) {
        return Stream.of(ResignPassStatus.values())
            .filter(p -> p.getValue() == resignFinalFormStatusInt)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    public static ResignPassStatus of(String resignFinalFormStatusString) {

        int resignFinalFormStatusInt = 1;
        try {
            resignFinalFormStatusInt = Integer.parseInt(resignFinalFormStatusString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalResignFinalFormStatusInt = resignFinalFormStatusInt;
        return Stream.of(ResignPassStatus.values())
            .filter(p -> p.getValue() == finalResignFinalFormStatusInt)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
