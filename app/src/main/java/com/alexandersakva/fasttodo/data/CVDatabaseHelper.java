package com.alexandersakva.fasttodo.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

import static com.alexandersakva.fasttodo.data.ToDosContract.ToDos;



public class CVDatabaseHelper extends SQLiteOpenHelper {


    private static CVDatabaseHelper sInstance;

    private static final String DB_NAME = ToDos.TABLE_NAME;
    private static final int DB_VERSION = 1;

    public static synchronized CVDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CVDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }



    private CVDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
 //       DB_PATH = context.getApplicationInfo().dataDir +"/databases/";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_SKILLS_TABLE = "CREATE TABLE " + ToDos.TABLE_NAME + " ("
                + ToDos._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ToDos.COLUMN_NAME + " TEXT NOT NULL, "
                + ToDos.COLUMN_COMMENT + " TEXT, "
                + ToDos.COLUMN_DATE_OF_SET + " INTEGER NOT NULL, "
                + ToDos.COLUMN_DATE_OF_END + " INTEGER NOT NULL, "
                + ToDos.COLUMN_DONE + " INTEGER NOT NULL DEFAULT 0, "
                + ToDos.COLUMN_URGENCY + " INTEGER);";



        db.execSQL(SQL_CREATE_SKILLS_TABLE);

        Calendar c = Calendar.getInstance();

        int time;

        for (int i = 0; i < 50; i ++) {
            time = (int)(c.getTimeInMillis()/1000/60/60) + (i); //TODO заменить переменной из настроек

        insertTODO(db, "Очень важное дело № " + i, "", 547657, time, 0, 0);}

        //insertSkill(db, "SQLite", null, null);
        //insertSkill(db, "OOP", null, null);
        //insertSkill(db, "Git", null, null);

    }

    private static void insertTODO(SQLiteDatabase db, String name, String comment, int date_of_set, float date_of_end, int urgency, int done) {
        ContentValues skillValues = new ContentValues();
        skillValues.put(ToDos.COLUMN_NAME, name);
        skillValues.put(ToDos.COLUMN_COMMENT, comment);
        skillValues.put(ToDos.COLUMN_DATE_OF_SET, date_of_set);
        skillValues.put(ToDos.COLUMN_DATE_OF_END, date_of_end);
        skillValues.put(ToDos.COLUMN_DONE, done);
        skillValues.put(ToDos.COLUMN_URGENCY, urgency);
        db.insert(ToDos.TABLE_NAME, null, skillValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }
}
