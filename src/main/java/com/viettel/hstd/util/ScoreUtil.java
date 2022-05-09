package com.viettel.hstd.util;

import com.viettel.hstd.constant.KpiGrade;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class ScoreUtil {
    public static Float calculateAverageScore(List<String> listGrade) {
        if (listGrade.size() == 0) return 0f;
        float totalScore = 0;
        for (int i = 0; i < listGrade.size(); i++) {
            switch (listGrade.get(i)) {
                case "A": {
                    totalScore += 40;
                    break;
                }
                case "B": {
                    totalScore += 30;
                    break;
                }
                case "C": {
                    totalScore += 20;
                    break;
                }
                case "D": {
                    totalScore += 10;
                    break;
                }
                default: {
                    totalScore += 0;
                    break;
                }
            }
        }
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        return Float.parseFloat(df.format(totalScore / listGrade.size()));
    }
}
