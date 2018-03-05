package com.teinproductions.tein.pitrainer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class AnimationUtils {
    public static void animateExpand(Context context, final View viewToExpand, final View buttonToFadeOut) {
        // Prepare for animation:
        // Determine final values for animation
        viewToExpand.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = viewToExpand.getMeasuredHeight();

        // Set initial values for animation
        viewToExpand.setAlpha(0f);
        viewToExpand.setVisibility(View.VISIBLE);
        viewToExpand.getLayoutParams().height = 1; // On older versions of Android, the animation gets cancelled if we set height to 0.
        buttonToFadeOut.setAlpha(1f);
        buttonToFadeOut.setVisibility(View.VISIBLE);

        // Create the animation
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                // Update settingsLayout appearance
                viewToExpand.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                viewToExpand.requestLayout();
                viewToExpand.setAlpha(interpolatedTime);
                // Update openSettingsButton
                buttonToFadeOut.setAlpha(1 - (float) Math.cbrt(interpolatedTime));

                // At the end:
                if (interpolatedTime == 1)
                    buttonToFadeOut.setVisibility(View.GONE);
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(context.getResources().getInteger(android.R.integer.config_shortAnimTime));

        // Start the animation!
        viewToExpand.startAnimation(animation);
    }

    public static void animateCollapse(Context context, final View viewToCollapse, final View buttonToFadeIn) {
        // Prepare for animation:
        // Determine initial height
        viewToCollapse.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int initialHeight = viewToCollapse.getMeasuredHeight();

        // Set initial values for animation
        buttonToFadeIn.setAlpha(0f);
        buttonToFadeIn.setVisibility(View.VISIBLE);

        // Create the animation
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                // Update settingsLayout appearance
                viewToCollapse.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                viewToCollapse.requestLayout();
                viewToCollapse.setAlpha(1f - interpolatedTime);

                // Update openSettingsButton appearance
                buttonToFadeIn.setAlpha((float) Math.cbrt(interpolatedTime));

                // At the end:
                if (interpolatedTime == 1)
                    viewToCollapse.setVisibility(View.GONE);
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(context.getResources().getInteger(android.R.integer.config_shortAnimTime));

        // Start the animation!
        viewToCollapse.startAnimation(animation);
    }
}
