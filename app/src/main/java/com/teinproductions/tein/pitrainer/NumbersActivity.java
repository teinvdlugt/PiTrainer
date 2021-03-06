package com.teinproductions.tein.pitrainer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class NumbersActivity extends AppCompatActivity {

    public static final int ADD_NUMBER_ACTIVITY_REQUEST_CODE = 1;
    public static final int NEW_NUMBER = -2;
    public static final String DELETE_DIGITS = "DELETE_DIGITS";
    public static final String DIGITS = "DIGITS";

    private RecyclerView recyclerView;
    private Digits[] customDigits;
    private int indexEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numbers);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setResult(RESULT_CANCELED);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        customDigits = Digits.savedDigits(this);

        setupViews();
    }

    private void setupViews() {
        if (customDigits.length == 0) {
            recyclerView.setVisibility(View.GONE);
            findViewById(R.id.noSavedNumbers_layout).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.noSavedNumbers_layout).setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(new NumberRecyclerAdapter(customDigits, this,
                    new NumberRecyclerAdapter.OnClickListener() {
                        @Override
                        public void onClickEdit(int which) {
                            indexEditing = which;
                            Intent intent = new Intent(NumbersActivity.this, AddNumberActivity.class);
                            intent.putExtra(AddNumberActivity.DIGITS, customDigits[which]);
                            startActivityForResult(intent, ADD_NUMBER_ACTIVITY_REQUEST_CODE);
                        }

                        @Override
                        public void onClickDelete(final int which, Context context) {
                            if (Digits.currentDigit.getName().equals(customDigits[which].getName()))
                                Digits.currentDigit = Digits.digits[0];

                            new AlertDialog.Builder(context)
                                    .setMessage(R.string.sure_delete_digits)
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int wh) {
                                            // Delete the number from the array and save the new array
                                            customDigits = deleteDigits(customDigits, which);
                                            saveCustomDigits();
                                            setResult(RESULT_OK);
                                            // Refresh the views
                                            setupViews();
                                        }
                                    }).setNegativeButton(R.string.no, null)
                                    .create().show();
                        }
                    }));
        }
    }

    public void onClickCreate(View view) {
        Intent intent = new Intent(this, AddNumberActivity.class);
        indexEditing = NEW_NUMBER;
        startActivityForResult(intent, ADD_NUMBER_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NUMBER_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            setResult(RESULT_OK);

            Digits edited = (Digits) data.getSerializableExtra(DIGITS);
            if (edited == null) return;

            if (data.getBooleanExtra(DELETE_DIGITS, false)) {
                // Delete or dismiss the number
                if (indexEditing == NEW_NUMBER) {
                    indexEditing = -1;
                } else {
                    String nameOfDeletedDigits = customDigits[indexEditing].getName();
                    customDigits = deleteDigits(customDigits, indexEditing);
                    saveCustomDigits();
                    if (Digits.currentDigit.getName().equals(nameOfDeletedDigits)) {
                        Digits.currentDigit = Digits.digits[0];
                    } else {
                        Digits.currentDigit = Digits.findDigits(Digits.currentDigit.getName());
                    }
                    indexEditing = -1;
                }
            } else if (indexEditing == NEW_NUMBER) {
                customDigits = addDigits(customDigits, edited);
                saveCustomDigits();
                Digits.currentDigit = Digits.findDigits(edited.getName());
            } else {
                try {
                    customDigits[indexEditing] = edited;
                    saveCustomDigits();
                    Digits.currentDigit = Digits.findDigits(edited.getName());
                } catch (IndexOutOfBoundsException e) {
                    Toast.makeText(this, "Sorry, something went wrong. Try again.", Toast.LENGTH_SHORT).show();
                }
            }

            // TODO notify data set changed
            setupViews();
        }
    }

    private void saveCustomDigits() {
        Digits.save(this, customDigits);
        Digits.initDigits(this);
    }

    @Override
    public void onPause() {
        saveCustomDigits();
        Digits.currentDigit = Digits.findDigits(Digits.currentDigit.getName());
        if (Digits.currentDigit == null) Digits.currentDigit = Digits.digits[0];
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_numbers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_add:
                onClickCreate(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class NumberRecyclerAdapter extends RecyclerView.Adapter<NumberRecyclerAdapter.NumberViewHolder> {
        private Digits[] data;
        private LayoutInflater layoutInflater;
        private Context context;
        private OnClickListener onClickListener;

        public NumberRecyclerAdapter(Digits[] data, Activity activity, OnClickListener listener) {
            super();
            this.data = data;
            this.context = activity;
            this.layoutInflater = LayoutInflater.from(context);
            this.onClickListener = listener;
        }

        @Override
        public NumberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.list_item_number, parent, false);
            return new NumberViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final NumberViewHolder holder, int position) {
            Digits number = data[position];
            holder.nameTV.setText(number.getName());
            holder.numberTV.setText(createDigitsString(number));

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClickEdit(holder.getAdapterPosition());
                }
            });
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClickDelete(holder.getAdapterPosition(), context);
                }
            });
        }

        private static String createDigitsString(Digits number) {
            String integerPart = number.getIntegerPart();
            String fractionalPart = number.getFractionalPart();
            if (integerPart == null) return "";

            String wholeNumber;
            if (fractionalPart == null || fractionalPart.isEmpty())
                wholeNumber = integerPart;
            else
                wholeNumber = integerPart + "." + fractionalPart;

            if (wholeNumber.length() > 15)
                return wholeNumber.substring(0, 15) + "…";
            else return wholeNumber;
        }

        @Override
        public int getItemCount() {
            return data.length;
        }

        static class NumberViewHolder extends RecyclerView.ViewHolder {
            TextView nameTV, numberTV;
            ImageView deleteButton;
            ViewGroup root;

            NumberViewHolder(View itemView) {
                super(itemView);
                nameTV = itemView.findViewById(R.id.name_textView);
                numberTV = itemView.findViewById(R.id.number_textView);
                root = itemView.findViewById(R.id.root);
                deleteButton = itemView.findViewById(R.id.delete_button);
            }
        }

        interface OnClickListener {
            void onClickEdit(int which);

            void onClickDelete(int which, Context context);
        }
    }


    public static Digits[] addDigits(Digits[] array, Digits toAdd) {
        Digits[] result = new Digits[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = toAdd;
        return result;
    }

    public static Digits[] deleteDigits(Digits[] array, int index) {
        Digits[] result = new Digits[array.length - 1];
        System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(array, index + 1, result, index, result.length - index);
        //for (int i = 0; i < newArray.length; i++)
        //    newArray[i] = customDigits[i + i >= which ? 1 : 0];
        return result;
    }
}
