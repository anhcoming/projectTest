package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum DecreaseStatus {
    NOT_IN_DECREASE_PROGRESS(-1),
    IN_DECREASE_PROGRESS(0),
    DECREASE_PROGRESS_FAIL(1),
    DECREASE_PROGRESS_SUCCESS(3);

    private int value = -1;

    DecreaseStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case -1: {
                return "Không ở trong quy trình báo giảm";
            }
            case 0: {
                return "Bắt đầu quy trình báo giảm";
            }
            case 1: {
                return "Quy trình báo giảm thất bại";
            }
            case 2: {
                return "Quy trình báo giảm thành công";
            }
            default: {
                return "Chưa xác định";
            }
        }
    }

    public static DecreaseStatus of(int insuranceStatusInt) {
        return Stream.of(DecreaseStatus.values())
                .filter(p -> p.getValue() == insuranceStatusInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static DecreaseStatus of(String insuranceStatusString) {

        int insuranceStatusInt = 1;
        try {
            insuranceStatusInt = Integer.parseInt(insuranceStatusString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalDecreaseStatusInt = insuranceStatusInt;
        return Stream.of(DecreaseStatus.values())
                .filter(p -> p.getValue() == finalDecreaseStatusInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}