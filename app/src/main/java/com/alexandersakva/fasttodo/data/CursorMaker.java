package com.alexandersakva.fasttodo.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alexandersakva.fasttodo.ToDoCalendar;

/**
 * Created by asakv on 10.05.2017.
 */

public class CursorMaker {

    private String[] columns;
    private String selection;
    private ToDoCalendar c;
    private String overdueDate;
    private String[] selectionArgs;
    private Cursor cursor;
    private SQLiteDatabase db;



    public CursorMaker(SQLiteDatabase db) {
        this.db = db;

    }


    public Cursor initMainCursor() {
            columns = new String[]{ToDosContract.ToDos._ID,
                    ToDosContract.ToDos.COLUMN_NAME,
                    ToDosContract.ToDos.COLUMN_DATE_OF_END,
                    ToDosContract.ToDos.COLUMN_DONE,
                    ToDosContract.ToDos.COLUMN_URGENCY,
                    ToDosContract.ToDos.COLUMN_COMMENT,
                    ToDosContract.ToDos.COLUMN_DATE_OF_SET
            };


            selection = String.format("%s > ? AND %s = ?",
                    ToDosContract.ToDos.COLUMN_DATE_OF_END, ToDosContract.ToDos.COLUMN_DONE);


            c = new ToDoCalendar();
            overdueDate = String.valueOf(c.getOverdueDate());

            selectionArgs = new String[]{overdueDate, "0"};

            String orderBy = ToDosContract.ToDos.COLUMN_DATE_OF_END + " ASC";

                cursor = db.query(ToDosContract.ToDos.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        orderBy);
        return cursor;
    }

}
