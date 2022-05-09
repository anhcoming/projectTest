package com.viettel.hstd.constant;

public class VPSConstant {
    public static long PROVINCE_DOMAIN_TYPE_ID = 300L;
    public static String ALL_PROVINCE_DOMAIN_CODE = "KCQ.TCT";
    public static Long ALL_PROVINCE_UNIT_ID = 9004488L;
    public static String ALL_PROVINCE_UNIT_NAME = "KCQ TCT Công trình";

    public static Long VCC_ORGANIZATION_ID = 9004482L;

    public static int UNIT_ORGANIZATION_LEVEL = 5;
    public static int DEPARTMENT_ORGANIZATION_LEVEL = 6;

    public static int VCC_UNIT_ORG_MANAGE_LEVEL = 2;
    public static int VCC_DEPARTMENT_ORG_MANAGE_LEVEL = 3;

    public static String HSTD_USER_ROLE_CODE = "HSTD_USER";
    public static String HSTD_ADMIN_PROVINCE_ROLE_CODE = "HSTD_ADMIN_PROVINCE";
    public static String HSTD_ADMIN_ROLE_CODE = "HSTD_ADMIN";

    public enum BIEU_MAU {
        BIEU_MAU_07 {
            public String test() {
                return "FORM 07";
            }
        },
        BIEU_MAU_08 {
            public String test() {
                return "FORM 08";
            }
        },
        BIEU_MAU_09 {
            public String test() {
                return "FORM 09";
            }
        };

        public abstract String test();
    }


}
