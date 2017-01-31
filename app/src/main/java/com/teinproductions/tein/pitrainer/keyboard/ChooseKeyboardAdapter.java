package com.teinproductions.tein.pitrainer.keyboard;

import android.content.Context;
import android.support.v4.widget.Space;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.teinproductions.tein.pitrainer.R;

class ChooseKeyboardAdapter extends RecyclerView.Adapter<ChooseKeyboardAdapter.ViewHolder> {

    private Context context;
    private int selectedKeyboard = 0;
    private OnItemClickListener clickListener;

    interface OnItemClickListener {
        void onClickKeyboard(int index);
    }

    ChooseKeyboardAdapter(Context context, int selectedKeyboard, OnItemClickListener clickListener) {
        this.context = context;
        this.selectedKeyboard = selectedKeyboard;
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View keyboard = LayoutInflater.from(context).inflate(R.layout.list_item_keyboards, parent, false);
        return new ViewHolder(keyboard);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position, position == selectedKeyboard);
    }

    @Override
    public int getItemCount() {
        return ChooseKeyboardActivity.LAYOUTS.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private Button button1, button2, button3, button4,
                button5, button6, button7, button8, button9;
        private LinearLayout lastRow;
        private ImageButton backspace;
        private Space space;
        private Button button0;

        // To prevent useless work:
        private int lastKeyboardIndex = -1;

        ViewHolder(final View itemView) {
            super(itemView);
            // Make some adjustments to the keyboard layout, to be displayed in a list
            int _12dp = (int) context.getResources().getDisplayMetrics().density * 12;
            itemView.setPadding(0, _12dp, 0, _12dp);

            // Initialize views
            button1 = (Button) itemView.findViewById(R.id.button1);
            button2 = (Button) itemView.findViewById(R.id.button2);
            button3 = (Button) itemView.findViewById(R.id.button3);
            button4 = (Button) itemView.findViewById(R.id.button4);
            button5 = (Button) itemView.findViewById(R.id.button5);
            button6 = (Button) itemView.findViewById(R.id.button6);
            button7 = (Button) itemView.findViewById(R.id.button7);
            button8 = (Button) itemView.findViewById(R.id.button8);
            button9 = (Button) itemView.findViewById(R.id.button9);
            lastRow = (LinearLayout) itemView.findViewById(R.id.last_keyboard_row);
            button0 = (Button) itemView.findViewById(R.id.button0);
            backspace = (ImageButton) itemView.findViewById(R.id.buttonBackspace);
            space = (Space) itemView.findViewById(R.id.keyboard_space);

            // Disable all keys
            for (View key : new View[]{button1, button2, button3, button4, button5,
                    button6, button7, button8, button9, button0, backspace}) {
                key.setClickable(false);
            }

            // Set onClickListener
            itemView.findViewById(R.id.keyboard_root).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int previousSelectedKeyboard = selectedKeyboard;
                    selectedKeyboard = lastKeyboardIndex;
                    notifyItemChanged(lastKeyboardIndex);
                    notifyItemChanged(previousSelectedKeyboard);
                    if (clickListener != null) clickListener.onClickKeyboard(lastKeyboardIndex);
                }
            });
        }

        void bind(int keyboardIndex, boolean selected) {
            // If this is the selected keyboard layout, give it a colored background
            if (selected)
                itemView.findViewById(R.id.keyboard_root).setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            else
                itemView.findViewById(R.id.keyboard_root).setBackgroundResource(0);

            // To prevent useless work:
            if (lastKeyboardIndex == keyboardIndex) return;
            else lastKeyboardIndex = keyboardIndex;

            int[] keyboardLayout = ChooseKeyboardActivity.LAYOUTS[keyboardIndex];
            button1.setText(String.valueOf(keyboardLayout[0]));
            button2.setText(String.valueOf(keyboardLayout[1]));
            button3.setText(String.valueOf(keyboardLayout[2]));
            button4.setText(String.valueOf(keyboardLayout[3]));
            button5.setText(String.valueOf(keyboardLayout[4]));
            button6.setText(String.valueOf(keyboardLayout[5]));
            button7.setText(String.valueOf(keyboardLayout[6]));
            button8.setText(String.valueOf(keyboardLayout[7]));
            button9.setText(String.valueOf(keyboardLayout[8]));
            // Reorder the last LinearLayout according to the order in keyboardLayout
            lastRow.removeAllViews();
            for (int i = 9; i < 12; i++) {
                switch (keyboardLayout[i]) {
                    case 0:
                        lastRow.addView(button0);
                        break;
                    case -1:
                        lastRow.addView(backspace);
                        break;
                    case -2:
                        lastRow.addView(space);
                }
            }
        }
    }
}
