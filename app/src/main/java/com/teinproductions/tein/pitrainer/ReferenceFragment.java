package com.teinproductions.tein.pitrainer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.TextView;

public class ReferenceFragment extends Fragment
        implements FragmentInterface, ReferenceFragmentSettingsDialog.Listener {

    public static final String TEXT_SIZE = "TEXT_SIZE";
    public static final String SPACINGS = "SPACINGS";

    private float textSize;
    private int spacings;

    private TextView integerPart, fractionalPart;
    private CardView settingsLayout;
    private ImageButton openSettingsButton;
    private ImageButton closeSettingsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reference, container, false);

        integerPart = root.findViewById(R.id.integerPart_textView);
        fractionalPart = root.findViewById(R.id.fractionalPart_textView);
        settingsLayout = root.findViewById(R.id.settings_layout);
        openSettingsButton = root.findViewById(R.id.openSettings_button);
        closeSettingsButton = root.findViewById(R.id.closeSettings_button);

        restoreValues();
        notifyDigitsChanged();

        openSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickOpenSettings();
            }
        });
        closeSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCloseSettings();
            }
        });

        return root;
    }

    public void restoreValues() {
        textSize = getActivity().getPreferences(0).getFloat(TEXT_SIZE, 18);
        spacings = getActivity().getPreferences(0).getInt(SPACINGS, 10);

        fractionalPart.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    @Override
    public void notifyDigitsChanged() {
        integerPart.setText(Digits.currentDigit.getIntegerPart() + "."); // TODO Use point or comma depending on locale
        setFractionalPartText();
    }

    private void setFractionalPartText() {
        StringBuilder sb = new StringBuilder(Digits.currentDigit.getFractionalPart());
        if (spacings > 0) { // If 0, then no spacings
            for (int i = spacings; i < sb.length(); i += spacings + 1) {
                sb.insert(i, " ");
            }
        }
        fractionalPart.setText(sb);
    }

    private void onClickOpenSettings() {
        // Prepare for animation:
        // Determine final values for animation
        settingsLayout.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = settingsLayout.getMeasuredHeight();

        // Set initial values for animation
        settingsLayout.setAlpha(0f);
        settingsLayout.setVisibility(View.VISIBLE);
        settingsLayout.getLayoutParams().height = 1; // On older versions of Android, the animation gets cancelled if we set height to 0.
        openSettingsButton.setAlpha(1f);
        openSettingsButton.setVisibility(View.VISIBLE);

        // Create the animation
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                // Update settingsLayout appearance
                settingsLayout.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                settingsLayout.requestLayout();
                settingsLayout.setAlpha(interpolatedTime);
                // Update openSettingsButton
                openSettingsButton.setAlpha(1 - (float) Math.cbrt(interpolatedTime));

                // At the end:
                if (interpolatedTime == 1)
                    openSettingsButton.setVisibility(View.GONE);
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime));

        // Start the animation!
        settingsLayout.startAnimation(animation);

        // ReferenceFragmentSettingsDialog.show(this, textSize, spacings);
        // When on this dialog OK is clicked, reload() is called
    }

    private void onClickCloseSettings() {
        // Prepare for animation:
        // Determine initial height
        settingsLayout.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int initialHeight = settingsLayout.getMeasuredHeight();

        // Set initial values for animation
        openSettingsButton.setAlpha(0f);
        openSettingsButton.setVisibility(View.VISIBLE);

        // Create the animation
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                // Update settingsLayout appearance
                settingsLayout.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                settingsLayout.requestLayout();
                settingsLayout.setAlpha(1f - interpolatedTime);

                // Update openSettingsButton appearance
                openSettingsButton.setAlpha((float) Math.cbrt(interpolatedTime));

                // At the end:
                if (interpolatedTime == 1)
                    settingsLayout.setVisibility(View.GONE);
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime));

        // Start the animation!
        settingsLayout.startAnimation(animation);
    }

    @Override
    public void reload() {
        restoreValues();
        setFractionalPartText();
    }

    @Override
    public void showOnScreenKeyboard(boolean show) {
        // N/A
    }

    @Override
    public void refreshKeyboard() {
        // N/A
    }

    @Override
    public Class getPreviousFragment() {
        return null;
    }
}
