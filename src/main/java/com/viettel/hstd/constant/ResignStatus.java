package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum ResignStatus {
    NOT_IN_RESIGN_SESSION(-1),
    IN_EVALUATION(0),
    HR_UPDATED_INTERVIEW_RESULT(1),
    CREATED_BM_FILE(2),
    SENT_BM_TO_VOFFICE1(3),
    RECEIVED_BM_09_VOFFICE_AND_FAIL(4),
    RECEIVED_BM_09_VOFFICE_AND_SUCCESS(5),
    HR_TCT_ADD_EMPLOYEE_TO_VOFFICE2(6),
    HR_TCT_CREATED_BMTCT_FILE(7),
    HR_TCT_SENT_TO_VOFFICE2(8),
    RECEIVED_VOFFICE2_AND_FAIL(9),
    RECEIVED_VOFFICE2_AND_SUCCESS(10),
    TEMP_CONTRACT_CREATE(11),
    HR_UPDATED_RESIGN_CONTRACT(12),
    HR_CREATED_FILE_4_EMPLOYEE_2_SIGN(13),
    EMPLOYEE_SIGN_RESIGN_CONTRACT(14),
    HR_SENT_TO_VOFFICE3(15),
    RECEIVED_VOFFICE3_AND_FAIL(16),
    RECEIVED_VOFFICE3_AND_SUCCESS(17),
    UPDATED_TEMP_CONTRACT_2_OFFICAL(18),
    SENT_REQUEST_OF_NEW_RESIGN_CONTRACT_TO_VHR(19);

    private int value = -1;

    ResignStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case 0: {
                return "Đang đánh giá (Ở trong đợt tái ký)";
            }
            case 1: {
                return "Đang gửi biểu mẫu 09";
            }
            case 2: {
                return "Đã được chấp thuận (Trình ký bm09 thành công và được ký tiếp)";
            }
            case 3: {
                return "Tạo mới hợp đồng tạm thời";
            }
            case 4: {
                return "Chờ nhân viên ký";
            }
            case 5: {
                return "Nhân viên đã ký";
            }
            case 6: {
                return "Đang trình ký voffice";
            }
            case 7: {
                return "Đã trình ký";
            }
            default: {
                return "Không ở trong đợt tái ký";
            }
        }
    }

    public static ResignStatus of(int resignStatus) {
        return Stream.of(ResignStatus.values())
            .filter(p -> p.getValue() == resignStatus)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    public static ResignStatus of(String resignFinalFormStatusString) {

        int resignStatus = -1;
        try {
            resignStatus = Integer.parseInt(resignFinalFormStatusString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalResignStatusInt = resignStatus;
        return Stream.of(ResignStatus.values())
            .filter(p -> p.getValue() == finalResignStatusInt)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}

