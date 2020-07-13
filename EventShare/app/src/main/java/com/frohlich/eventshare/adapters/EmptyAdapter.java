package com.frohlich.eventshare.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.frohlich.eventshare.R;

import java.util.ArrayList;

public class EmptyAdapter extends ArrayAdapter<String> {

    private static final String TAG = "empty adapter";
    private Context context;
    private String message;

    private TextView mTextView;

    public EmptyAdapter(@NonNull Context context, ArrayList<String> input) {
        super(context, 0, input);
        message = input.get(0);
        context = this.context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        parent.getContext();
        if (context == null) {
            Log.d(TAG, "context is null");
            context = parent.getContext();
        }
        view = LayoutInflater.from(context).inflate(R.layout.empty_model, parent, false);
        mTextView = view.findViewById(R.id.empty_model_message);
        mTextView.setText(message);
        return view;
    }
}
