package com.alexandersakva.fasttodo;

import android.database.Cursor;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexandersakva.fasttodo.data.ToDosContract;

/**
 * Created by Tawiskaron on 14.04.2017.
 */

public class ToDoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private TextView textTitle;
    private TextView textDescription;
    private ImageView imageView;
    private ImageButton buttonImportance;
    public short color;
    public ToDoViewHolderClicks mListener;
    public CardView cardViewToDo;
    Bundle bundle;
    private int importance;
    public ConstraintLayout undoView;
    public Button buttonUndo;

    public short getColor() {
        return color;
    }

    public ToDoViewHolder(View itemView, ToDoViewHolderClicks listener) {
        super(itemView);
        mListener=listener;
        textTitle = (TextView) itemView.findViewById(R.id.textview_title);
        imageView = (ImageView) itemView.findViewById(R.id.indicator);
        textDescription = (TextView) itemView.findViewById(R.id.textview_description);
        buttonImportance = (ImageButton) itemView.findViewById(R.id.image_button_importance);
        buttonImportance.setOnClickListener(this);
        cardViewToDo = (CardView)itemView.findViewById(R.id.todo_item_card);
        cardViewToDo.setOnClickListener(this);
        undoView = (ConstraintLayout)itemView.findViewById(R.id.undo_view);
        buttonUndo = (Button)itemView.findViewById(R.id.undo_button);
    }

    public void setData(Cursor c) {

        bundle = new Bundle();
        ToDoCalendar calendar = new ToDoCalendar();
        int date = calendar.currentTime;
        int endDate = c.getInt(c.getColumnIndex(ToDosContract.ToDos.COLUMN_DATE_OF_END));
        bundle.putInt("endDate", endDate);
        int timeToEnd = endDate - date;
        bundle.putInt("timeToEnd", timeToEnd);
        if (timeToEnd<=24){
            imageView.setBackgroundResource(R.color.colorRed);
            bundle.putInt("marker", 0);
        } else if (timeToEnd<=3*24){
            imageView.setBackgroundResource(R.color.colorOrange);
            bundle.putInt("marker", 1);
        } else if (timeToEnd<=7*24){
            imageView.setBackgroundResource(R.color.colorYellow);
            bundle.putInt("marker", 2);
        } else {
            imageView.setBackgroundResource(R.color.colorGreen);
            bundle.putInt("marker", 3);
        }


        String title = c.getString(c.getColumnIndex(ToDosContract.ToDos.COLUMN_NAME));
        textTitle.setText(title);
        bundle.putString("title", title);

        String description = c.getString(c.getColumnIndex(ToDosContract.ToDos.COLUMN_COMMENT)).trim();
        bundle.putString("description", description);
        if (description.isEmpty()){
            textDescription.setVisibility(View.GONE);
            textTitle.setMaxLines(2);
            textTitle.setSingleLine(false);
        } else {
            textDescription.setVisibility(View.VISIBLE);
            textDescription.setText(description);
            textTitle.setMaxLines(1);
        }

        importance = c.getInt(c.getColumnIndex(ToDosContract.ToDos.COLUMN_URGENCY));
        bundle.putInt("importance", importance);
        if ( importance == 1) {
            buttonImportance.setImageResource(R.drawable.fire_on);
        } else buttonImportance.setImageResource(R.drawable.fire_off);


    }

    @Override
    public void onClick(View v) {
        if (v instanceof ImageButton){
            mListener.onFire((ImageButton)v, getItemId(), importance);
        } else {
            mListener.onHolder(v, getItemId(), bundle);
        }

    }

    public static interface ToDoViewHolderClicks {
        public void onFire(ImageButton callerImage, long itemId, int importance);
        public void onHolder(View callerHolder, long itemId, Bundle bundle);
    }
}
