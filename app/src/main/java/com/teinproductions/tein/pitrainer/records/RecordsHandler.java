package com.teinproductions.tein.pitrainer.records;


import android.content.Context;
import android.util.Log;

import com.teinproductions.tein.pitrainer.Digits;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class RecordsHandler {
    private static final String FILE_NAME = "minute_records_"; // Append Digits name

    public static void addRecord(Context context, int digits, int milliseconds, String name) {
        ArrayList<Record> records = loadRecords(context);
        records.add(new Record(digits, milliseconds, name, System.currentTimeMillis()));
        saveFile(context, Record.arrayToJSON(records));
    }

    public static ArrayList<Record> loadRecords(Context context) {
        try {
            return Record.arrayFromJSON(getFile(context));
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static String getFile(Context context) {
        try {
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(
                    context.openFileInput(FILE_NAME + Digits.currentDigit.getName())));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = buffReader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            buffReader.close();

            Log.d("confetti", sb.toString());
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void saveFile(Context context, String toSave) {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME + Digits.currentDigit.getName(), Context.MODE_PRIVATE);
            fos.write(toSave.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
