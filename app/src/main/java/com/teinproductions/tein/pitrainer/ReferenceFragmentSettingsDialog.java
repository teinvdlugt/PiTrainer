package com.teinproductions.tein.pitrainer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.io.Serializable;

public class ReferenceFragmentSettingsDialog extends DialogFragment {

    private static final String LISTENER = "LISTENER";

    private EditText textSizeET, spacingsET;
    private float textSize;
    private int spacings;

    private Listener listener;

    public interface Listener extends Serializable {
        /*Called when OK is clicked and changes
        * must be applied
        */
        void reload();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        listener = (Listener) getArguments().getSerializable(LISTENER);
        textSize = getArguments().getFloat(ReferenceFragment.TEXT_SIZE);
        spacings = getArguments().getInt(ReferenceFragment.SPACINGS);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(getContentView());
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                applyChanges();
                listener.reload();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        return builder.create();
    }

    private View getContentView() {
        View theView = getActivity().getLayoutInflater().inflate(R.layout.dialog_reference_settings, null);

        textSizeET = (EditText) theView.findViewById(R.id.textSize_textView);
        spacingsET = (EditText) theView.findViewById(R.id.spacings_textView);

        // Format
        String textSizeStr = Math.floor(textSize) == textSize ? "" + (int) textSize : "" + textSize;

        textSizeET.setText(textSizeStr);
        spacingsET.setText("" + spacings);
        textSizeET.setSelection(textSizeET.length());

        setTextWatchers();

        return theView;
    }

    private void setTextWatchers() {
        // If a TextView isn't filled or it contains 0, the OK-button must be disabled
        for (final EditText e : new EditText[]{textSizeET, spacingsET}) {
            e.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    final String input1 = textSizeET.getText().toString();
                    final String input2 = spacingsET.getText().toString();

                    if (isValidFloat(input1) && isValidInteger(input2)
                            && !input1.equals("0") && !input2.equals("0")) {
                        ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    } else {
                        ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }

    private static boolean isValidFloat(String string) {
        try {
            Float.parseFloat(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void applyChanges() {
        SharedPreferences.Editor prefs = getActivity().getPreferences(0).edit();
        prefs.putFloat(ReferenceFragment.TEXT_SIZE,
                Float.parseFloat(textSizeET.getText().toString()));
        prefs.putInt(ReferenceFragment.SPACINGS,
                Integer.parseInt(spacingsET.getText().toString()));
        prefs.apply();
    }

    public static void show(Fragment fragment, float textSize, int spacings) {
        ReferenceFragmentSettingsDialog dialog = new ReferenceFragmentSettingsDialog();
        Bundle args = new Bundle();
        args.putFloat(ReferenceFragment.TEXT_SIZE, textSize);
        args.putInt(ReferenceFragment.SPACINGS, spacings);
        args.putSerializable(LISTENER, (Listener) fragment);
        dialog.setArguments(args);

        dialog.show(fragment.getActivity().getSupportFragmentManager(), "reference_fragment_settings_dialog");
    }
}
