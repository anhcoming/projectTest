package com.viettel.hstd.core.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GsonTimestampDeSerializer extends TypeAdapter<Timestamp> {
    public static List<String> patternList = Arrays.asList("y-M-d H:m:s", "y-M-d", "d/M/y H:m:s");

    @Override
    public void write(JsonWriter out, Timestamp value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.getTime());
    }

    @Override
    public Timestamp read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        if (in.nextString().matches("\\d*")){
            return new Timestamp(in.nextLong());
        } else {
            try {
                return parseDate(in.nextString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private Timestamp parseDate(String dateString) throws ParseException {

            for (int i = 0; i < patternList.size(); i++) {
                SimpleDateFormat format = new SimpleDateFormat(patternList.get(i));
                try {
                    Date date = format.parse(dateString);
                    return (Timestamp) date;
                } catch (ParseException e) {
                    if (i == patternList.size() - 1)
                        e.printStackTrace();
                }
            }



        return null;
    }
}
