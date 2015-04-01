package com.teinproductions.tein.pitrainer;

import android.widget.EditText;


public interface ActivityInterface {
    public void preventSoftKeyboardFromShowingUp(EditText et, boolean show);

    public void vibrate(int millis);

    public void animateToolbarColor(boolean correct);
}
