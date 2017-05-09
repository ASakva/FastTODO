package com.alexandersakva.fasttodo;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexandersakva.fasttodo.data.ToDosContract;

/**
 * Created by Tawiskaron on 14.04.2017.
 */

public class ToDoViewHolder extends RecyclerView.ViewHolder {

    private TextView textTitle;
    private TextView textDescription;
    private ImageView imageView;
    private ImageButton buttonImportance;
    public short color;
    private int importance = 0;

    public short getColor() {
        return color;
    }

    public ToDoViewHolder(View itemView) {
        super(itemView);


        textTitle = (TextView) itemView.findViewById(R.id.textview_title);
        imageView = (ImageView) itemView.findViewById(R.id.indicator);
        textDescription = (TextView) itemView.findViewById(R.id.textview_description);
        buttonImportance = (ImageButton) itemView.findViewById(R.id.image_button_importance);
        buttonImportance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importance = (importance -1) * -1;
                if (importance == 1) {
                    buttonImportance.setImageResource(R.drawable.fire_on);
                } else buttonImportance.setImageResource(R.drawable.fire_off);
            }
        });



    }

    public void setData(Cursor c) {

        ToDoCalendar calendar = new ToDoCalendar();
        int date = calendar.currentTime;
        int endDate = c.getInt(c.getColumnIndex(ToDosContract.ToDos.COLUMN_DATE_OF_END));
        int daysToEnd = endDate - date;
        if (daysToEnd<=1){
            imageView.setBackgroundResource(R.color.colorRed);
        } else if (daysToEnd<=3){
            imageView.setBackgroundResource(R.color.colorOrange);
        } else if (daysToEnd<=7){
            imageView.setBackgroundResource(R.color.colorYellow);
        } else
            imageView.setBackgroundResource(R.color.colorGreen);

        textTitle.setText(c.getString(c.getColumnIndex(ToDosContract.ToDos.COLUMN_NAME)));
        if (c.getString(c.getColumnIndex(ToDosContract.ToDos.COLUMN_COMMENT)).trim().isEmpty()){
            textDescription.setVisibility(View.GONE);
        } else {
            textDescription.setVisibility(View.VISIBLE);
            textDescription.setText(c.getString(c.getColumnIndex(ToDosContract.ToDos.COLUMN_COMMENT)));
        }

        if (c.getInt(c.getColumnIndex(ToDosContract.ToDos.COLUMN_URGENCY)) == 1) {
            buttonImportance.setImageResource(R.drawable.fire_on);
        } else buttonImportance.setImageResource(R.drawable.fire_off);








    }


    //public boolean getColor(int position){
    //    ToDoViewHolder toDoViewHolder =
    //}
}
