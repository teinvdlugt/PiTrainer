package com.teinproductions.tein.pitrainer;

public interface FragmentInterface {
    void notifyDigitsChanged();
    void showOnScreenKeyboard(boolean show);
    void refreshKeyboard();

    /** Returns the class of the Fragment which should be
     * switched to when back button is pressed, or null if there is none */
    Class getPreviousFragment();
}
