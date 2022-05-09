package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum ContractDuration {
    PERMANENCE(0),
    ONE_MONTH(1),
    TWO_MONTH(2),
    THREE_MONTH(3),
    SIX_MONTH(6),
    ONE_YEAR(12),
    TWO_YEAR(24);

    private int value = 0;

    ContractDuration(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case 1: {
                return "Hợp đồng thử việc 1 tháng";
            }
            case 2: {
                return "Hợp đồng thử việc 2 tháng";
            }
            case 3: {
                return "Hợp đồng thử việc 3 tháng";
            }
            case 6: {
                return "Hợp đồng thử việc 6 tháng";
            }
            case 12: {
                return "Hợp đồng lao động 12 tháng";
            }
            case 24: {
                return "Hợp đồng lao động 24 tháng";
            }
            default: {
                return "Không xác định thời hạn";
            }
        }
    }

    public static ContractDuration of(int contractDurationInt) {
        return Stream.of(ContractDuration.values())
            .filter(p -> p.getValue() == contractDurationInt)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Không có hợp đồng với thời hạn " + contractDurationInt + " tháng"));
    }

    public static ContractDuration of(String contractDurationString) {

        int contractDurationInt = 1;
        try {
            contractDurationInt = Integer.parseInt(contractDurationString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalContractDurationInt = contractDurationInt;
        return Stream.of(ContractDuration.values())
            .filter(p -> p.getValue() == finalContractDurationInt)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
