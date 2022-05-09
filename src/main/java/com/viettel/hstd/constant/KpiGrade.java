package com.viettel.hstd.constant;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public enum KpiGrade {
    UNKNOWN("KĐG"),
    A("A"),
    B("B"),
    C("C"),
    D("D");

    private String value = "KĐG";

    KpiGrade(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static KpiGrade of(String operationString) {
        if (operationString == null) return null;
        return Stream.of(KpiGrade.values())
                .filter(p -> {
                    return p.getValue().equals(operationString);
                })
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static Float calculateAverageScore(List<KpiGrade> listGrade) {
        if (listGrade.size() == 0) return 0f;
        float totalScore = 0;
        for (int i = 0; i < listGrade.size(); i++) {
            switch (listGrade.get(i)) {
                case A: {
                    totalScore += 40;
                    break;
                }
                case B: {
                    totalScore += 30;
                    break;
                }
                case C: {
                    totalScore += 20;
                    break;
                }
                case D: {
                    totalScore += 10;
                    break;
                }
                default: {
                    totalScore += 0;
                    break;
                }
            }
        }
        Locale locale  = new Locale("en", "US");
        String pattern = "###.##";
        DecimalFormat df = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
        df.applyPattern(pattern);
        df.setRoundingMode(RoundingMode.CEILING);
        return Float.parseFloat(df.format(totalScore / listGrade.size()));
    }
}
