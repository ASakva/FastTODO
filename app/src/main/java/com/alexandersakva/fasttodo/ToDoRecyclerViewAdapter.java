package com.alexandersakva.fasttodo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alexandersakva.fasttodo.data.CVDatabaseHelper;
import com.alexandersakva.fasttodo.data.CursorMaker;
import com.alexandersakva.fasttodo.data.CursorRecyclerAdapter;
import com.alexandersakva.fasttodo.data.ToDosContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Tawiskaron on 14.04.2017.
 */

public class ToDoRecyclerViewAdapter extends CursorRecyclerAdapter {

    Context mContext;
    SQLiteDatabase db;


    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    List<Long> itemsPendingRemoval;
    boolean undoOn = true; // is undo on, you can turn it on from the toolbar menu
    private Handler handler = new Handler(); // hanlder for running delayed runnables

    HashMap<Long, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be





    public ToDoRecyclerViewAdapter(Context context, Cursor cursor) {
        super(cursor);
        mContext=context;
        setHasStableIds(true);
        itemsPendingRemoval = new ArrayList<>();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_to_dos, parent, false);

        ToDoViewHolder vh = new ToDoViewHolder(v, new ToDoViewHolder.ToDoViewHolderClicks() {
            @Override
            public void onFire(ImageButton callerImage, long itemId, int importance) {

                importance = (importance - 1) * -1;

                if (importance == 0) {
                    callerImage.setImageResource(R.drawable.fire_off);
                } else {
                    callerImage.setImageResource(R.drawable.fire_on);
                }

                try {
                    CVDatabaseHelper dbHelper = CVDatabaseHelper.getInstance(mContext);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(ToDosContract.ToDos.COLUMN_URGENCY, importance);
                    db.update(ToDosContract.ToDos.TABLE_NAME,
                            values,
                            ToDosContract.ToDos._ID + "= ?", new String[]{Long.toString(itemId)});

                    swapCursor(new CursorMaker(db).initMainCursor());



                } catch (SQLiteDatabaseLockedException e) {
                    Toast.makeText(mContext, "Нет доступа к базе данных", Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onHolder(View callerHolder, long itemId, Bundle bundle) {
                Intent intent = new Intent(mContext, EditToDoActivity.class);
                intent.putExtras(bundle);
                intent.putExtra("itemId", itemId);
                mContext.startActivity(intent);
            }
        });
        return vh;
    }



    @Override
    public void onBindViewHolderCursor(final RecyclerView.ViewHolder holder, Cursor cursor) {
        ToDoViewHolder toDoViewHolder = (ToDoViewHolder) holder;
        cursor.moveToPosition(cursor.getPosition());
        toDoViewHolder.setData(cursor);
        final Long item = cursor.getLong(cursor.getColumnIndex(ToDosContract.ToDos._ID));

        if (itemsPendingRemoval.contains(item)) {

            // we need to show the "undo" state of the row

            holder.itemView.setBackgroundColor(Color.RED);

            ((ToDoViewHolder) holder).cardViewToDo.setVisibility(View.GONE);

            ((ToDoViewHolder) holder).undoView.setVisibility(View.VISIBLE);
            ((ToDoViewHolder) holder).buttonUndo.setVisibility(View.VISIBLE);


            ((ToDoViewHolder) holder).buttonUndo.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View v) {

                    // user wants to undo the removal, let's cancel the pending task

                    Runnable pendingRemovalRunnable = pendingRunnables.get(item);

                    pendingRunnables.remove(item);

                    if (pendingRemovalRunnable != null) handler.removeCallbacks(pendingRemovalRunnable);

                    itemsPendingRemoval.remove(item);

                    notifyItemChanged(holder.getAdapterPosition());

                    // this will rebind the row in "normal" state

                    //notifyItemChanged(items.indexOf(item));

                }

            });

        } else {

            // we need to show the "normal" state

            holder.itemView.setBackgroundColor(Color.WHITE);

            ((ToDoViewHolder) holder).undoView.setVisibility(View.GONE);

            ((ToDoViewHolder) holder).cardViewToDo.setVisibility(View.VISIBLE);

            ((ToDoViewHolder) holder).buttonUndo.setOnClickListener(null);

        }
    }


    public void setUndoOn(boolean undoOn) {

        this.undoOn = undoOn;

    }



    public boolean isUndoOn() {

        return undoOn;

    }



    public void pendingRemoval(final int position) {

        final Long item = getItemId(position);

        if (!itemsPendingRemoval.contains(item)) {

            itemsPendingRemoval.add(item);

            // this will redraw row in "undo" state


            notifyItemChanged(position);

            // let's create, store and post a runnable to remove the item

            Runnable pendingRemovalRunnable = new Runnable() {

                @Override

                public void run() {

                    remove(position);

                }

            };

            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);

            pendingRunnables.put(item, pendingRemovalRunnable);

        }

    }



    public void remove(int position) {

        Long item = getItemId(position);

        if (itemsPendingRemoval.contains(item)) {

            itemsPendingRemoval.remove(item);

        }

        try {
            CVDatabaseHelper dbHelper = CVDatabaseHelper.getInstance(mContext);
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteDatabaseLockedException e) {
            Toast.makeText(mContext, "Cant get db :(", Toast.LENGTH_SHORT).show();
        }
        db.delete(ToDosContract.ToDos.TABLE_NAME,
                ToDosContract.ToDos._ID + "= ?",
                new String[]{Long.toString(item)});

        swapCursor(new CursorMaker(db).initMainCursor());

    }



    public boolean isPendingRemoval(int position) {

        Long item = getItemId(position);

        return itemsPendingRemoval.contains(item);

    }




    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }



}

