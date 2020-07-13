package edu.dartmouth.cs.myruns1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.dartmouth.cs.myruns1.adapters.ManualInputAdapter;
import edu.dartmouth.cs.myruns1.asyncTasks.DeleteExercise;
import edu.dartmouth.cs.myruns1.asyncTasks.ExercisesListLoader;
import edu.dartmouth.cs.myruns1.asyncTasks.InsertExercise;
import edu.dartmouth.cs.myruns1.asyncTasks.LoadExercise;
import edu.dartmouth.cs.myruns1.fragments.HistoryFragment;
import edu.dartmouth.cs.myruns1.models.ExerciseEntryModel;
import edu.dartmouth.cs.myruns1.models.ManualEntryModel;
import edu.dartmouth.cs.myruns1.utils.ExerciseEntry;

public class ManualEntryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<ExerciseEntry>> {

    private ListView mListView;
    private ManualInputAdapter mAdapter;
    private ArrayList<ManualEntryModel> mItems;

    private final Calendar cal = Calendar.getInstance();
    EditText textDateView;
    EditText textTimeView;

    private static final String TAG = "CS 65: ";
    private static final String ARRAY_KEY = "array_key";
    private static final int ALL_EXERCISES_LOADER_ID = 1;
    public static final String INTENT_FROM_HISTORY = "FROM_HISTORY";
    public static final String ID = "_id";

    private boolean deletingEntry = false;

    private ExerciseDataSource datasource;
    private List<ExerciseEntry> exercises;

    private long deletedID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);
        setTitle("Manual Entry Activity");
        setUpActionBar();

        mListView = findViewById(R.id.list_view);
        mItems = new ArrayList<>();
        mItems.clear();

        mAdapter = new ManualInputAdapter(this, mItems); 
        mAdapter.clear(); 
        mListView.setAdapter(mAdapter);

        if (getIntent().hasExtra(INTENT_FROM_HISTORY)) {
            deletedID = getIntent().getExtras().getLong(INTENT_FROM_HISTORY);
            deletingEntry = true;
            mListView.setEnabled(false);
        }

        datasource = new ExerciseDataSource(this);
        datasource.open();

        exercises = new ArrayList<>();

        final LoaderManager mLoader = LoaderManager.getInstance(this);
        mLoader.initLoader(ALL_EXERCISES_LOADER_ID, null, this).forceLoad();

        if (deletingEntry) {
            loadExercise();
        } else {
            initialFields();
        }

        textTimeView = new EditText(this);
        textDateView = new EditText(this);

        if (savedInstanceState != null) {
            String[] savedData = savedInstanceState.getStringArray(ARRAY_KEY);
            for (int i=0; i<savedData.length; i++) {
                mItems.get(i).setData(savedData[i]);
            }
        }




        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleSelection(position);
            }
        });
    }

    private void loadExercise() {
        new LoadExercise(mItems, deletedID, datasource, mAdapter).execute();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        String[] savedData = new String[8];

        //copy data from main array
        for (int i=0; i < savedData.length; i++) {
            savedData[i] = mItems.get(i).getData();
        }

        outState.putStringArray(ARRAY_KEY, savedData);
        super.onSaveInstanceState(outState);
    }

    private void initialFields()  {
        mItems.add(new ManualEntryModel("Activity", getIntent().getExtras().getString("activity_type")));
        mItems.add(new ManualEntryModel("Date", "today"));
        mItems.add(new ManualEntryModel("Time", "now"));
        mItems.add(new ManualEntryModel("Duration", "0 mins"));
        mItems.add(new ManualEntryModel("Distance", "0 kms"));
        mItems.add(new ManualEntryModel("Calories", "0 cals"));
        mItems.add(new ManualEntryModel("Heartbeat", "0 bpm"));
        mItems.add(new ManualEntryModel("Comment", ""));
    }


    private void handleSelection(int selection) {
        if (selection == 0) {
        }

        else if (selection == 1) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    cal.set(Calendar.YEAR, year);
                    cal.set(Calendar.MONTH, month);
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    mAdapter.notifyDataSetChanged();

                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
                    textDateView.setText(sdf.format(cal.getTime()));
                    editList(1, textDateView);
                }
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }

        else if (selection == 2) {
            TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String minuteString = "" + minute;
                    if (minute < 10) minuteString = "0" + minuteString;
                    textTimeView.setText(hourOfDay + ":" + minuteString);
                    mAdapter.notifyDataSetChanged();
                    editList(2, textTimeView);
                }
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
            timePicker.setTitle("Time");
            timePicker.show();
        }
        else if (selection == 3) {
            createDialog(3, "Duration");
        }

        else if (selection == 4) {
            createDialog(4, "Distance");
        }

        else if(selection == 5) {
            createDialog(5, "Calorie");
        }

        else if(selection == 6) {
            createDialog(6, "Heartbeat");
        }

        else if (selection == 7) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Comment");
            final EditText editText = new EditText(this);
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(editText);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editList(7, editText);
                }
            });
            builder.show();
        }
        mAdapter.notifyDataSetChanged();
    }

    private void editList(int position, EditText editText) {
        ManualEntryModel mem = mItems.get(position);
        String input = editText.getText().toString();
        if (position == 3) {
            // duration
            input = input + " mins";
        }
        if (position == 4) {
            //distance
            input += " kms";

        }
        if (position == 5) {
            //calories
            input += " cals";

        }
        if (position == 6) {
            // heartbeat
            input += " bpm";

        }
        mem.setData(input);
        mItems.set(position, mem);
    }

    private void createDialog(final int position, String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(type);
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(editText);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //save user input
                editList(position, editText);
            }

        });
        builder.show();
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (deletingEntry) {
            getMenuInflater().inflate(R.menu.menu_delete, menu);
            return super.onCreateOptionsMenu(menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_save, menu);
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Back button
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                ArrayList<String> inputs = loadInputList();
                new InsertExercise(datasource, inputs).execute();
                finish();
                return true;
            case R.id.action_delete:
                new DeleteExercise(deletedID, datasource).execute();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<String> loadInputList() {
        ArrayList<String> inputs = new ArrayList<>();
        inputs.add("Manual");
        for (ManualEntryModel model : mItems) {
            inputs.add(model.getData());
        }
        return inputs;
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    @NonNull
    @Override
    public Loader<List<ExerciseEntry>> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == ALL_EXERCISES_LOADER_ID) {
            Log.d(TAG, "starting loader");
            return new ExercisesListLoader(this);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<ExerciseEntry>> loader, List<ExerciseEntry> data) {
        if (loader.getId() == ALL_EXERCISES_LOADER_ID) {
            Log.d(TAG, "id correct");
            if (data.size() > 0) {
                exercises = data;
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<ExerciseEntry>> loader) {

    }
}
