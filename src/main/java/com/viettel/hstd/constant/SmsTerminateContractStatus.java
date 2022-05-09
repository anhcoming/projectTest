package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum SmsTerminateContractStatus {
    SEND_REQUEST(1),
    SIGNED_REQUEST(2),
    REQUEST_BHXH(3),
    SEND_BHXH(4),
    CONFIRM_BHXH(5),
    CONFIRM_QUIT(6);

    private int value = 1;

    SmsTerminateContractStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case 2: {
                return "Phê duyêt quyết định chấm dứt hợp đồng";
            }
            case 3: {
                return "Yêu cầu hoàn thiện BHXH";
            }
            case 4: {

            }
            default: {
                return "Gửi yêu cầu chấm dứt hợp đồng";
            }
        }
    }

    public static SearchType of(int smsTerminateContractStatusInt) {
        return Stream.of(SearchType.values())
                .filter(p -> p.getValue() == smsTerminateContractStatusInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static SearchType of(String smsTerminateContractStatusString) {

        int smsTerminateContractStatusInt = 1;
        try {
            smsTerminateContractStatusInt = Integer.parseInt(smsTerminateContractStatusString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalSearchTypeInt = smsTerminateContractStatusInt;
        return Stream.of(SearchType.values())
                .filter(p -> p.getValue() == finalSearchTypeInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
    }
