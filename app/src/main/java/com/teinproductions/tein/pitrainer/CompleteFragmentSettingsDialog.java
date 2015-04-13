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

    private EditText numOfDigitsET, rangeET;
    private int numOfDigits, range;

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
        numOfDigits = getArguments().getInt(CompleteFragment.NUM_OF_DIGITS_GIVEN);
        range = getArguments().getInt(CompleteFragment.RANGE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(getContentView());
        builder.setPositiveButton(android.R.string.ok, null); // onClick is handled in onStart()
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

        if (numOfDigits != -1 && range != -1) {
            numOfDigitsET.setText("" + numOfDigits);
            rangeET.setText("" + range);
        }
        numOfDigitsET.setSelection(numOfDigitsET.length());

        setTextWatchers();

        return theView;
    }

    private void setTextWatchers() {
        // This TextWatcher must make sure that a zero can't
        // be filled in as the first number in both TextViews.
        for (final EditText e : new EditText[]{numOfDigitsET, rangeET}) {
            e.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (e.getText().toString().equals("0")) {
                        e.setText("");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (numOfDigitsET.length() > 0 && rangeET.length() > 0) {
                        applyChanges();
                        listener.reload();
                        dismiss();
                    }
                }
            });
        }
    }

    private void applyChanges() {
        SharedPreferences.Editor prefs = getActivity().getPreferences(0).edit();
        prefs.putInt(CompleteFragment.NUM_OF_DIGITS_GIVEN,
                Integer.parseInt(numOfDigitsET.getText().toString()));
        prefs.putInt(CompleteFragment.RANGE,
                Integer.parseInt(rangeET.getText().toString()));
        prefs.apply();
    }

    public static void show(Fragment fragment, int numOfDigits, int range) {
        CompleteFragmentSettingsDialog dialog = new CompleteFragmentSettingsDialog();
        Bundle args = new Bundle();
        args.putInt(CompleteFragment.NUM_OF_DIGITS_GIVEN, numOfDigits);
        args.putInt(CompleteFragment.RANGE, range);
        args.putSerializable(LISTENER, (Listener) fragment);
        dialog.setArguments(args);

        dialog.show(fragment.getActivity().getSupportFragmentManager(), "complete_fragment_settings_dialog");
    }
}
