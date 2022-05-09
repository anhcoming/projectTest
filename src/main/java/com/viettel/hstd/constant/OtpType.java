package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum OtpType {
    UNKNOWN(0),
    PROBATIONARY_CONTRACT(1),
    LABOR_CONTRACT(2),
    TERMINATE_CONTRACT(3);

    private int value = 0;

    OtpType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case 1: {
                return "Hợp đồng Thử việc";
            }
            case 2: {
                return "Hợp đồng Lao động";
            }
            case 3: {
                return "Chấm dứt hợp đồng";
            }
            default: {
                return "Chưa xác định";
            }
        }
    }

    public static OtpType of(int optInt) {
        return Stream.of(OtpType.values())
                .filter(p -> p.getValue() == optInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static OtpType of(String otpString) {

        int optInt = 0;
        try {
            optInt = Integer.parseInt(otpString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalOtpTypeInt = optInt;
        return Stream.of(OtpType.values())
                .filter(p -> p.getValue() == finalOtpTypeInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}


