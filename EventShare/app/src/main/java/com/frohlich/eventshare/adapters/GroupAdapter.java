package com.frohlich.eventshare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.frohlich.eventshare.Event;
import com.frohlich.eventshare.Group;
import com.frohlich.eventshare.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends ArrayAdapter<Group> {
    private static final String TAG = GroupAdapter.class.getName();
    private ArrayList<Group> items;
    private Context context;
    private FirebaseAuth mAuth;
    private TextView mTitle, mMembers;


    public GroupAdapter(@NonNull Context context, ArrayList<Group> items) {
        super(context, 0, items);
        this.items = items;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        //Check if it is the header
        view = LayoutInflater.from(context).inflate(R.layout.group_model, parent, false);
            final Group model = items.get(position);
            mTitle = view.findViewById(R.id.textView);
            mMembers = view.findViewById(R.id.textView2);
            mTitle.setText(model.getGroupName());
            mMembers.setText(model.getGroupUsers().size() + " members");
        return view;
    }
}
