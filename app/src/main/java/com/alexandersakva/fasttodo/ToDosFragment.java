package com.alexandersakva.fasttodo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
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

    public ToDoRecyclerViewAdapter getmAdapter() {
        return mAdapter;
    }

    private ToDoRecyclerViewAdapter mAdapter;
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
    private ToDoCalendar c;
    public int[] group= new int[4];

    public ToDosFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //return inflater.inflate(R.layout.fragment_to_dos, container, false);
        View rootView = inflater.inflate(R.layout.fragment_to_dos, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        View view = getView();
        if (view != null) {
            mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_to_dos);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new ToDoItemDecorator(group));
    }

    @Override
    public void onStart() {
        super.onStart();
        initCursor();
        mAdapter = new ToDoRecyclerViewAdapter(context, cursor);
        mRecyclerView.setAdapter(mAdapter);
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

        selectionArgs = new String[]{overdueDate, String.valueOf(c.getCurrentTime()+hours), "0"};
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

    public void initCursor() {
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

        try {
            dbHelper = CVDatabaseHelper.getInstance(context.getApplicationContext());
            db = dbHelper.getWritableDatabase();



            cursor = db.query(ToDosContract.ToDos.TABLE_NAME,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    orderBy);

            group[1] = countGroup(2);
            group[2] = countGroup(4);
            group[3] = countGroup(8);

            mRecyclerView.invalidateItemDecorations();
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(context, "fhgfhf", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onCreateToDo() {
        initCursor();
        mAdapter.swapCursor(cursor);



    }

}
