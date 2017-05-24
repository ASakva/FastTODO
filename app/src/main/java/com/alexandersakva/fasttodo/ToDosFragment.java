package com.alexandersakva.fasttodo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alexandersakva.fasttodo.data.CVDatabaseHelper;
import com.alexandersakva.fasttodo.data.CursorMaker;
import com.alexandersakva.fasttodo.data.ToDosContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class ToDosFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ToDoRecyclerViewAdapter mAdapter;
    private Context context;
    private SQLiteDatabase db;
    private Cursor cursor;
    private Cursor countCursor;
    private CVDatabaseHelper dbHelper;
    private String[] columns;
    private String selection;
    private String[] selectionArgs;
    private int importance;
    String overdueDate;
    private ToDoCalendar c;
    public int[] group= new int[4];


    private int edit_position;
    private View view;
    private boolean add = false;
    private Paint p = new Paint();


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
        final FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new ToDoItemDecorator(group));

        setUpItemTouchHelper();



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
    }

    @Override
    public void onResume() {
        super.onResume();

        initCursor();
        mAdapter.swapCursor(cursor);
    }

    public int countGroup(int hours) {
        columns = new String[]{ToDosContract.ToDos._ID,
                ToDosContract.ToDos.COLUMN_NAME,
                ToDosContract.ToDos.COLUMN_DATE_OF_END,
                ToDosContract.ToDos.COLUMN_DONE,
                ToDosContract.ToDos.COLUMN_URGENCY,
                ToDosContract.ToDos.COLUMN_COMMENT,
                ToDosContract.ToDos.COLUMN_DATE_OF_SET
        };

        c = new ToDoCalendar();
        selection = String.format("%s > ? AND %s < ? AND %s = ?",
                ToDosContract.ToDos.COLUMN_DATE_OF_END, ToDosContract.ToDos.COLUMN_DATE_OF_END, ToDosContract.ToDos.COLUMN_DONE);
        selectionArgs = new String[]{Integer.toString(c.getOverdueDate()), String.valueOf(c.getCurrentTime()+hours), "0"};
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

        try {
            dbHelper = CVDatabaseHelper.getInstance(context);
            db = dbHelper.getWritableDatabase();

            cursor = new CursorMaker(db).initMainCursor();

            group[1] = countGroup(25);
            group[2] = countGroup(73);
            group[3] = countGroup(169);

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








    private void setUpItemTouchHelper() {


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                int swipedPosition = viewHolder.getAdapterPosition();

                ToDoRecyclerViewAdapter adapter = (ToDoRecyclerViewAdapter) mRecyclerView.getAdapter();
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (viewHolder != null){
                    final View foregroundView = ((ToDoViewHolder) viewHolder).cardViewToDo;

                    getDefaultUIUtil().onSelected(foregroundView);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                final View foregroundView = ((ToDoViewHolder) viewHolder).cardViewToDo;

                drawBackground(viewHolder, dX, actionState);

                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                        actionState, isCurrentlyActive);
            }

            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                        RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                        int actionState, boolean isCurrentlyActive) {
                final View foregroundView = ((ToDoViewHolder) viewHolder).cardViewToDo;

                drawBackground(viewHolder, dX, actionState);

                getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                        actionState, isCurrentlyActive);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder){
                final View backgroundView = ((ToDoViewHolder) viewHolder).undoView;
                final View foregroundView = ((ToDoViewHolder) viewHolder).cardViewToDo;

                // TODO: should animate out instead. how?
                backgroundView.setRight(0);

                getDefaultUIUtil().clearView(foregroundView);
            }

            private  void drawBackground(RecyclerView.ViewHolder viewHolder, float dX, int actionState) {
                final View backgroundView = ((ToDoViewHolder) viewHolder).undoView;

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //noinspection NumericCastThatLosesPrecision
                    backgroundView.setRight((int) Math.max(dX, 0));
                }
            }



        };

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
}
