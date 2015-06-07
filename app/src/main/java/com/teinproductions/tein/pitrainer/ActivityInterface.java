package com.teinproductions.tein.pitrainer;

import android.widget.EditText;


public interface ActivityInterface {
    void preventSoftKeyboardFromShowingUp(EditText et, boolean show);

    void vibrate(int millis);

    void animateToolbarColor(boolean correct);
}
