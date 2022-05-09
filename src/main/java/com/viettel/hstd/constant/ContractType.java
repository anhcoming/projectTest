package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum ContractType {
    UNKNOWN(0),
    CREATED_CONTRACT(1), // Có lẽ sẽ bỏ loại này
    COLLABORATOR(2),
    FREELANCE_CONTRACT(3),
    LABOR_CONTRACT(4),
    PROBATIONARY_CONTRACT(5),
    SERVICE_CONTRACT(6);

    private int value = 4;

    ContractType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case 2: {
                return "Thỏa thuận hợp tác (CTV)";
            }
            case 3: {
                return "Hợp đồng giao khoán (Máy phát điện)";
            }
            case 4: {
                return "Hợp đồng lao động";
            }
            case 5: {
                return "Hợp đồng thử việc";
            }

            default: {
                return "Không xác định thời hạn";
            }
        }
    }

    public static ContractType of(int contractType) {
        return Stream.of(ContractType.values())
                .filter(p -> p.getValue() == contractType)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static ContractType of(String resignFinalFormStatusString) {

        int contractType = 4;
        try {
            contractType = Integer.parseInt(resignFinalFormStatusString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalProbationaryStatusInt = contractType;
        return Stream.of(ContractType.values())
                .filter(p -> p.getValue() == finalProbationaryStatusInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}