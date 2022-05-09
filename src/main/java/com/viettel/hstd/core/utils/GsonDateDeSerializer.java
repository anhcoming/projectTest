package com.viettel.hstd.core.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GsonDateDeSerializer implements JsonDeserializer<Date> {

    public static List<String> patternList = Arrays.asList("y-M-d H:m:s", "y-M-d", "d/M/y H:m:s", "y/M/d H:m:s", "M d, y h:m:s a");

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            String j = json.getAsJsonPrimitive().getAsString();
            return parseDate(j);
        } catch (ParseException e) {
            throw new JsonParseException(e.getMessage(), e);
        }
    }

    private Date parseDate(String dateString) throws ParseException {
        if (dateString.matches("\\d*")) {
            return new Timestamp(Long.parseLong(dateString));
        } else {
            for (int i = 0; i < patternList.size(); i++) {
                SimpleDateFormat format = new SimpleDateFormat(patternList.get(i));
                try {
                    Date date = format.parse(dateString);
                    return date;
                } catch (ParseException e) {
                    if (i == patternList.size() - 1)
                        e.printStackTrace();
                }
            }
        }
        return null;
    }


}
