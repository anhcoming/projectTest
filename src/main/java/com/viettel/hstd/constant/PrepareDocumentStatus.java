package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum PrepareDocumentStatus {
    NOT_IN_PREPARE_DOCUMENT_PROGRESS(-1),
    IN_PREPARE_DOCUMENT_PROGRESS(0),
    FINISH_PREPARE_DOCUMENT_PROGRESS(1);

    private int value = -1;

    PrepareDocumentStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case -1: {
                return "Không ở trong quy trình Chuẩn bị hồ sơ";
            }
            case 0: {
                return "Bắt đầu quy trình báo giảm";
            }
            case 1: {
                return "Hoàn thành Quy trình báo giảm thành công";
            }
            default: {
                return "Chưa xác định";
            }
        }
    }

    public static PrepareDocumentStatus of(int prepareDocumentStatusInt) {
        return Stream.of(PrepareDocumentStatus.values())
                .filter(p -> p.getValue() == prepareDocumentStatusInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static PrepareDocumentStatus of(String prepareDocumentStatusString) {

        int prepareDocumentStatusInt = 1;
        try {
            prepareDocumentStatusInt = Integer.parseInt(prepareDocumentStatusString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalPrepareDocumentStatusInt = prepareDocumentStatusInt;
        return Stream.of(PrepareDocumentStatus.values())
                .filter(p -> p.getValue() == finalPrepareDocumentStatusInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}