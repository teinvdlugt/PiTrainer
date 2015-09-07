package com.teinproductions.tein.pitrainer.records;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Record {
    private static final String DIGITS_JSON = "digits";
    private static final String MILLISECONDS_JSON = "milliseconds";
    private static final String RECORD_HOLDER_NAME_JSON = "recordHolder";
    private static final String DATE_MILLIS = "date_unixStamp";

    private int digits;
    private int milliseconds;
    private String recordHolder;
    private long dateMillis;

    public Record(int digits, int milliseconds, String recordHolder, long dateMillis) {
        this.digits = digits;
        this.milliseconds = milliseconds;
        this.recordHolder = recordHolder;
        this.dateMillis = dateMillis;
    }

    public Record() {
    }

    public int getDigits() {
        return digits;
    }

    public int getMilliseconds() {
        return milliseconds;
    }

    public double getDigitsPerMinute() {
        return (double) digits / milliseconds * 60000;
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public String getDateString() {
        return DateFormat.getDateInstance().format(new Date(dateMillis));
    }

    public String getRecordHolder() {
        return recordHolder;
    }

    private String toJSON() {
        return "{\"" + DIGITS_JSON + "\":" + digits + ",\"" + MILLISECONDS_JSON + "\":" + milliseconds +
                ",\"" + RECORD_HOLDER_NAME_JSON + "\":" + recordHolder + ",\"" + DATE_MILLIS + "\":" + dateMillis + "}";
    }

    public static Record fromJSON(String json) throws JSONException {
        JSONObject jObject = new JSONObject(json);
        return fromJSON(jObject);
    }

    private static Record fromJSON(JSONObject json) throws JSONException {
        Record result = new Record();
        result.digits = json.getInt(DIGITS_JSON);
        result.milliseconds = json.getInt(MILLISECONDS_JSON);
        result.recordHolder = json.getString(RECORD_HOLDER_NAME_JSON);
        result.dateMillis = json.getLong(DATE_MILLIS);
        return result;
    }

    public static String arrayToJSON(ArrayList<Record> data) {
        StringBuilder sb = new StringBuilder("[");
        for (Record record : data) {
            sb.append(record.toJSON());
            sb.append(",");
        }
        // Delete last comma
        sb.deleteCharAt(sb.length() - 1);

        return sb.append("]").toString();
    }

    public static ArrayList<Record> arrayFromJSON(String json) throws JSONException {
        ArrayList<Record> result = new ArrayList<>();

        JSONArray jArray = new JSONArray(json);
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jObject = jArray.getJSONObject(i);
            result.add(fromJSON(jObject));
        }

        return result;
    }
}
