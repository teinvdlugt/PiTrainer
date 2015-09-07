package com.teinproductions.tein.pitrainer.records;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.teinproductions.tein.pitrainer.R;

public class RecordDialog extends DialogFragment {
    private static final String RECORD_HOLDER_NAME = "RECORD_HOLDER_NAME";
    private static final String NUM_OF_DIGITS = "NUM_OF_DIGITS";
    private static final String MILLISECONDS = "MILLISECONDS";

    private EditText nameEditText;

    private int numOfDigits;
    private int milliseconds;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        numOfDigits = getArguments().getInt(NUM_OF_DIGITS);
        milliseconds = getArguments().getInt(MILLISECONDS);

        return new AlertDialog.Builder(getActivity())
                .setView(getContentView())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameEditText.getText().toString();
                        getActivity().getPreferences(Context.MODE_PRIVATE).edit().putString(RECORD_HOLDER_NAME, name).apply();
                        RecordsHandler.addRecord(getActivity(), numOfDigits, milliseconds, name);
                        ((OnAppliedListener) getActivity()).reloadRecords();
                    }
                })
                .setNegativeButton(R.string.do_not_save_record, null)
                .setCancelable(false)
                .create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private View getContentView() {
        View theView = getActivity().getLayoutInflater().inflate(R.layout.dialog_record, null);
        nameEditText = (EditText) theView.findViewById(R.id.name_editText);
        TextView messageTV = (TextView) theView.findViewById(R.id.message_textView);

        messageTV.setText("You typed " + numOfDigits + " digits in " + (milliseconds / 1000d) + " seconds.");

        setTextWatcher();
        setNameText();
        return theView;
    }

    private void setTextWatcher() {
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (nameEditText.length() == 0) {
                        ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    } else {
                        ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {/*ignored*/}

            public void afterTextChanged(Editable s) {/*ignored*/}
        });
    }

    private void setNameText() {
        String name = getActivity().getPreferences(Context.MODE_PRIVATE).getString(RECORD_HOLDER_NAME, "");
        nameEditText.setText(name);
    }

    public static void show(TimeFragment fragment, int numOfDigits, int milliseconds) {
        RecordDialog dialog = new RecordDialog();

        Bundle args = new Bundle();
        args.putInt(NUM_OF_DIGITS, numOfDigits);
        args.putInt(MILLISECONDS, milliseconds);
        dialog.setArguments(args);

        FragmentTransaction ft = fragment.getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(dialog, "RECORD_DIALOG");
        ft.commitAllowingStateLoss();
    }


    public interface OnAppliedListener {
        void reloadRecords();
    }
}
