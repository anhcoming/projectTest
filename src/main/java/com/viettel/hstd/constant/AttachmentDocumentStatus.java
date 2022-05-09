package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum AttachmentDocumentStatus {
    PENDING(0),
    APPROVAL(1),
    REJECT(2);
    private int value = 0;

    public int getValue() {
        return value;
    }

    AttachmentDocumentStatus(Integer value) {
        this.value = value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case 1: {
                return "Đạt yêu cầu";
            }
            case 2: {
                return "Chưa đạt yêu cầu";
            }
            default: {
                return "Đợi duyệt yêu cầu";
            }
        }
    }

    public static AttachmentDocumentStatus of(int status) {
        return Stream.of(AttachmentDocumentStatus.values())
                .filter(p -> p.getValue() == status)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static AttachmentDocumentStatus of(String statusText) {

        int statusNumber = 0;
        try {
            statusNumber = Integer.parseInt(statusText);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalAttachmentStatus = statusNumber;
        return Stream.of(AttachmentDocumentStatus.values())
                .filter(p -> p.getValue() == finalAttachmentStatus)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
