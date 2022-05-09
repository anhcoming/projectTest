package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum InsuranceStatus {
    NOT_IN_INSURANCE_PROGRESS(-1),
    JUST_ADD_INTO_INSURANCE_PROGRESS(0),
    DONE_ADD_INTO_INSURANCE_PROGRESS(1),
    IN_PREPARING_AND_DECREASING_PROGRESS(2),
    PREPARING_AND_DECREASING_PROGRESS_FAIL(3),
    PREPARING_AND_DECREASING_PROGRESS_SUCCESS(4),
    IN_SENDING_TO_BHXH_PROGRESS(5),
    SENT_TO_BHXH_FAILED(6),
    SENT_TO_BHXH_SUCCESS(7);

    private int value = -1;

    InsuranceStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case -1: {
                return "Chưa ở trong quy trình chốt sổ";
            }
            case 0: {
                return "Vừa add vào trong quy trình chốt sổ";
            }
            case 1: {
                return "Hoàn thành add vào trong quy trình chốt sổ";
            }
            case 2: {
                return "Đang ở trong giai đoạn Báo giảm/Chuẩn bị hồ sơ";
            }
            case 3: {
                return "Báo giảm/Chuẩn bị hồ sơ thất bại";
            }
            case 4: {
                return "Báo giảm/Chuẩn bị hồ sơ thành công";
            }
            case 5: {
                return "Đang ở trong giai đoạn gửi BHXH";
            }
            case 6: {
                return "Gửi BHXH trả về thất bại";
            }
            case 7: {
                return "Gửi BHXH trả về thành công";
            }
            default: {
                return "Chưa xác định";
            }
        }
    }

    public static InsuranceStatus of(int insuranceStatusInt) {
        return Stream.of(InsuranceStatus.values())
                .filter(p -> p.getValue() == insuranceStatusInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static InsuranceStatus of(String insuranceStatusString) {

        int insuranceStatusInt = 1;
        try {
            insuranceStatusInt = Integer.parseInt(insuranceStatusString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalInsuranceStatusInt = insuranceStatusInt;
        return Stream.of(InsuranceStatus.values())
                .filter(p -> p.getValue() == finalInsuranceStatusInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}