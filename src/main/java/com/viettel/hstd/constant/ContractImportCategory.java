package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum ContractImportCategory {
    LABOR(1),
    PROBATION(2),
    PROBATION_TO_LABOR(3),
    CORPORATION_TO_COMPANY(4),
    LABOR_OLD(5);
    private int value = 1;

    ContractImportCategory(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public String getVietnameseStringValue() {
        switch (value) {
            case 1: {
                return "Hợp đồng lao động";
            }
            case 2: {
                return "Hợp đồng thử việc";
            }
            case 3: {
                return "Hợp đồng chuyển diện";
            }
            case 4: {
                return "Hợp đồng chuyển công tác";
            }
            case 5: {
                return "Hợp đồng lao động cũ";
            }
            default: {
                return "Chưa xác định";
            }
        }
    }

    public static ContractImportCategory of(int contractImportCategoryInt) {
        return Stream.of(ContractImportCategory.values())
                .filter(p -> p.getValue() == contractImportCategoryInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static ContractImportCategory of(String contractImportCategoryString) {

        int contractImportCategoryInt = 1;
        try {
            contractImportCategoryInt = Integer.parseInt(contractImportCategoryString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalContractImportCategoryInt = contractImportCategoryInt;
        return Stream.of(ContractImportCategory.values())
                .filter(p -> p.getValue() == finalContractImportCategoryInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
