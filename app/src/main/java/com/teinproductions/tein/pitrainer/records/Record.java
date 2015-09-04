package com.teinproductions.tein.pitrainer.records;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Record {
    private static final String DIGITS_JSON = "digits";
    private static final String MILLISECONDS_JSON = "milliseconds";

    private int digits;
    private int milliseconds;

    public Record(int digits, int milliseconds) {
        this.digits = digits;
        this.milliseconds = milliseconds;
    }

    public Record() {
    }

    public int getDigits() {
        return digits;
    }

    public void setDigits(int digits) {
        this.digits = digits;
    }

    public int getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    public String toJSON() {
        return "{\"" + DIGITS_JSON + "\":" + digits + ",\"" + MILLISECONDS_JSON + "\":" + milliseconds + "}";
    }

    public static Record fromJSON(String json) throws JSONException {
        JSONObject jObject = new JSONObject(json);
        return fromJSON(jObject);
    }

    public static Record fromJSON(JSONObject json) throws JSONException {
        Record result = new Record();
        result.setDigits(json.getInt(DIGITS_JSON));
        result.setMilliseconds(json.getInt(MILLISECONDS_JSON));
        return result;
    }

    public static String arrayToJSON(ArrayList<Record> data) {
        StringBuilder sb = new StringBuilder("[");
        for (Record record : data) {
            sb.append(record.toJSON());
            sb.append(",");
        }

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
