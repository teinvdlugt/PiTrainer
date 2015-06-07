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

public class CompleteFragmentSettingsDialog extends DialogFragment {

    private static final String LISTENER = "LISTENER";

    private EditText numOfDigitsET, rangeET, answerLengthET;
    private int numOfDigits, range, answerLength;

    private Listener listener;

    public interface Listener extends Serializable {
        /**
         * Called when OK is clicked and changes
         * must be applied
         */
        void reload();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        listener = (Listener) getArguments().getSerializable(LISTENER);
        numOfDigits = getArguments().getInt(CompleteFragment.NUM_OF_DIGITS_GIVEN);
        range = getArguments().getInt(CompleteFragment.RANGE);
        answerLength = getArguments().getInt(CompleteFragment.LENGTH_OF_ANSWER);

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
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        return builder.create();
    }

    private View getContentView() {
        View theView = getActivity().getLayoutInflater().inflate(R.layout.dialog_complete_settings, null);

        numOfDigitsET = (EditText) theView.findViewById(R.id.digits_given_editText);
        rangeET = (EditText) theView.findViewById(R.id.range_editText);
        answerLengthET = (EditText) theView.findViewById(R.id.lengthOfAnswer_editText);

        numOfDigitsET.setText("" + numOfDigits);
        rangeET.setText("" + range);
        answerLengthET.setText("" + answerLength);

        numOfDigitsET.setSelection(numOfDigitsET.length());

        setTextWatchers();

        return theView;
    }

    private void setTextWatchers() {
        // This TextWatcher has to make sure that a zero can't
        // be filled in as the first number in the TextViews.
        for (final EditText e : new EditText[]{numOfDigitsET, rangeET}) {
            e.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    final String input1 = numOfDigitsET.getText().toString();
                    final String input2 = rangeET.getText().toString();
                    final String input3 = answerLengthET.getText().toString();

                    boolean OKButtonEnabled = ReferenceFragmentSettingsDialog.isValidInteger(input1) // Valid integer input
                            && ReferenceFragmentSettingsDialog.isValidInteger(input2) // Valid integer input
                            && ReferenceFragmentSettingsDialog.isValidInteger(input3) // Valid integer input
                            && !input1.equals("0") && !input2.equals("0") && !input3.equals("0") // No zeros
                            && Integer.parseInt(input1) + Integer.parseInt(input3) < Integer.parseInt(input2) // statement + answer < range
                            && Integer.parseInt(input2) < Digits.currentDigit.getFractionalPart().length(); // range < maximum digits in app

                    if (OKButtonEnabled) {
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

    private void applyChanges() {
        String name = Digits.currentDigit.getName();

        SharedPreferences.Editor prefs = getActivity().getPreferences(0).edit();
        prefs.putInt(CompleteFragment.NUM_OF_DIGITS_GIVEN + name,
                Integer.parseInt(numOfDigitsET.getText().toString()));
        prefs.putInt(CompleteFragment.RANGE + name,
                Integer.parseInt(rangeET.getText().toString()));
        prefs.putInt(CompleteFragment.LENGTH_OF_ANSWER + name,
                Integer.parseInt(answerLengthET.getText().toString()));
        prefs.apply();
    }

    public static void show(Fragment fragment, int numOfDigits, int range, int answerLength) {
        CompleteFragmentSettingsDialog dialog = new CompleteFragmentSettingsDialog();
        Bundle args = new Bundle();
        args.putInt(CompleteFragment.NUM_OF_DIGITS_GIVEN, numOfDigits);
        args.putInt(CompleteFragment.RANGE, range);
        args.putInt(CompleteFragment.LENGTH_OF_ANSWER, answerLength);
        args.putSerializable(LISTENER, (Listener) fragment);
        dialog.setArguments(args);

        dialog.show(fragment.getActivity().getSupportFragmentManager(), "complete_fragment_settings_dialog");
    }
}
