package edu.dartmouth.cs.myruns1.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import edu.dartmouth.cs.myruns1.ManualEntryActivity;
import edu.dartmouth.cs.myruns1.MapActivity;
import edu.dartmouth.cs.myruns1.R;
import edu.dartmouth.cs.myruns1.adapters.ExerciseEntryAdapter;
import edu.dartmouth.cs.myruns1.asyncTasks.ExercisesListLoader;
import edu.dartmouth.cs.myruns1.models.ExerciseEntryModel;
import edu.dartmouth.cs.myruns1.utils.ExerciseEntry;
import edu.dartmouth.cs.myruns1.utils.Preference;


public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<ExerciseEntry>> {

    private ListView listView;
    private ExerciseEntryAdapter mAdapter;
    private ArrayList<ExerciseEntry> mItems;

    private static final String TAG = "debug";
    public static final String HISTORY = "from_history";
    public static final String ID_KEY = "key";

    private static final int ALL_EXERCISES_LOADER_ID = 1;

    private String unit_choice = "Metric";
    private edu.dartmouth.cs.myruns1.utils.Preference preference;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = getView().findViewById(R.id.entries);

        final LoaderManager mLoader = LoaderManager.getInstance(this);
        mLoader.initLoader(ALL_EXERCISES_LOADER_ID, null, this).forceLoad();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // change this so that it loads intent based on activity type
                ExerciseEntry exercise = mAdapter.getItem(position);
                if (exercise.getInputType().equals("Manual")) {
                    Intent intent = new Intent(getContext(), ManualEntryActivity.class);
                    intent.putExtra(ManualEntryActivity.INTENT_FROM_HISTORY, exercise.getID());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), MapActivity.class);
                    intent.putExtra(MapActivity.INTENT_FROM, HISTORY);
                    intent.putExtra(ID_KEY, exercise.getID());
                    startActivity(intent);
                }
            }
        });

    }

    public ExerciseEntryAdapter getAdapter() {
        //used when updating UI
        return mAdapter;
    }


    @NonNull
    @Override
    public Loader<List<ExerciseEntry>> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == ALL_EXERCISES_LOADER_ID) {
            return new ExercisesListLoader(getContext());
        }
        return null;
    }

    private void setUnits() {
        preference = new edu.dartmouth.cs.myruns1.utils.Preference(this.getContext());
        if (!preference.getUnits().equals("nan")) {
            unit_choice = preference.getUnits();
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<ExerciseEntry>> loader, List<ExerciseEntry> data) {
        // add exercises to adapter

        if (loader.getId() == ALL_EXERCISES_LOADER_ID) {
            if (data.size() > 0) {
                mItems = new ArrayList<ExerciseEntry>();
                mItems.addAll(data);
                mAdapter = new ExerciseEntryAdapter(this.getContext(), mItems);
                setUnits();
                String currUnits = mAdapter.getUnits();
                if (!currUnits.equals(unit_choice)) {
                    if (unit_choice.equals("Metric")) {
                        mAdapter.changeToKm();
                    } else {
                        mAdapter.changeToImperial();
                    }
                }
                listView.setAdapter(mAdapter);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final LoaderManager mLoader = LoaderManager.getInstance(this);
        mLoader.restartLoader(ALL_EXERCISES_LOADER_ID, null, this);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<ExerciseEntry>> loader) {

    }
}
