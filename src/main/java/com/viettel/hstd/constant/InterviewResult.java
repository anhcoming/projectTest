package com.viettel.hstd.constant;

import java.util.stream.Stream;

public enum InterviewResult {
    NOT_EVALUATE_YET(2),
    PASS(1),
    FAIL(0);

    private int value = 2;

    InterviewResult(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getVietnameseStringValue() {
        switch (value) {
            case 1: {
                return "Trúng tuyển";
            }
            case 0: {
                return "Không trúng tuyển";
            }
            default: {
                return "Chưa có kết quả";
            }
        }
    }

    public static InterviewResult of(int interviewResultInt) {
        return Stream.of(InterviewResult.values())
                .filter(p -> p.getValue() == interviewResultInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static InterviewResult of(String interviewResultString) {

        int interviewResultInt = 2;
        try {
            interviewResultInt = Integer.parseInt(interviewResultString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        int finalInterviewResultInt = interviewResultInt;
        return Stream.of(InterviewResult.values())
                .filter(p -> p.getValue() == finalInterviewResultInt)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
