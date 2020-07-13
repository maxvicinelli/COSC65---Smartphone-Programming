package edu.dartmouth.cs.myruns1.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.dartmouth.cs.myruns1.ExerciseDataSource;
import edu.dartmouth.cs.myruns1.models.ManualEntryModel;
import edu.dartmouth.cs.myruns1.utils.ExerciseEntry;

public class InsertExercise extends AsyncTask<Void, Void, ExerciseEntry> {

    ExerciseDataSource eds;
    ArrayList<String> inputs;
    List<ExerciseEntry> exercises;

    private static final String TAG = "insert";


    public InsertExercise(ExerciseDataSource eds, ArrayList<String> inputs) {
        this.eds = eds;
        eds.open();
        exercises = eds.getAllExercises();
        this.inputs = inputs;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ExerciseEntry doInBackground(Void... voids) {
        long id;
        ExerciseEntry exercise = new ExerciseEntry();
        // Assign a unique ID
        // If no exercises, ID = 0
        if (exercises.size() == 0) {
            id = 0L;
        }
        // if exercises, then get last ID and add 1
        else {
            id = exercises.get(exercises.size()-1).getID() + 1;
        }

        exercise.setID(id);
        exercise.setInputType(inputs.get(0));
        exercise.setActivityType(inputs.get(1));
        exercise.setDate(inputs.get(2));
        exercise.setTime(inputs.get(3));
        exercise.setDuration(inputs.get(4));
        exercise.setDistance(inputs.get(5));
        exercise.setCalorie(inputs.get(6));
        exercise.setHeartbeat(inputs.get(7));
        exercise.setComment(inputs.get(8));

        // only true if GPS or Automatic activity
        if (inputs.size() > 9) {
            exercise.setClimb(inputs.get(9));
            exercise.setAvgSpeed(inputs.get(10));
            exercise.setLocations(inputs.get(11));
        }

        Log.d(TAG, exercise.getActivityType());
        Log.d(TAG, exercise.getDate());


        // add it to db
        eds.createExerciseEntry(exercise);
        return exercise;
    }
}
