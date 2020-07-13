package edu.dartmouth.cs.myruns1.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.dartmouth.cs.myruns1.ManualEntryActivity;
import edu.dartmouth.cs.myruns1.MapActivity;
import edu.dartmouth.cs.myruns1.R;


public class StartFragment extends Fragment {

    private Spinner activity_spinner;
    private Spinner input_spinner;

    private String input;
    private String activity;

    private FloatingActionButton fab;

    private static final String TAG = "CS 65: ";
    public static final String INPUT_TYPE = "INPUT_TYPE";
    public static final String ACTIVITY_TYPE = "activity_type";


    public StartFragment() {
        // Required empty public constructor
    }

    public static StartFragment newInstance() {
        return new StartFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity_spinner = getView().findViewById(R.id.activity_type);
        ArrayAdapter<CharSequence> activity_adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.activity_type_items, android.R.layout.simple_spinner_item);
        activity_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activity_spinner.setAdapter(activity_adapter);
        activity_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activity = activity_spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });

        input_spinner = getView().findViewById(R.id.input_type);
        ArrayAdapter<CharSequence> input_adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.input_type_items, android.R.layout.simple_spinner_item);
        input_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        input_spinner.setAdapter(input_adapter);
        input_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                input = input_spinner.getSelectedItem().toString();
                if (input.equals("Automatic")) {
                    activity_spinner.setEnabled(false);
                } else {
                    if (!activity_spinner.isEnabled()) {
                        activity_spinner.setEnabled(true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });

        fab = getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (input.equals("Manual")) {
                    intent = new Intent(getActivity(), ManualEntryActivity.class);
                }
                else {
                    intent = new Intent(getActivity(), MapActivity.class);
                }
                intent.putExtra(ACTIVITY_TYPE, activity);
                intent.putExtra(INPUT_TYPE, input);
                startActivity(intent);
            }
        });

    }


}
