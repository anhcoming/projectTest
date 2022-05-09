package com.viettel.hstd.core.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.tomcat.jni.Local;

import java.security.SecureRandom;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class StringUtils {
    public static String pattern = "yyyy-MM-dd HH:mm:ss";
    public static List<String> patternList = Arrays.asList("y-M-d H:m:s", "y-M-d", "y/M/d H:m:s", "d/m/y H:m:s", "d/M/y");

    public static long convertDateToLong(String date, String format) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        // Convert date time
        java.util.Date dateDf = simpleDateFormat.parse(date);
        return dateDf.getTime();
    }

    public static String optionalToString(Object input) {
        if (input == null) {
            return null;
        } else {
            return input.toString();
        }
    }

    public static java.util.Date convertString2Date(String stringDate) {
        for (int i = 0; i < patternList.size(); i++) {
            SimpleDateFormat format = new SimpleDateFormat(patternList.get(i));
            try {
                java.util.Date date = format.parse(stringDate);
                return date;
            } catch (ParseException e) {
                if (i == patternList.size() - 1)
                    e.printStackTrace();
            }
        }

        return new java.util.Date();

    }

    public static Timestamp convertLongToTime(Long time) {
        return time == null ? null : new Timestamp(time);
    }

    public static String convertLongToDateString(Long time, String format) {
        if (time == null) return null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date(time));
    }

    public static Date generateDate(int year, int month, int date) {
        return new Date(year - 1900, month - 1, date);
    }

    //    public static String date2String(java.util.Date date){
//
//    }
    public static Integer getNumDayInMonth(int year, int month) {
        YearMonth yearMonthObject = YearMonth.of(year, month);
        return yearMonthObject.lengthOfMonth();
    }

    public static String camelToSnake(String str) {

        // Empty String
        String result = "";

        // Append first character(in lower case)
        // to result string
        char c = str.charAt(0);
        result = result + Character.toLowerCase(c);

        // Tarverse the string from
        // ist index to last index
        for (int i = 1; i < str.length(); i++) {

            char ch = str.charAt(i);

            // Check if the character is upper case
            // then append '_' and such character
            // (in lower case) to result string
            if (Character.isUpperCase(ch)) {
                result = result + '_';
                result
                        = result
                        + Character.toLowerCase(ch);
            }

            // If the character is lower case then
            // add such character into result string
            else {
                result = result + ch;
            }
        }

        // return the result
        return result;
    }

    public static String generateRandomPassword(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        // each iteration of the loop randomly chooses a character from the given
        // ASCII range and appends it to the `StringBuilder` instance
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        return sb.toString();
    }

    public static String generateAccountName(String fullname, String prefix) {
        if (fullname != null && fullname.length() > 0) {
            fullname = fullname.toLowerCase();
            fullname = VNCharacterUtils.removeAccent(fullname);
            String accountName = "";
            String[] arrFullname = fullname.split(" ");
            int length = arrFullname.length;
            accountName = arrFullname[length - 1];
            for (int i = 0; i < length - 1; i++) {
                accountName += arrFullname[i].charAt(0);
            }
            return prefix + "_" + accountName;
        }
        return null;
    }

    public static Integer convertToInt(String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException num) {

            }
        }
        return null;
    }

    public static Long convertToLong(String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException num) {

            }
        }
        return null;
    }

    public static Boolean convertToBoolean(String value) {
        if (value != null && value.trim().length() > 0) {
            if (value.equals("1")) {
                return true;
            } else if (value.equals("0")) {
                return false;
            }
            try {
                return Boolean.parseBoolean(value);
            } catch (Exception ex) {

            }
        }
        return null;
    }

    public static LocalDate convertToLocalDate(String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return Instant.ofEpochMilli(Long.parseLong(value))
                        .atZone(ZoneId.systemDefault()).toLocalDate();
            } catch (Exception num) {

            }
        }
        return null;
    }

    public static String dividedPathIntoArray(String path, int level) {
        String[] strings = path.split("/");
        if (level > strings.length) {
            return null;
        } else {
            return strings[level];
        }
    }

    public static String unAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        temp = pattern.matcher(temp).replaceAll("");
        return temp.replaceAll("Đ", "D").replace("đ", "d").trim();
    }

    public static String numberCurrency(Locale locale, float value) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
        df.applyPattern("###,###.##");
        return df.format(value);
    }

    public static Boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static Boolean isBlank(String s) {
        return s == null || s.isEmpty() || s.trim().isEmpty();
    }

    public static String convertCellToString(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType().equals(CellType.STRING)) {
            String value = cell.getStringCellValue();
            if (value == null || value.isEmpty() || value.trim().isEmpty()) {
                return null;
            }
            return value;
        } else if (cell.getCellType().equals(CellType.NUMERIC)) {
            return cell.getNumericCellValue() + "";
        } else {
            return null;
        }
    }

    public static boolean isInteger(String s) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), 10) < 0) return false;
        }
        return true;
    }

    public static boolean isDouble(String s) {
        try {
            Double.valueOf(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public static boolean isOnlyNumber(String s) {
        if (s == null) return false;
        return s.matches("[0-9]+");
    }
}
