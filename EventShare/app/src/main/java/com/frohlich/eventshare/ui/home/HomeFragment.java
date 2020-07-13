package com.frohlich.eventshare.ui.home;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.frohlich.eventshare.Event;
import com.frohlich.eventshare.Group;
import com.frohlich.eventshare.MainActivity;
import com.frohlich.eventshare.R;
import com.frohlich.eventshare.ViewEventActivity;
import com.frohlich.eventshare.adapters.EventAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getName();
    private HomeViewModel homeViewModel;
    private EventAdapter mAdapter;
    private EventAdapter mPrvateAdapter;
    private ArrayList<Event> mItems;
    private ArrayList<Event> mPrivateItems;
    private ListView mListView;
    private Button mHot, mprivate, mAll;
    private ProgressBar progressBar;
    private boolean isPublic = true;
    private boolean isHot = false;
    private boolean isPrivate = false;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "OnCreateView");
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mListView = root.findViewById(R.id.list_home);
        mHot = root.findViewById(R.id.hot_bttn);
        mprivate = root.findViewById(R.id.private_bttn);
        mAll = root.findViewById(R.id.all_events_bttn);
        progressBar = (ProgressBar) root.findViewById(R.id.progressbar);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "OnViewCreated");
        progressBar.setVisibility(View.VISIBLE);
        mItems = new ArrayList<Event>();
        mPrivateItems = new ArrayList<Event>();
        mAdapter = new EventAdapter(Objects.requireNonNull(getContext()), mItems);
        mPrvateAdapter = new EventAdapter(Objects.requireNonNull(getContext()), mPrivateItems);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        String currentDateString = new SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(new Date());
        final Date today = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");

        Log.d(TAG, "DATE: " + currentDateString);
        LiveData<DataSnapshot> liveData = homeViewModel.getDataSnapshotLiveData();
        liveData.observe(getViewLifecycleOwner(), new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    mItems.clear();
                    mPrivateItems.clear();
                    for (DataSnapshot shot: dataSnapshot.getChildren()){
                        Log.d(TAG, "LOG TIME BABY: " + shot.getValue().toString());
                        final Event event = shot.getValue(Event.class);
                        try {
                            if (sdf.format(today).equals(event.getDate()) || sdf.parse(event.getDate()).after(today)){
                                if (event.getOpenToAll().equals("true")){
                                    mItems.add(event);
                                    mAdapter.notifyDataSetChanged();
                                }
                                else{
                                    //IN HERE make a list of private events user is privy to
                                    String groupId = event.getGroupID();
                                    Log.d(TAG, "Group id: " + groupId);
                                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference ref = database.getReference().child("groups").child(groupId);
                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot != null){
                                                Group g = dataSnapshot.getValue(Group.class);
                                                Log.d(TAG, "Group: " + g.getGroupName());
                                                ArrayList<String> users = g.getGroupUsers();
                                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                                if (users.contains(mAuth.getUid())){
                                                    mPrivateItems.add(event);
                                                    mPrvateAdapter.notifyDataSetChanged();
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    if (!isPrivate){
                        if (isPublic){
                            sortByUpcoming();
                        }
                        else if (isHot){
                            sortByUpVote();
                        }
                        mAdapter = new EventAdapter(Objects.requireNonNull(getContext()), mItems);
                        mListView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                    else{
                        sortByUpcoming();
                        mPrvateAdapter = new EventAdapter(Objects.requireNonNull(getContext()), mPrivateItems);
                        mListView.setAdapter(mPrvateAdapter);
                        mPrvateAdapter.notifyDataSetChanged();
                    }
                    Log.d(TAG, "added data to adapter");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });



        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event item = null;
                if (mListView.getAdapter().equals(mAdapter)){
                    item = mAdapter.getItem(position);
                }
                else{
                   item = mPrvateAdapter.getItem(position);
                }
                //START AN INTENT TO THE EVENT VIEW HER

                String eventID = item.getId();

                Intent intent = new Intent(getActivity(), ViewEventActivity.class);
                intent.putExtra(MainActivity.EVENT_INFO_KEY, eventID);
                startActivity(intent);
            }
        });

        mHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHot = true;
                isPublic = false;
                isPrivate = false;
                Log.d(TAG, "HOT");
                sortByUpVote();
                mAdapter.replaceItems(mItems);
                mAdapter.notifyDataSetChanged();
                mListView.setAdapter(mAdapter);
            }
        });

        mAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPublic = true;
                isHot = false;
                isPrivate = false;
                Log.d(TAG, "Public Events");
                sortByUpcoming();
                mAdapter.replaceItems(mItems);
                mAdapter.notifyDataSetChanged();
                mListView.setAdapter(mAdapter);
            }
        });

        mprivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPublic = false;
                isHot = false;
                isPrivate = true;
                sortByUpcoming();
                if (mPrivateItems != null){
                    Log.d(TAG, "Private items size: " + mPrivateItems.size());
                    Log.d(TAG, "Private items " + mPrivateItems.toString());
                    Log.d(TAG, "Private Events");
                    mPrvateAdapter.notifyDataSetChanged();
                    mListView.setAdapter(mPrvateAdapter);
                }
                else{

                }
            }
        });

    }

    public void sortByUpcoming(){
        final SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yy HH:mm");
        Comparator<Event> comparator = new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                String o1DT = o1.getDate() + " " + o1.getTime();
                String o2DT = o2.getDate() + " " + o2.getTime();
                Log.d(TAG, "Dates: " + o1DT + " " + o2DT);

                try {
                    Date o1D = sdf2.parse(o1DT);
                    Date o2D = sdf2.parse(o2DT);
                    if (o2D.after(o1D)){
                        return -1;
                    }
                    else if (o2D.equals(o1D)){
                        return 0;
                    }
                    else{
                        return 1;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.d(TAG, "exception caught");
                }
                return 0;
            }
        };
        Collections.sort(mItems, comparator);
        if (mPrivateItems != null) {
            Collections.sort(mPrivateItems, comparator);
        }
    }

    public void sortByUpVote(){
        Comparator<Event> comparator = new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {

                if (o1.getUpVotes() == null && o2.getUpVotes() == null){
                    Log.d(TAG, "both null");
                    return 0;
                }
                else if (o1.getUpVotes() == null && o2.getUpVotes() != null){
                    Log.d(TAG, "Comparator compared");
                    return -1;
                }
                else if (o2.getUpVotes() == null && o1.getUpVotes() != null){
                    Log.d(TAG, "Comparator compared");
                    Log.d(TAG, o1.getTitle() + " " + o2.getTitle());
                    return 1;
                }
                else {
                    return o1.getUpVotes().size() - o2.getUpVotes().size();
                }
            }
        };
        Collections.sort(mItems, comparator);
        Collections.reverse(mItems);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "OnDestroyView");
    }
}
