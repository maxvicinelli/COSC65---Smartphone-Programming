package edu.dartmouth.cs.myruns1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.dartmouth.cs.myruns1.utils.ExerciseEntry;

public class ExerciseDataSource {


    private MySQLiteHelper dbHelper;
    private SQLiteDatabase database;

    private String[] allColumns = {
            MySQLiteHelper.KEY_ROWID,
            MySQLiteHelper.KEY_INPUT_TYPE,
            MySQLiteHelper.KEY_ACTIVITY_TYPE,
            MySQLiteHelper.KEY_DATE,
            MySQLiteHelper.KEY_TIME,
            MySQLiteHelper.KEY_DURATION,
            MySQLiteHelper.KEY_DISTANCE,
            MySQLiteHelper.KEY_CALORIES,
            MySQLiteHelper.KEY_HEARTRATE,
            MySQLiteHelper.KEY_COMMENT,
            MySQLiteHelper.KEY_CLIMB,
            MySQLiteHelper.KEY_AVG_SPEED,
            MySQLiteHelper.KEY_LOCATIONS
    };

    private static final String TAG = "debug";


    public ExerciseDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public List<ExerciseEntry> getAllExercises() {
        List<ExerciseEntry> exercises = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_EXERCISE, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ExerciseEntry exerciseEntry = cursorToExercise(cursor);
            exercises.add(exerciseEntry);
            cursor.moveToNext();
        }
        cursor.close();
        return exercises;
    }

    public ExerciseEntry createExerciseEntry(ExerciseEntry entry) {
        ContentValues value = new ContentValues();

        value.put(MySQLiteHelper.KEY_INPUT_TYPE, entry.getInputType());
        value.put(MySQLiteHelper.KEY_ACTIVITY_TYPE, entry.getActivityType());
        value.put(MySQLiteHelper.KEY_DATE, entry.getDate());
        value.put(MySQLiteHelper.KEY_TIME, entry.getTime());
        value.put(MySQLiteHelper.KEY_DURATION, entry.getDuration());
        value.put(MySQLiteHelper.KEY_DISTANCE, entry.getDistance());
        value.put(MySQLiteHelper.KEY_CALORIES, entry.getCalorie());
        value.put(MySQLiteHelper.KEY_HEARTRATE, entry.getHeartbeat());
        value.put(MySQLiteHelper.KEY_COMMENT, entry.getComment());
        value.put(MySQLiteHelper.KEY_CLIMB, entry.getClimb());
        value.put(MySQLiteHelper.KEY_AVG_SPEED, entry.getAvgSpeed());
        value.put(MySQLiteHelper.KEY_LOCATIONS, entry.getLocations());

        long insertID = database.insert(MySQLiteHelper.TABLE_EXERCISE, null, value);
        if (!database.isOpen()) {
            open();
        }
        Cursor cursor = database.query(MySQLiteHelper.TABLE_EXERCISE, allColumns, MySQLiteHelper.KEY_ROWID + " = " + insertID,
                null, null, null, null);
        cursor.moveToFirst();
        ExerciseEntry newExercise = cursorToExercise(cursor);
        cursor.close();
        return newExercise;
    }

    private ExerciseEntry cursorToExercise(Cursor cursor) {
        ExerciseEntry exercise = new ExerciseEntry();
        exercise.setID(cursor.getLong(0));
        exercise.setInputType(cursor.getString(1));
        exercise.setActivityType(cursor.getString(2));
        exercise.setDate(cursor.getString(3));
        exercise.setTime(cursor.getString(4));
        exercise.setDuration(cursor.getString(5));
        exercise.setDistance(cursor.getString(6));
        exercise.setCalorie(cursor.getString(7));
        exercise.setHeartbeat(cursor.getString(8));
        exercise.setComment(cursor.getString(9));
        exercise.setClimb(cursor.getString(10));
        exercise.setAvgSpeed(cursor.getString(11));
        exercise.setLocations(cursor.getString(12));
        return exercise;
    }

    public long deleteExercise(long exerciseID) {
        database.delete(MySQLiteHelper.TABLE_EXERCISE, MySQLiteHelper.KEY_ROWID + " = " + exerciseID, null);
        // I wanted to return nothing, but it was just easier with the asyncTask stuff to just return an ID and not use it anywhere
        return exerciseID;
    }

    public ExerciseEntry getExercise(long exerciseID) {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_EXERCISE, allColumns, MySQLiteHelper.KEY_ROWID + " = " + exerciseID,
                null, null, null, null);
        cursor.moveToFirst();
        return cursorToExercise(cursor);
    }

}
