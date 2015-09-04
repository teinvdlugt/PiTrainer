package com.teinproductions.tein.pitrainer.records;


import android.content.Context;

import com.teinproductions.tein.pitrainer.Digits;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class RecordsHandler {
    private static final String FILE_NAME = "minute_records_"; // Append Digits name

    public static boolean addRecord(Context context, int digits, int milliseconds) {
        try {
            String file = getFile(context);
            ArrayList<Record> records;
            if (file == null) records = new ArrayList<>();
            else records = Record.arrayFromJSON(getFile(context));
            records.add(new Record(digits, milliseconds));
            saveFile(context, Record.arrayToJSON(records));

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getFile(Context context) {
        try {
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(
                    context.openFileInput(FILE_NAME + Digits.currentDigit.getName())));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = buffReader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            buffReader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveFile(Context context, String toSave) {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME + Digits.currentDigit.getName(), Context.MODE_PRIVATE);
            fos.write(toSave.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
