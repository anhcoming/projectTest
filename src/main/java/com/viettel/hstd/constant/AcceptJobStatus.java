package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum AcceptJobStatus {
    NOT_DECIDED_YET(2),
    ACCEPT(1),
    DECLINE(0);

    private int value = 2;

    AcceptJobStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case 1: {
                return "Nhận việc";
            }
            case 0: {
                return "Không nhận việc";
            }
            default: {
                return "Chưa đánh giá";
            }
        }
    }

    public static AcceptJobStatus of(int acceptJobStatusInt) {
        return Stream.of(AcceptJobStatus.values())
                .filter(p -> p.getValue() == acceptJobStatusInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static AcceptJobStatus of(String acceptJobStatusString) {

        int acceptJobStatusInt = 2;
        try {
            acceptJobStatusInt = Integer.parseInt(acceptJobStatusString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalAcceptJobStatusInt = acceptJobStatusInt;
        return Stream.of(AcceptJobStatus.values())
                .filter(p -> p.getValue() == finalAcceptJobStatusInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}


