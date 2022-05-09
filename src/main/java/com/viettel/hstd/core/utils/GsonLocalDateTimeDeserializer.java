package com.viettel.hstd.core.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GsonLocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
    public static List<String> patternList = Arrays.asList("y-M-d H:m:s", "y-M-d", "d/M/y H:m:s");

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            String j = json.getAsJsonPrimitive().getAsString();
            return parseLocalDateTime(j);
        } catch (DateTimeParseException e) {
            throw new JsonParseException(e.getMessage(), e);
        }
    }

    private LocalDateTime parseLocalDateTime(String dateString) throws DateTimeParseException {
        for (int i = 0; i < patternList.size(); i++) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patternList.get(i), Locale.ENGLISH);
            try {
                return LocalDateTime.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                if (i == patternList.size() - 1)
                    throw e;
                e.printStackTrace();
            }
        }

        return null;
    }
}
