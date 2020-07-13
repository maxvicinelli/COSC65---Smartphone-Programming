package edu.dartmouth.cs.myruns1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "exercise.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_EXERCISE = "exercise_table";

    public static final String KEY_ROWID = "_id";
    public static final String KEY_INPUT_TYPE = "input_type";
    public static final String KEY_ACTIVITY_TYPE = "activity_type";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIME = "time";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_CALORIES = "calories";
    public static final String KEY_HEARTRATE = "heartrate";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_CLIMB = "climb";
    public static final String KEY_AVG_SPEED = "avg_speed";
    public static final String KEY_LOCATIONS = "locations";

    private static final String DATABASE_SCHEMA = "CREATE TABLE IF NOT EXISTS "
            + TABLE_EXERCISE
            + " ("
            + KEY_ROWID
            + " integer primary key, "
            + KEY_INPUT_TYPE
            + " TEXT, "
            + KEY_ACTIVITY_TYPE
            + " TEXT, "
            + KEY_DATE
            + " TEXT, "
            + KEY_TIME
            + " TEXT, "
            + KEY_DURATION
            + " TEXT, "
            + KEY_DISTANCE
            + " TEXT, "
            + KEY_CALORIES
            + " TEXT, "
            + KEY_HEARTRATE
            + " TEXT, "
            + KEY_COMMENT
            + " TEXT, "
            + KEY_CLIMB
            + " TEXT, "
            + KEY_AVG_SPEED
            + " TEXT, "
            + KEY_LOCATIONS
            + " TEXT"
            + ")";


    public MySQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_SCHEMA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
        onCreate(db);
    }
}
