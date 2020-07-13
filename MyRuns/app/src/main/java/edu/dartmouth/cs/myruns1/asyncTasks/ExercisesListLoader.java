package edu.dartmouth.cs.myruns1.asyncTasks;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

import edu.dartmouth.cs.myruns1.ExerciseDataSource;
import edu.dartmouth.cs.myruns1.utils.ExerciseEntry;

public class ExercisesListLoader extends AsyncTaskLoader<List<ExerciseEntry>> {

    private ExerciseDataSource datasource;

    public ExercisesListLoader(@NonNull Context context) {
        super(context);

        datasource = new ExerciseDataSource(context);
        datasource.open();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public List<ExerciseEntry> loadInBackground() {
        return datasource.getAllExercises();
    }
}