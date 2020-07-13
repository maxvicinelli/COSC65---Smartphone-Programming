package com.frohlich.eventshare.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.frohlich.eventshare.Event;
import com.frohlich.eventshare.R;
import com.frohlich.eventshare.SettingsActivity;
import com.frohlich.eventshare.User;
import com.frohlich.eventshare.utils.getProfileImage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<Event> {
    private static final String TAG = EventAdapter.class.getName();
    private ArrayList<Event> items;
    private Context context;
    private FirebaseAuth mAuth;
    private TextView hostView;
    private ImageView imageView;
    private FirebaseDatabase database;


    public EventAdapter(@NonNull Context context, ArrayList<Event> items) {
        super(context, 0, items);
        this.items = items;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        final Event model = items.get(position);
        view = LayoutInflater.from(context).inflate(R.layout.event_model, parent, false);
        imageView = view.findViewById(R.id.event_image);
        hostView = view.findViewById(R.id.host);
        TextView titleView = view.findViewById(R.id.title);
        TextView descriptionView = view.findViewById(R.id.description);
        TextView dateTimeView = view.findViewById(R.id.date_time);
        Button upVoteBttn = view.findViewById(R.id.upvote_model_bttn);
        TextView timeView = view.findViewById(R.id.time_event_ya);

        titleView.setText(model.getTitle());
        descriptionView.setText(model.getDescription());
        dateTimeView.setText(model.getDate());
        timeView.setText(model.getTime());
        hostView.setText(model.getHostName());
        if (model.getUpVotes() != null){
            upVoteBttn.setText(String.valueOf(model.getUpVotes().size()));
        }

        database = FirebaseDatabase.getInstance();

        //Get the profile image
        //getProfileImage p = new getProfileImage(imageView, "images/" + model.getHostID());
        try {
            getProfileImage.getProfileImg(imageView, "images/" + model.getHostID());
        } catch (IOException e) {
            e.printStackTrace();
        }

        upVoteBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference eventRef = database.getReference().child("events").child(model.getId());
                ArrayList<String> upVotes = model.getUpVotes();
                if (model.getUpVotes().contains(mAuth.getUid())){
                    upVotes.remove(mAuth.getUid());
                    Log.d(TAG, "ArrayList: " + upVotes);
                }
                else{
                    upVotes.add(mAuth.getUid());
                }
                eventRef.child("upVotes").setValue(upVotes);
            }
        });

        return view;
    }

    public void replaceItems(ArrayList<Event> items){
        this.items = items;

    }

}
