package com.teinproductions.tein.pitrainer;


import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import androidx.annotation.StringRes;

public class Digits implements Serializable {

    private static final String FILE_NAME = "saved_digits";
    private static final String DIGITS = "digits";
    private static final String NAME = "name";
    private static final String INTEGER_PART = "integer_part";
    private static final String FRACTIONAL_PART = "fractional_part";

    public static Digits[] digits;

    /**
     * The name of the Digits which is currently selected in the application
     */
    public static Digits currentDigit;

    /**
     * The name of a Digits has to be unique
     */
    private String name;

    /**
     * The integerPart are/is the digit(s) in front of the decimal point,
     * the fractionalPart are the digits after it.
     */
    private String integerPart, fractionalPart;

    public static String decimalSeparator = "."; // Point or comma, depending on locale. Initialised in initDigits().

    public Digits() {
    }

    public Digits(String name, String integerPart, String fractionalPart) {
        this.name = name;
        this.integerPart = integerPart;
        this.fractionalPart = fractionalPart;
    }

    public static void initDigits(Context context) {
        Digits[] preloaded = preloaded(context);
        Digits[] saved = savedDigits(context);

        digits = new Digits[preloaded.length + saved.length];
        System.arraycopy(saved, 0, digits, 0, saved.length);
        System.arraycopy(preloaded, 0, digits, saved.length, preloaded.length);

        // Determine what decimal separator to use
        // https://stackoverflow.com/questions/8188137/decimal-point-or-decimal-comma-in-android
        NumberFormat nf = NumberFormat.getInstance();
        if (nf instanceof DecimalFormat) {
            DecimalFormatSymbols sym = ((DecimalFormat) nf).getDecimalFormatSymbols();
            char decSeparator = sym.getDecimalSeparator();
            if (decSeparator == '.' || decSeparator == ',')
                decimalSeparator = Character.toString(decSeparator);
        }
    }

    /**
     * This method doesn't handle exceptions, for example a StringIndexOutOfBoundsException when
     * stringToCheck is too large for currentDigit.fractionalPart. So make sure that
     * stringToCheck.length() <= fractionalPart.length() - startDigit + 1, before calling this method.
     */
    public static boolean isIncorrect(String stringToCheck, int startDigit) {
        for (int i = 0; i < stringToCheck.length(); i++) {
            if (stringToCheck.charAt(i) != currentDigit.getFractionalPart().charAt(i + startDigit - 1)) {
                return true;
            }
        }

        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntegerPart() {
        return integerPart;
    }

    public void setIntegerPart(String integerPart) {
        this.integerPart = integerPart;
    }

    public String getFractionalPart() {
        return fractionalPart;
    }

    public void setFractionalPart(String fractionalPart) {
        this.fractionalPart = fractionalPart;
    }

    private static Digits[] preloaded(Context context) {

        return new Digits[]{
                // Sources for pi: https://www.piday.org/million/ and
                // https://introcs.cs.princeton.edu/java/data/pi-1million.txt and
                // https://pi2e.ch/blog/2017/03/10/pi-digits-download/
                loadPreloadedDigitsFromAssets(context, "digits_pi", R.string.pi),

                // Source for tau: https://tauday.com/tau-digits
                loadPreloadedDigitsFromAssets(context, "digits_tau", R.string.tau),

                // Source for e: https://apod.nasa.gov/htmltest/gifcity/e.2mil and
                // https://www.math.utah.edu/~pa/math/e.html
                loadPreloadedDigitsFromAssets(context, "digits_e", R.string.euler_number),

                // Source for sqrt 2: https://apod.nasa.gov/htmltest/gifcity/sqrt2.1mil and
                // https://nerdparadise.com/math/reference/2sqrt10000 (for first 10K)
                loadPreloadedDigitsFromAssets(context, "digits_sqrt2", R.string.sqrt2),

                // Source for phi: https://www2.cs.arizona.edu/icon/oddsends/phi.htm and
                // https://www.goldennumber.net/phi-million-places/
                loadPreloadedDigitsFromAssets(context, "digits_phi", R.string.golden_ratio),

                // Source for gamma: http://www.plouffe.fr/simon/constants/gamma.txt
                loadPreloadedDigitsFromAssets(context, "digits_euler-mascheroni", R.string.euler_mascheroni_constant)
        };

        // TODO Euler-Mascheroni constant
    }

    /**
     * The file at assetName should contain only one line! (Only first line will be read)
     */
    private static Digits loadPreloadedDigitsFromAssets(Context context, String assetName, @StringRes int digitsName) {
        try {
            InputStream is = context.getAssets().open(assetName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            reader.close();
            String[] parts = line.split("[.]");

            return new Digits(context.getString(digitsName), parts[0], parts[1]);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Should never happen
        }
    }

    public static Digits[] savedDigits(Context context) {
        String file = getFile(context);
        if (file == null) return new Digits[0];

        try {
            JSONObject jObject = new JSONObject(file);
            JSONArray jArray = jObject.getJSONArray(DIGITS);
            Digits[] digits = new Digits[jArray.length()];
            for (int i = 0; i < jArray.length(); i++) {
                digits[i] = fromJSON(jArray.getJSONObject(i));
            }
            return digits;
        } catch (JSONException e) {
            e.printStackTrace();
            return new Digits[0];
        }
    }

    private static Digits fromJSON(JSONObject jObject) {
        try {
            String name = jObject.getString(NAME);
            String integerPart = jObject.getString(INTEGER_PART);
            String fractionalPart = jObject.getString(FRACTIONAL_PART);

            return new Digits(name, integerPart, fractionalPart);
        } catch (JSONException e) {
            return null;
        }
    }

    private String toJSON() {
        return String.format("{\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\"}",
                NAME, name, INTEGER_PART, integerPart, FRACTIONAL_PART, fractionalPart);
    }

    private static String arrayToJSON(Digits[] array) {
        StringBuilder sb = new StringBuilder(String.format("{\"%s\":[", DIGITS));
        for (Digits digits : array) {
            sb.append(digits.toJSON());
        }
        return sb.append("]}").toString().replace("}{", "},{");
    }

    private static String getFile(Context context) {
        StringBuilder sb;

        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);

            sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addDigits(Context context, Digits newDigits) {
        Digits[] saved = savedDigits(context);
        Digits[] newSaved = new Digits[saved.length + 1];
        System.arraycopy(saved, 0, newSaved, 0, saved.length);
        newSaved[newSaved.length - 1] = newDigits;

        save(context, newSaved);

        String currentName = currentDigit.getName();
        initDigits(context);
        currentDigit = findDigits(currentName);
    }

    public static void save(Context context, Digits[] toSave) {
        String json = arrayToJSON(toSave);

        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.save_error_message, Toast.LENGTH_SHORT).show();
        }
    }

    public static String[] digitsNames() {
        String[] result = new String[digits.length];
        for (int i = 0; i < digits.length; i++) {
            result[i] = digits[i].getName();
        }
        return result;
    }

    public static int currentDigitsIndex() {
        for (int i = 0; i < digits.length; i++) {
            if (digits[i].equals(currentDigit)) return i;
        }

        return 0;
    }

    public static Digits findDigits(String name) {
        for (Digits digits : Digits.digits) {
            if (digits.getName().equals(name)) return digits;
        }
        return digits[0];
    }
}
