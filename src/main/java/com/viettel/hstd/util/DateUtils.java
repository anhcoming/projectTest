package com.viettel.hstd.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.tomcat.jni.Local;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {
    public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyyMMdd = "yyyy-MM-dd";
    public static final String yyyyMMddHHmm = "yyyy-MM-dd HH:mm";
    public static final String hhMMyyyyMMdd = "HH:mm yyyy/MM/dd ";

    public static List<String> patternList = Arrays.asList("y-M-d H:m:s", "y-M-d", "y/M/d H:m:s", "d/m/y H:m:s", "yyyy-MM-dd'T'HH:mm:ss.SS'Z'");


    public static String getNowFullTime() {
        SimpleDateFormat formatter = new SimpleDateFormat(yyyyMMddHHmmss);
        return formatter.format(Calendar.getInstance().getTime());
    }

    public static Date convertStringToDate(String format, String date) {
        if (date == null || format == null) return null;
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Timestamp convertStringToTimeStamp(String stringTime) {
        try {
            stringTime = stringTime.replace("T", " ").replace("Z", "");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedDate = dateFormat.parse(stringTime);
            return new Timestamp(parsedDate.getTime());
        } catch (Exception e) { //this generic but you can control another types of exception
            return null;
        }
    }

    public static Date convertString2Date(String stringDate) {
        stringDate = stringDate.replace("T", " ").replace("Z", "");
        for (int i = 0; i < patternList.size(); i++) {
            SimpleDateFormat format = new SimpleDateFormat(patternList.get(i));
            try {
                Date date = format.parse(stringDate);
                return date;
            } catch (ParseException e) {
                if (i == patternList.size() - 1) e.printStackTrace();
            }
        }

        return new Date();

    }

    public static String getNowTime(String format) {
        if (format == null) return null;
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(Calendar.getInstance().getTime());
    }

    public static Timestamp getNow() {
        return new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    public static LocalDate getFirstLocalDate(int quarter, int year) {
        int firstMonthOfQuarter = 1 + ((quarter - 1) * 3);
        return LocalDate.of(year, firstMonthOfQuarter, 1);
    }

    public static LocalDate getLastLocalDate(int quarter, int year) {
        int lastMonthOfQuarter = 3 + ((quarter - 1) * 3);
        LocalDate localDate = LocalDate.of(year, lastMonthOfQuarter, 1);
        return localDate.plusMonths(1).minusDays(1);
    }

    public static LocalDate convertStringToLocalDate(String stringDate) {
        stringDate = stringDate.replace("T", " ").replace("Z", "");
        for (int i = 0; i < patternList.size(); i++) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern(patternList.get(i));
            try {
                LocalDate date = LocalDate.parse(stringDate, format);
                return date;
            } catch (Exception e) {
                if (i == patternList.size() - 1) e.printStackTrace();
            }
        }

        return null;
    }

    public static Date convertCellDate(Cell cell, String format) {
        if (cell == null) return null;
        try {
            if (cell.getCellType().equals(CellType.STRING)) {
                return new SimpleDateFormat(format).parse(cell.getStringCellValue());
            } else if (cell.getCellType().equals(CellType.NUMERIC)) {
                return cell.getDateCellValue();
            } else {
                return null;
            }
        } catch (ParseException exception) {
            return null;
        }

    }

    public static LocalDate convertDateToLocalDate(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static String convertDateToString(Date date, String format) {
        if (date == null) return null;
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static String convertLocalDateToString(LocalDate date, String format) {
        if (date == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }
}
