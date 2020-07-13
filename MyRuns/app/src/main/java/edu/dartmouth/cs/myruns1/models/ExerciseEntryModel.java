package edu.dartmouth.cs.myruns1.models;

import edu.dartmouth.cs.myruns1.utils.ExerciseEntry;

public class ExerciseEntryModel {
    private ExerciseEntry exercise;


    public ExerciseEntryModel(ExerciseEntry exercise) {
        this.exercise = exercise;
    }

    public ExerciseEntry getExercise() {
        return exercise;
    }

    public void setExercise(ExerciseEntry exercise) {
        this.exercise = exercise;
    }
}
