package com.alexandersakva.fasttodo;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alexandersakva.fasttodo.data.ToDosContract;

import java.util.Calendar;

/**
 * Created by Tawiskaron on 14.04.2017.
 */

public class ToDoViewHolder extends RecyclerView.ViewHolder {

    public TextView textView1;
    public short color;

    public short getColor() {
        return color;
    }

    public ToDoViewHolder(View itemView) {
        super(itemView);


        textView1 = (TextView) itemView.findViewById(R.id.card_textView_skill);


    }

    public void setData(Cursor c) {

        Calendar calendar = Calendar.getInstance();
        long date = calendar.getTimeInMillis();
        long endDate = c.getLong(c.getColumnIndex(ToDosContract.ToDos.COLUMN_DATE_OF_END));
        int daysToEnd = (int)(endDate - (date / 1000 / 60 / 60 / 24));
        if (daysToEnd<=1){
            itemView.setBackgroundResource(R.color.colorRed);
        } else if (daysToEnd<=3){
            itemView.setBackgroundResource(R.color.colorOrange);
        } else if (daysToEnd<=7){
            itemView.setBackgroundResource(R.color.colorYellow);
        } else
            itemView.setBackgroundResource(R.color.colorGreen);

        textView1.setText(c.getString(c.getColumnIndex(ToDosContract.ToDos.COLUMN_NAME)));

    }

    //public boolean getColor(int position){
    //    ToDoViewHolder toDoViewHolder =
    //}
}
