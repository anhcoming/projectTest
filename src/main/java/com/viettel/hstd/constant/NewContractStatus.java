package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum NewContractStatus {
    HAVENT_SENT_REQUEST_TO_VHR(0),
    SENT_REQUEST_TO_VHR(1),
    RECEIVED_CODE_FROM_VHR(2),
    HR_UPDATED_PROBATIONARY_CONTRACT(3),
    HR_CREATED_WORD_FILE(4),
    HR_SENT_CONTRACT_EMPLOYEE(5),
    EMPLOYEE_SIGNED_CONTRACT(6),
    SENT_TO_VOFFICE(7),
    RECEIVED_FROM_VOFFICE(8);

    private int value = 0;

    NewContractStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case 1: {
                return "Đang cấp mã VHR";
            }
            case 2: {
                return "Đã cấp mã VHR/ Chưa cập nhật hợp đồng thử việc";
            }
            case 3: {
                return "Đã cập nhật hợp đồng thử việc/ Chưa và đang đưa người LĐ ký";
            }
            case 4: {
                return "HR đã tạo file hợp đồng thử việc";
            }
            case 5: {
                return "HR gửi file hợp đồng cho nhân viên để ký";
            }
            case 6: {
                return "Người Lao Động đã ký/ Chưa trình ký Voffice";
            }
            case 7: {
                return "Đang trình ký Voffice";
            }
            case 8: {
                return "Đã trình ký Voffice/ Có trong danh sách nhân viên";
            }
            default: {
                return "Chưa xin cấp mã VHR";
            }
        }
    }

    public static NewContractStatus of(int probationaryStatus) {
        return Stream.of(NewContractStatus.values())
                .filter(p -> p.getValue() == probationaryStatus)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static NewContractStatus of(String resignFinalFormStatusString) {

        int probationaryStatus = 0;
        try {
            probationaryStatus = Integer.parseInt(resignFinalFormStatusString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalProbationaryStatusInt = probationaryStatus;
        return Stream.of(NewContractStatus.values())
                .filter(p -> p.getValue() == finalProbationaryStatusInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}