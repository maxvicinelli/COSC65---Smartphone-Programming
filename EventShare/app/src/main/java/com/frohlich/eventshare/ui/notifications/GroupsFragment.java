package com.frohlich.eventshare.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.frohlich.eventshare.CreateGroupActivity;
import com.frohlich.eventshare.Group;
import com.frohlich.eventshare.R;
import com.frohlich.eventshare.ViewGroupActivity;
import com.frohlich.eventshare.adapters.EmptyAdapter;
import com.frohlich.eventshare.adapters.GroupAdapter;
import com.frohlich.eventshare.ui.home.HomeViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GroupsFragment extends Fragment {


    private static final String TAG = "GroupsFragment";
    private ListView mListView;
    private ProgressBar mProgressBar;
    private Button mAddGroup;
    private final ArrayList<Group> groups = new ArrayList<Group>();
    private GroupAdapter mGroupAdapter;

    private EmptyAdapter mEmptyAdapter;
    private ArrayList<String> message = new ArrayList<>();
    private boolean isEmpty = false;

    public static final String GROUP_KEY = "gk";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        message.add("You have no groups");
        mEmptyAdapter = new EmptyAdapter(getContext(), message);

        mGroupAdapter = new GroupAdapter(getContext(), groups);
        mListView = root.findViewById(R.id.list_groups);
        mAddGroup = root.findViewById(R.id.create_group);
        TextView t = new TextView(this.getContext());
        t.setText("Your Groups:");
        t.setTextSize(25);
        mListView.addHeaderView(t);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = database.getReference().child("users").child(mAuth.getUid()).child("groupList");
        ref.addListenerForSingleValueEvent( new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> groupids = (ArrayList<String>) dataSnapshot.getValue();
                if (groupids != null){
                    Log.d("GroupFragment group ids", groupids.toString());
                    for (String s: groupids){
                        Log.d("GroupFragment individual group id", s);
                        DatabaseReference ref2 = database.getReference().child("groups").child(s);
                        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Group g = dataSnapshot.getValue(Group.class);
                                groups.add(g);
                                Log.d("GroupFragment adding group: " , g.getGroupName());
                                mGroupAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    //Now add them to the adapter
                    Log.d("GroupFragment groups adding to adapter", groups.toString());
                }
                else{
                    mListView.setAdapter(mEmptyAdapter);
                    isEmpty = true;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
                startActivity(intent);
            }
        });
        mGroupAdapter.notifyDataSetChanged();
        mListView.setAdapter(mGroupAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isEmpty && position != 0){
                    Group group = groups.get(position-1);
                    String gID = group.getGroupID();
                    Intent intent = new Intent(getActivity(), ViewGroupActivity.class);
                    intent.putExtra(GROUP_KEY, gID);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "ON RESUME");
        if (mGroupAdapter != null){
            mGroupAdapter.notifyDataSetChanged();
        }
    }
}
