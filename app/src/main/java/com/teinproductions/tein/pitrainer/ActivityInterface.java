package com.teinproductions.tein.pitrainer;

import android.widget.EditText;


public interface ActivityInterface {
    void preventSoftKeyboardFromShowingUp(EditText et, boolean show);
    void vibrate();
    void animateToolbarColor(boolean red);
    void swapFragment(Class fragmentClass);
    void logEventSelectContent(String itemId, String itemName, String contentType);
    void logEventComplete();
}
