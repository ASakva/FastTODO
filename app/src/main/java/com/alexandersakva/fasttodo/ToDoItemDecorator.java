package com.alexandersakva.fasttodo;

import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ToDoItemDecorator extends RecyclerView.ItemDecoration {

    public ToDoItemDecorator(int[] groups) {
        this.groups = groups;
    }

    public  int groups[];
    private int textSize = R.attr.subtitleTextAppearance;
    private int groupSpacing = 30;
    private Paint paint = new Paint();
    private int position;
    {
        paint.setTextSize(textSize);
    }



 /*   @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            position = parent.getChildAdapterPosition(view);
            for(int e = 0; e < 4; e++){
                if (position == groups[e]) {
                    c.drawText("Group " + (e + 1), view.getLeft(),
                            view.getTop() - groupSpacing / 2 + textSize / 3, paint);

                }
            }
        }
    }
*/

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        position = parent.getChildAdapterPosition(view);

        for(int i = 1; i < 4; i++){
            if (position == groups[i]) {
                outRect.set(0, groupSpacing, 0, 0);
            }
        }
    }

}
