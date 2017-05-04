package com.alexandersakva.fasttodo;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import java.util.Calendar;
import java.util.Date;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alexandersakva.fasttodo.data.CVDatabaseHelper;
import com.alexandersakva.fasttodo.data.ToDosContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class ToDosFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private Context context;
    private SQLiteDatabase db;
    private Cursor cursor;
    private Cursor countCursor;
    private CVDatabaseHelper dbHelper;
    private String[] columns;
    private String selection;
    private String[] selectionArgs;
    private int date;
    String overdueDate;
    public int[] group= new int[4];

    public ToDosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //return inflater.inflate(R.layout.fragment_to_dos, container, false);
        View rootView = inflater.inflate(R.layout.fragment_to_dos, container, false);
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        context = getActivity();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);

        View view = getView();
        if (view != null) {
            mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_to_dos);
            mRecyclerView.setLayoutManager(mLayoutManager);

            columns = new String[]{ToDosContract.ToDos._ID, //TODO Вынести в метод
                    ToDosContract.ToDos.COLUMN_NAME,
                    ToDosContract.ToDos.COLUMN_DATE_OF_END,
                    ToDosContract.ToDos.COLUMN_DONE,
                    ToDosContract.ToDos.COLUMN_URGENCY,
                    ToDosContract.ToDos.COLUMN_COMMENT,
                    ToDosContract.ToDos.COLUMN_DATE_OF_SET
            };


            selection = String.format("%s > ? AND %s = ?",
                    ToDosContract.ToDos.COLUMN_DATE_OF_END, ToDosContract.ToDos.COLUMN_DONE);


            Calendar c = Calendar.getInstance();
            date = (int)(c.getTimeInMillis()/1000/60/60/24);
            overdueDate = String.valueOf(date - 3);

            selectionArgs = new String[]{overdueDate, "0"};

            String orderBy = ToDosContract.ToDos.COLUMN_DATE_OF_END + " ASC";

            try {
                dbHelper = new CVDatabaseHelper(context);
                db = dbHelper.getReadableDatabase();



                cursor = db.query(ToDosContract.ToDos.TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        orderBy);

                ToDoRecyclerViewAdapter mAdapter = new ToDoRecyclerViewAdapter(context, cursor);
                mRecyclerView.setAdapter(mAdapter);
                group[1] = countGroup(2);
                group[2] = countGroup(4);
                group[3] = countGroup(8);
            } catch (SQLiteException e) {
                Toast toast = Toast.makeText(context, "fhgfhf", Toast.LENGTH_SHORT);
                toast.show();
            }

        }
        mRecyclerView.addItemDecoration(new ToDoItemDecorator(group));
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cursor.close();
        db.close();
    }

    public int countGroup(int hours) {
        selection = String.format("%s > ? AND %s < ? AND %s = ?",
                ToDosContract.ToDos.COLUMN_DATE_OF_END, ToDosContract.ToDos.COLUMN_DATE_OF_END, ToDosContract.ToDos.COLUMN_DONE);

        selectionArgs = new String[]{overdueDate, String.valueOf(date+hours), "0"};
        countCursor = db.query(ToDosContract.ToDos.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);
        int count = countCursor.getCount();
        countCursor.close();
        return count;
    }


}
