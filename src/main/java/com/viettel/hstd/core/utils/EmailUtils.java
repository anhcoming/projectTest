package com.viettel.hstd.core.utils;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailUtils {
    public static String replaceContent(String content, Map<String, String> hm) {
        Matcher matcher = Pattern.compile("#([a-zA-Z_]*?)#").matcher(content);
        String value = "";
        Map<String, Object> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        map.putAll(hm);
        while (matcher.find()) {
            value = removeHash(matcher.group());
            if (map.containsKey(value)) {
                content = content.replaceAll(matcher.group(), map.get(value).toString());
            } else {
                content = content.replaceAll(matcher.group(), " ");
            }
        }
        return content;
    }

    public static String replaceContent(String content, Map<String, String> hm, Boolean isReplace) {
        Matcher matcher = Pattern.compile("#([a-zA-Z_]*?)#").matcher(content);
        String value = "";
        Map<String, Object> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        map.putAll(hm);
        while (matcher.find()) {
            value = removeHash(matcher.group());
            if (map.containsKey(value)) {
                content = content.replaceAll(matcher.group(), map.get(value).toString());
            } else if (isReplace) {
                content = content.replaceAll(matcher.group(), " ");
            }
        }
        return content;
    }

    private static String removeHash(String str) {
        return str.replace("#", "").replace("#", "");
    }
}
