package edu.dartmouth.cs.myruns1.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.dartmouth.cs.myruns1.R;
import edu.dartmouth.cs.myruns1.models.ExerciseEntryModel;
import edu.dartmouth.cs.myruns1.utils.ExerciseEntry;

public class ExerciseEntryAdapter extends ArrayAdapter<ExerciseEntry> {

    private ArrayList<ExerciseEntry> exercises;
    private Context context;

    public ExerciseEntryAdapter(@NonNull Context context, ArrayList<ExerciseEntry> exercises) {
        super(context, 0, exercises);
        this.exercises = exercises;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ExerciseEntry exercise = exercises.get(position);

        view = LayoutInflater.from(context).inflate(R.layout.eem_listview, parent, false);
        TextView title = view.findViewById(R.id.title);
        TextView timeStamp = view.findViewById(R.id.timestamp);
        TextView distanceDuration = view.findViewById(R.id.distance_duration);

        title.setText(exercise.getInputType() + ": " + exercise.getActivityType());
        timeStamp.setText(exercise.getDate() + " " + exercise.getTime());
        distanceDuration.setText(exercise.getDistance() + ", " + exercise.getDuration());

        return view;
    }

    public void changeToImperial(){
        for (ExerciseEntry entity: exercises){
            String distance = entity.getDistance();
            Double number = Double.valueOf(distance.split(" ")[0])/1.60934;
            int intNum = (int) Math.round(number);
            entity.setDistance(intNum + " mis");
            notifyDataSetChanged();
        }
    }

    public void changeToKm(){
        for (ExerciseEntry entity: exercises){
            String distance = entity.getDistance();
            Double number = Double.valueOf(distance.split(" ")[0])*1.60934;
            int intNum = (int) Math.round(number);
            entity.setDistance(intNum + " kms");
            notifyDataSetChanged();
        }
    }

    public String getUnits() {
        // return type of Units based on what units the first exercise holds
        String distance = exercises.get(0).getDistance();
        String units = distance.split(" ")[1];
        Log.d("eea", "the current units are: " + units);
        if (units.equals("kms")) return "Metric";
        else return "Imperial";
    }



    @Override
    public int getCount() {
        return exercises.size();
    }
}
