package com.frohlich.eventshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.frohlich.eventshare.adapters.UserAdapter;
import com.frohlich.eventshare.ui.notifications.GroupsFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewGroupActivity extends AppCompatActivity {

    private static final String TAG = "blah";
    private String groupID;
    private FirebaseDatabase db;
    private Group group;
    private TextView groupTitle;
    private ListView groupMembersView;
    private UserAdapter mUserAdapter;
    private ArrayList<User> groupMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        setUpActionBar();

        Intent intent = getIntent();
        groupID = intent.getStringExtra(GroupsFragment.GROUP_KEY);

        groupMembers = new ArrayList<>();
        mUserAdapter = new UserAdapter(this, groupMembers);
        groupMembersView = findViewById(R.id.single_group_list);
        groupMembersView.setAdapter(mUserAdapter);

        getGroupFromID();
    }

    private void getGroupFromID() {
        db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference().child("groups").child(groupID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                group = dataSnapshot.getValue(Group.class);
                loadViews();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadViews() {
        groupTitle = findViewById(R.id.group_name_view);
        groupTitle.setText(group.getGroupName());

        ArrayList<String> groupUserIDs;
        groupUserIDs = group.getGroupUsers();
        for (String memberID : groupUserIDs) {
            DatabaseReference ref2 = db.getReference().child("users").child(memberID);
            ref2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User member = dataSnapshot.getValue(User.class);
                    Log.d(TAG, "added group member: " + member.getName());
                    groupMembers.add(member);
                    mUserAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        mUserAdapter.notifyDataSetChanged();
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // Back button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
