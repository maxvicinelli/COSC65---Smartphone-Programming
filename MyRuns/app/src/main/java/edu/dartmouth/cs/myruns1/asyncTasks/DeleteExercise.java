package edu.dartmouth.cs.myruns1.asyncTasks;

import android.os.AsyncTask;

import edu.dartmouth.cs.myruns1.ExerciseDataSource;
import edu.dartmouth.cs.myruns1.MainActivity;

public class DeleteExercise extends AsyncTask<Void, Void, Long> {

    private long id;
    private ExerciseDataSource eds;

    public DeleteExercise(long id, ExerciseDataSource eds) {
        this.id = id;
        this.eds = eds;
        eds.open();
    }

    @Override
    protected Long doInBackground(Void... voids) {
        return eds.deleteExercise(id);
    }

    @Override
    protected void onPostExecute(Long aLong) {
        // get the adapter from the HistoryFragment and clear it to update UI after deletion
        MainActivity.mViewPagerAdapter.getFrag().getAdapter().clear();
    }
}
