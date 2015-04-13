package com.teinproductions.tein.pitrainer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;

import java.io.Serializable;

public class ReferenceFragmentSettingsDialog extends DialogFragment {

    private static final String LISTENER = "LISTENER";

    private EditText textSizeET, spacingsET;
    private int textSize, spacings;

    private Listener listener;

    public interface Listener extends Serializable {
        /*Called when OK is clicked and changes
        * must be applied
        */
        public void reload();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        listener = (Listener) getArguments().getSerializable(LISTENER);
        textSize = getArguments().getInt(ReferenceFragment.TEXT_SIZE);
        spacings = getArguments().getInt(ReferenceFragment.SPACINGS);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(getContentView());
        builder.setPositiveButton(android.R.string.ok, null); // onClick is handled in onStart()
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

        textSizeET.setText("" + textSize);
        spacingsET.setText("" + spacings);
        textSizeET.setSelection(textSizeET.length());

        return theView;
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (textSizeET.length() > 0 && spacingsET.length() > 0) {
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
        prefs.putInt(ReferenceFragment.TEXT_SIZE,
                Integer.parseInt(textSizeET.getText().toString())); // TODO float
        prefs.putInt(ReferenceFragment.SPACINGS,
                Integer.parseInt(spacingsET.getText().toString()));
        prefs.apply();
    }

    public static void show(Fragment fragment, int textSize, int spacings) {
        ReferenceFragmentSettingsDialog dialog = new ReferenceFragmentSettingsDialog();
        Bundle args = new Bundle();
        args.putInt(ReferenceFragment.TEXT_SIZE, textSize);
        args.putInt(ReferenceFragment.SPACINGS, spacings);
        args.putSerializable(LISTENER, (Listener) fragment);
        dialog.setArguments(args);

        dialog.show(fragment.getActivity().getSupportFragmentManager(), "reference_fragment_settings_dialog");
    }
}
