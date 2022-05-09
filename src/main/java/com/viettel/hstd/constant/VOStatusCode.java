package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum VOStatusCode {
    SUCCESS(1),
    FILE_TYPE_ERROR(3),
    TRANSACTION_CODE_EXIST_ERROR(4),
    TRANSACTION_CODE_NULL_ERROR(5),
    FILE_LIST_NULL_ERROR(8),
    ACCOUNT_ERROR(9),
    WEBSERVICE_ERROR(11),
    SOME_PARAM_MISSING_ERROR(12),
    ACCOUNT_NULL_ERROR(13),
    UNIT_ID_NULL_ERROR(14),
    PEOPLE_TO_SIGN_NULL_ERROR(15),
    ISSUE_EMPLOYEE_NULL_ERROR(16),
    VOFFICE_ACCOUNT_NOT_EXIST_ERROR(17),
    LOGIN_INFO_NOT_EXIST_ERROR(18),
    ATTACHMENT_FILE_SECURITY_ERROR(20),
    ATTACHMENT_FILE_SIZE_NOT_EXIST_ERROR(22),
    VOFFICE_ACCOUNTS_EMAIL_NOT_EXIST_ERROR(28),
    SSO_LOGIN_ERROR(102),
    LOI_TRINH_KY_VAN_THU_ERROR(103),
    ISSUE_UNIT_NOT_CORRECT_ERROR(104),
    DATA_IS_EMPTY_ERROR(105),
    ATTACHMENT_FILE_NOT_VALID_ERROR(106),
    MAIL_TRINH_KY_KO_TON_TAI_TREN_VO_ERROR(107),
    TITLE_TOO_LONG_ERROR(108),
    PASSWORD_DECRYPTION_ERROR(109);

    private int value = 1;

    VOStatusCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case 1: {
                return "Trình kí thành công";
            }
            case 3: {
                return "File trình kí không đúng định dạng";
            }
            case 4: {
                return "Đã tồn tại mã giao dịch này";
            }
            case 5: {
                return "Mã giao dịch null";
            }
            case 8: {
                return "Danh sách file trình kí null";
            }
            case 9: {
                return "Account không đúng";
            }
            case 11: {
                return "Lỗi phía WebService";
            }
            case 12: {
                return "Thiếu thông tin trình kí";
            }
            case 13: {
                return "Account null";
            }
            case 14: {
                return "Mã đơn vị null";
            }
            case 15: {
                return "Danh sách người ký null";
            }
            case 16: {
                return "Mã nhân viên ban hành null";
            }
            case 17: {
                return "Lỗi không có thông tin tài khoản Voffice";
            }
            case 18: {
                return "Lỗi không truyền tham số tài khoản đăng nhập";
            }
            case 20: {
                return "Lỗi ATTT tên file đính kèm";
            }
            case 22: {
                return "Lỗi file đính kèm không có dung lượng";
            }
            case 28: {
                return "Lỗi không tài khoản Voffice có mail trong danh sách mail trình ký";
            }
            case 102: {
                return "Lỗi đăng nhập tài khoản tập trung SSO";
            }
            case 103: {
                return "Lỗi trình ký cho văn thư";
            }
            case 104: {
                return "Lấy sai đơn vị ban hành";
            }
            case 105: {
                return "Lỗi dữ liệu rỗng";
            }
            case 106: {
                return "Lỗi file đính kèm không hợp lệ";
            }
            case 107: {
                return "Lỗi mail trình ký không tồn tại trên hệ thống Voffice";
            }
            case 108: {
                return "Lỗi tiêu đề văn bản quá dài";
            }
            case 109: {
                return "Lỗi giải mã mật khẩu";
            }
            case 110: {
                return "Lỗi thiếu thông tin đơn vị hoặc Id người ký";
            }
            default: {
                return "Lỗi trình ký";
            }
        }
    }

    public static VOStatusCode of(int voErrorInt) {
        return Stream.of(VOStatusCode.values())
                .filter(p -> p.getValue() == voErrorInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static VOStatusCode of(String voErrorString) {

        int voErrorInt = 1;
        try {
            voErrorInt = Integer.parseInt(voErrorString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalVOStatusCodeInt = voErrorInt;
        return Stream.of(VOStatusCode.values())
                .filter(p -> p.getValue() == finalVOStatusCodeInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
