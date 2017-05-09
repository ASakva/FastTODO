package com.alexandersakva.fasttodo;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexandersakva.fasttodo.data.CursorRecyclerAdapter;


/**
 * Created by Tawiskaron on 14.04.2017.
 */

public class ToDoRecyclerViewAdapter extends CursorRecyclerAdapter {

    Context mContext;


    public ToDoRecyclerViewAdapter(Context context, Cursor cursor) {
        super(cursor);
        mContext=context;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_to_dos, parent, false);
        return new ToDoViewHolder(v);
    }

    @Override
    public void onBindViewHolderCursor(RecyclerView.ViewHolder holder, Cursor cursor) {
        ToDoViewHolder toDoViewHolder = (ToDoViewHolder) holder;
        cursor.moveToPosition(cursor.getPosition());
        toDoViewHolder.setData(cursor);
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
