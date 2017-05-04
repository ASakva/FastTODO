package com.alexandersakva.fasttodo.data;

import android.provider.BaseColumns;

/**
 * Created by Savior on 10.04.2017.
 */

public final class ToDosContract {

    public ToDosContract() {
    }

    public static final class ToDos implements BaseColumns {

        public static final String TABLE_NAME = ToDosContract.class.getSimpleName().toLowerCase();

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_URGENCY = "urgency";
        public static final String COLUMN_DATE_OF_SET = "date_of_set";
        public static final String COLUMN_DATE_OF_END = "date_of_end";
        public static final String COLUMN_COMMENT = "comment";
        public static final String COLUMN_DONE = "done";
    }

}
