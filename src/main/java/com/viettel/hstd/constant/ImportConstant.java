package com.viettel.hstd.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ImportConstant {

    public enum ImportStatus {
        PROCESSING("Đang xử lý"), FAILED("Lỗi"), SUCCESS("Thành công");

        public final String title;

        ImportStatus(String title) {
            this.title = title;
        }
    }

    public enum ImportType {
        TEXT, NUMERIC, BOOLEAN, DATE
    }

    public enum ImportCode {
        POSITION_DESCRIPTION("Mô tả công việc"), RECRUITMENT_PLAN ("Kế hoạch tuyển dụng");

        public final String title;

        ImportCode(String title) {
            this.title = title;
        }
    }
}
