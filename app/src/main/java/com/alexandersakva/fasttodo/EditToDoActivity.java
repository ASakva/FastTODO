package com.alexandersakva.fasttodo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alexandersakva.fasttodo.data.CVDatabaseHelper;
import com.alexandersakva.fasttodo.data.ToDosContract;

public class EditToDoActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{

    EditText textTitle;
    EditText textDescription;
    ImageButton imageFire;
    TextView toEndText;
    Button buttonDone;
    Button buttonDelete;
    Button buttonSave;
    Toast toast;
    TextView seekBarText;
    ImageView imageMarker;


    @Override
    protected void onPause() {
        super.onPause();
        saveAction();

    }

    Intent intent;

    ToDoCalendar c;
    SQLiteDatabase db;
    long itemId;
    int time;
    ContentValues values;
    int importance;

    int delay = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_to_do);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        intent = getIntent();
        initViews();
    }


    public void initViews() {

        itemId = intent.getLongExtra("itemId", -1);

        textTitle = (EditText) findViewById(R.id.edit_text_title);
        textTitle.setText(intent.getStringExtra("title"));

        textDescription = (EditText) findViewById(R.id.edit_text_description);
        textDescription.setText(intent.getStringExtra("description"));

        imageFire = (ImageButton) findViewById(R.id.button_edit_fire);
        importance = intent.getIntExtra("importance", 0);
        if (importance == 1) {
            imageFire.setImageResource(R.drawable.fire_on);
        }
        imageFire.setOnClickListener(this);

        toEndText = (TextView)findViewById(R.id.textView_time_to_end);
        int timeToEnd = intent.getIntExtra("timeToEnd", 0); // общее время до конца






        int daysToEnd = timeToEnd/24;                       // дней
        int hoursToEnd = timeToEnd % 24;                    // часов по модулю
        String timeWord;                                    // переменная для правильной формы слова
        if (Math.abs(timeToEnd) < 18){                      // если значение меньше 18 часов - указываем время с точностью до часа
            timeToEnd = hoursToEnd;
            switch (getWordForm(hoursToEnd)){
                case 1: timeWord = getResources().getString(R.string.hour_form_1);
                    break;
                case 2: timeWord = getResources().getString(R.string.hour_form_2);
                    break;
                default: timeWord = getResources().getString(R.string.hour_form_3);
            }
        } else {
            if(Math.abs(daysToEnd) < 1) {
                if (hoursToEnd >= 18) {
                    daysToEnd++;
                }
                if (hoursToEnd <= -18) {
                    daysToEnd--;
                }
            }
            timeToEnd = daysToEnd;
            switch (getWordForm(daysToEnd)){
                case 1: timeWord = getResources().getString(R.string.day_form_1);
                    break;
                case 2: timeWord = getResources().getString(R.string.day_form_2);
                    break;
                default: timeWord = getResources().getString(R.string.day_form_3);
            }
        }
        if (timeToEnd<0){
            toEndText.setText(getResources().getString(R.string.overdue_for) + ": " + Math.abs(timeToEnd) + " " + timeWord);
        } else {
            toEndText.setText(getResources().getString(R.string.time_to_end) + ": " + timeToEnd + " " + timeWord);
        }


        int marker = intent.getIntExtra("marker", 3);
        imageMarker = (ImageView)findViewById(R.id.image_marker);

        switch (marker){
            case 0 : imageMarker.setImageResource(R.color.colorRed);
                break;
            case 1 : imageMarker.setImageResource(R.color.colorOrange);
                break;
            case 2 : imageMarker.setImageResource(R.color.colorYellow);
                break;
            case 3 : imageMarker.setImageResource(R.color.colorGreen);
                break;
        }


        buttonDone = (Button)findViewById(R.id.button_done);
        buttonDone.setOnClickListener(this);

        buttonDelete = (Button)findViewById(R.id.button_delete);
        buttonDelete.setOnClickListener(this);

        buttonSave = (Button)findViewById(R.id.button_save);
        buttonSave.setOnClickListener(this);

        seekBarText = (TextView)findViewById(R.id.textView6);
        final SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        //seekBar.setProgress(8);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_done :
                try {
                    CVDatabaseHelper dbHelper = CVDatabaseHelper.getInstance(EditToDoActivity.this);
                    db = dbHelper.getWritableDatabase();
                } catch (SQLiteDatabaseLockedException e) {
                    Toast.makeText(this, "Cant get db :(", Toast.LENGTH_SHORT).show();
                }
                c = new ToDoCalendar();
                time = c.getCurrentTime();
                values = new ContentValues();
                values.put(ToDosContract.ToDos.COLUMN_DONE, time);
                db.update(ToDosContract.ToDos.TABLE_NAME,
                        values,
                        ToDosContract.ToDos._ID + "= ?", new String[]{Long.toString(itemId)});

                onBackPressed();
                break;

            case R.id.button_delete :
                try {
                    CVDatabaseHelper dbHelper = CVDatabaseHelper.getInstance(EditToDoActivity.this);
                    db = dbHelper.getWritableDatabase();
                } catch (SQLiteDatabaseLockedException e) {
                    Toast.makeText(this, "Cant get db :(", Toast.LENGTH_SHORT).show();
                }
                db.delete(ToDosContract.ToDos.TABLE_NAME,
                        ToDosContract.ToDos._ID + "= ?",
                        new String[]{Long.toString(itemId)});
                onBackPressed();
                break;

            case R.id.button_edit_fire :
                importance = (importance - 1) * -1;
                if (importance == 0) {
                    imageFire.setImageResource(R.drawable.fire_off);
                } else imageFire.setImageResource(R.drawable.fire_on);
                break;

            case R.id.button_save :
                saveAction();
                onBackPressed();
                break;


        }
    }

    public void saveAction() {
        try {
            CVDatabaseHelper dbHelper = CVDatabaseHelper.getInstance(EditToDoActivity.this);
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteDatabaseLockedException e) {
            Toast.makeText(this, "Cant get db :(", Toast.LENGTH_SHORT).show();
        }
        c = new ToDoCalendar();
        time = c.getCurrentTime();
        values = new ContentValues();
        values.put(ToDosContract.ToDos.COLUMN_DATE_OF_END, intent.getIntExtra("endDate", 0) + delay);
        values.put(ToDosContract.ToDos.COLUMN_NAME, String.valueOf(textTitle.getText()));
        values.put(ToDosContract.ToDos.COLUMN_COMMENT, String.valueOf(textDescription.getText()));
        values.put(ToDosContract.ToDos.COLUMN_URGENCY, importance);
        db.update(ToDosContract.ToDos.TABLE_NAME,
                values,
                ToDosContract.ToDos._ID + "= ?", new String[]{Long.toString(itemId)});
    }

    public int getWordForm(int time) {
        if (time >10 && time <20) {
            return 3;
        }
        time = Math.abs(time) % 10;
        if(time == 1) {
            return 1;
        } else if (time <= 4 && time > 1){
            return 2;
        } else return 3;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        delay = 0;

        if (progress == 0){
            delay = 0;
            seekBarText.setText(getResources().getString(R.string.delay_task));

        } else if (progress > 0){
            switch (progress) {
                case 1  :
                    delay = 6;
                    break;
                case 2  :
                    delay = 12;
                    break;
                case 3  :
                    delay = 24;
                    break;
                case 4  :
                    delay = 48;
                    break;
                case 5  :
                    delay = 72;
                    break;
                case 6  :
                    delay = 120;
                    break;
                case 7  :
                    delay = 168;
                    break;
                case 8  :
                    delay = 240;
                    break;
                case 9  :
                    delay = 336;
                    break;
            }
            if (delay < 24){
                seekBarText.setText(getResources().getString(R.string.delay_for) + " " + delay + getResources().getString(R.string.hours_letter));
            } else seekBarText.setText(getResources().getString(R.string.delay_for) + " " + delay/24 + getResources().getString(R.string.days_letter));



        } /*else if (progress < 8){
            switch (progress){
                case 7 :
                    delay = ;
                    break;
                case 6 :
                    delay = ;
                    break;
                case 5 :
                    delay = ;
                    break;
                case 4 :
                    delay = ;
                    break;
                case 3 :
                    delay = ;
                    break;
                case 2 :
                    delay = ;
                    break;
                case 1 :
                    delay = ;
                    break;
                case 0 :
                    delay = ;
                    break;
            }
        }*/


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}


