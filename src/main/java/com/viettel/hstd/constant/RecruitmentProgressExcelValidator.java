package com.viettel.hstd.constant;

import com.viettel.hstd.core.config.Message;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@AllArgsConstructor
public class RecruitmentProgressExcelValidator {

    public static final String POSITION_CODE = "Mã chức danh";
    public static final String ORGANIZATION_CODE = "Mã đơn vị";
    public static final String HR_PLAN = "Định biên";
    public static final String CURRENT_EMP = "Hiện có";
    public static final String DEADLINE = "Thời hạn hoàn thành";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final Message message;


    public String validatePositiveNumber(String value, String col) {
        if (value == null) return message.getMessage("recruitment.progress.field.not.found", new String[]{col});
        try {
            int valueNum = Integer.parseInt(value);
            if (valueNum < 0) return message.getMessage("recruitment.progress.field.not.negative", new String[]{col});
        } catch (NumberFormatException ex) {
            return message.getMessage("recruitment.progress.field.not.negative", new String[]{col});
        }
        return null;
    }

    public String validateDeadline(String value) {
        try {
            LocalDate deadline = LocalDate.parse(value, FORMATTER);
            if (deadline.isBefore(LocalDate.now()))
                return message.getMessage("recruitment.progress.deadline.before.now");
        } catch (Exception ex) {
            return message.getMessage("recruitment.progress.deadline.invalid");
        }
        return null;
    }
}
