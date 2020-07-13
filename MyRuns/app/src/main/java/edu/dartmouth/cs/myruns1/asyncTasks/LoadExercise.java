package edu.dartmouth.cs.myruns1.asyncTasks;

import android.os.AsyncTask;

import java.util.ArrayList;

import edu.dartmouth.cs.myruns1.ExerciseDataSource;
import edu.dartmouth.cs.myruns1.adapters.ManualInputAdapter;
import edu.dartmouth.cs.myruns1.models.ManualEntryModel;
import edu.dartmouth.cs.myruns1.utils.ExerciseEntry;

public class LoadExercise extends AsyncTask<Void, Void, Void> {

    private ArrayList<ManualEntryModel> mItems;
    private long deletedID;
    private ExerciseDataSource datasource;
    private ManualInputAdapter mAdapter;


    public LoadExercise(ArrayList<ManualEntryModel> mItems, long deletedID, ExerciseDataSource datasource, ManualInputAdapter mAdapter) {
        this.mItems = mItems;
        this.deletedID = deletedID;
        this.datasource = datasource;
        this.mAdapter = mAdapter;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        ExerciseEntry exercise = datasource.getExercise(deletedID);
        mItems.add(new ManualEntryModel("Activity", exercise.getActivityType()));
        mItems.add(new ManualEntryModel("Date", exercise.getDate()));
        mItems.add(new ManualEntryModel("Time", exercise.getTime()));
        mItems.add(new ManualEntryModel("Duration", exercise.getDuration()));
        mItems.add(new ManualEntryModel("Distance", exercise.getDistance()));
        mItems.add(new ManualEntryModel("Calories", exercise.getCalorie()));
        mItems.add(new ManualEntryModel("Heartbeat", exercise.getHeartbeat()));
        mItems.add(new ManualEntryModel("Comment", exercise.getComment()));
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mAdapter.notifyDataSetChanged();
    }
}
