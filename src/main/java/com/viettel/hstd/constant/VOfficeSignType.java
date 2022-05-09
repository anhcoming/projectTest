package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum VOfficeSignType {
    UNKNOWN(0),
    INTERVIEW_SESSION_CV(1),
    COLLABORATOR(2),
    FREELANCE(3),
    LABOR(4),
    PROBATIONARY(5),
    SERVICE(6),
    TERMINATE(8),
    RESIGN_FORM_09(12),
    SEV_ALLOWANCE(13),
    RESIGN_LABOR_FINAL(14),
    BRAND_NEW_CONTRACT(15),
    RESIGN_FORM_03(16),
    RESIGN_FORM_09_TCT(17),
    RESIGN_FORM_03_TCT(18),
    SEV_ALLOWANCE_MULTI(19);

    private int value = 0;

    VOfficeSignType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case 1: {
                return "Báo cáo tuyển dụng";
            }
            case 2: {
                return "Hợp đồng Cộng tác viên";
            }
            case 3: {
                return "Hợp đồng thỏa thuận hợp tác";
            }
            case 4: {
                return "Hợp đồng Lao động";
            }
            case 5: {
                return "Hợp đồng Thử việc";
            }
            case 6: {
                return "Hợp đồng Dịch vụ";
            }
            case 8: {
                return "Đơn xin chấm dứt hợp đồng";
            }
            case 12: {
                return "Biểu mẫu 9 trong phần tái ký";
            }
            case 13: {
                return "Ký trợ cấp thất nghiệp";
            }
            case 14: {
                return "Hợp đồng lao động cuối tái ký";
            }
            case 15: {
                return "Hợp đồng mới từ ứng viên";
            }
            case 16: {
                return "Biểu mẫu 03 trong phần tái ký";
            }
            case 17: {
                return "Biểu mẫu 09 TCT trong phần tái ký";
            }
            case 18: {
                return "Biểu mẫu 03 TCT trong phần tái ký";
            }
            case 19: {
                return "Ghép nhiều quyết định chấm dứt hợp đồng";
            }
            default: {
                return "Chưa xác định";
            }
        }
    }

    public static VOfficeSignType of(int vofficeSignTypeInt) {
        return Stream.of(VOfficeSignType.values())
                .filter(p -> p.getValue() == vofficeSignTypeInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static VOfficeSignType of(String otpString) {

        int vofficeSignTypeInt = 0;
        try {
            vofficeSignTypeInt = Integer.parseInt(otpString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalVOfficeSignTypeInt = vofficeSignTypeInt;
        return Stream.of(VOfficeSignType.values())
                .filter(p -> p.getValue() == finalVOfficeSignTypeInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}