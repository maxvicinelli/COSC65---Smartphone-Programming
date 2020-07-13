package com.frohlich.eventshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.frohlich.eventshare.utils.getProfileImage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class ViewEventActivity extends AppCompatActivity {

    private String eventID;
    private Event event;
    private static final String TAG = "VEA";
    private ImageView img;
    private TextView title, location, date, time, description, openToAll, theHost, upVotes;

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        setUpActionBar();

        if (getIntent().hasExtra(MainActivity.EVENT_INFO_KEY)) {
            Log.d(TAG, "got extra!");
            eventID = getIntent().getStringExtra(MainActivity.EVENT_INFO_KEY);
        }

        mAuth = FirebaseAuth.getInstance();

        setUpActionBar();

        getReferenceFromID();

        // TODO: Set image and host. Figure out how to get host info from firebase



        upVotes = findViewById(R.id.upvote_bttn_view_event);

        upVotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> upVotesList = event.getUpVotes();
                if (upVotesList.contains(mAuth.getUid())) {
                    upVotesList.remove(mAuth.getUid());
                } else {
                    upVotesList.add(mAuth.getUid());
                }
                upVotes.setText(String.valueOf(upVotesList.size()));
                DatabaseReference ref = database.getReference().child("events").child(event.getId());
                ref.child("upVotes").setValue(upVotesList);
            }
        });
    }

    private void getReferenceFromID() {

        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("events").child(eventID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                event = dataSnapshot.getValue(Event.class);
                loadViews();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadViews() {
        location = findViewById(R.id.location_view_event);
        title = findViewById(R.id.title_view_event);
        description = findViewById(R.id.event_info_view_event);
        date = findViewById(R.id.date_view_event);
        time = findViewById(R.id.time_view_event);
        openToAll = findViewById(R.id.access_view_event);
        theHost = findViewById(R.id.host_view_event);
        upVotes = findViewById(R.id.upvote_bttn_view_event);
        img = findViewById(R.id.image_view_event);


        location.setText(event.getLocation());

        if (event.getTitle().length() > 22) {
            title.setTextSize(16);
        }
        title.setText(event.getTitle());
        description.setText(event.getDescription());
        date.setText(event.getDate());
        time.setText(event.getTime());
        theHost.setText("Hosted by " + event.getHostName());
        if (event.getOpenToAll().equals("true")) {
            openToAll.setText("This is an open event");
        } else {
            openToAll.setText("This is a closed event");
        }

        try {
            getProfileImage.getProfileImg(img, "images/" + event.getHostID());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //getProfileImage p = new getProfileImage(img, event.getHostID());

        upVotes.setText(String.valueOf(event.getUpVotes().size()));

        setEventImage();
    }

    private void setEventImage() {
        DatabaseReference ref2 = database.getReference().child("users").child(event.getHostID());
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User eventHost = dataSnapshot.getValue(User.class);
                String uriString = eventHost.getUri();
                if (uriString != null) {
                    Uri uri = Uri.parse(uriString);
                    img.setImageURI(uri);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Back button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
