package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum ResignVofficeStatus {
    NOT_SEND_YET(0),
    SENT(1),
    RECEIVED(2);

    private int value = 0;

    ResignVofficeStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case 1: {
                return "Đã gửi trình ký đợi kết quả";
            }
            case 2: {
                return "Đã nhận được kết quả";
            }
            default: {
                return "Chưa trình ký";
            }
        }
    }

    public static ResignVofficeStatus of(int resignFinalFormStatusInt) {
        return Stream.of(ResignVofficeStatus.values())
            .filter(p -> p.getValue() == resignFinalFormStatusInt)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    public static ResignVofficeStatus of(String resignFinalFormStatusString) {

        int resignFinalFormStatusInt = 1;
        try {
            resignFinalFormStatusInt = Integer.parseInt(resignFinalFormStatusString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalResignFinalFormStatusInt = resignFinalFormStatusInt;
        return Stream.of(ResignVofficeStatus.values())
            .filter(p -> p.getValue() == finalResignFinalFormStatusInt)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
