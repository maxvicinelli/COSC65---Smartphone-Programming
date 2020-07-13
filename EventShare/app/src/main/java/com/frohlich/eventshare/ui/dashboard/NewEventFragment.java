package com.frohlich.eventshare.ui.dashboard;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.frohlich.eventshare.Event;
import com.frohlich.eventshare.Group;
import com.frohlich.eventshare.MainActivity;
import com.frohlich.eventshare.R;
import com.frohlich.eventshare.User;
import com.frohlich.eventshare.adapters.GroupAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewEventFragment extends Fragment {
    private String TAG = "NewEventFragment";
    private EditText title;
    private EditText location;
    private EditText info;
    private TextView dateView;
    private TextView timeView;
    private Button saveButton;
    private DatePickerDialog mDate;
    private Calendar myCal;
    private TimePickerDialog mTimePicker;
    private ListView mListView;
    private final ArrayList<Group> groups = new ArrayList<Group>();
    private GroupAdapter mGroupAdapter;
    private Switch mSwitch;
    private Group selectedGroup = null;

    private FirebaseAuth mAuth;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        location = root.findViewById(R.id.location_editText);
        info = root.findViewById(R.id.info_editText);
        dateView = root.findViewById(R.id.date_view);
        timeView = root.findViewById(R.id.time_view);
        saveButton = root.findViewById(R.id.save_event_button);
        title = root.findViewById(R.id.title_edit_text);
        mListView = root.findViewById(R.id.listViewdash);
        mListView.setVisibility(View.INVISIBLE);

        TextView t = new TextView(getContext());
        t.setText("Your Groups:");
        mListView.addHeaderView(t);
        mAuth = FirebaseAuth.getInstance();
        mSwitch = root.findViewById(R.id.switchtoggle);
        mGroupAdapter = new GroupAdapter(getContext(), groups);


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
//                    TextView t = new TextView(getContext());
//                    t.setText("You have no groups :( ");
                    //mListView.addView(t);
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

        mGroupAdapter.notifyDataSetChanged();
        mListView.setAdapter(mGroupAdapter);

        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCal = Calendar.getInstance();
                mDate = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        myCal.set(year, month, day);
                        String myFormat = "MM/dd/yy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        dateView.setText(sdf.format(myCal.getTime()));
                    }
                }, myCal.get(Calendar.YEAR), myCal.get(Calendar.MONTH), myCal.get(Calendar.DAY_OF_MONTH));
                mDate.show();
            }
        });

        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String minString = String.valueOf(selectedMinute);
                        if (selectedMinute < 10) {
                            minString = "0" + minString;
                        }
                        String time = selectedHour + ":" + minString;
                        timeView.setText(time);
                    }
                }, 12, 0, false);//Yes 24 hour time
                mTimePicker.setTitle("Select A Time");
                mTimePicker.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText() != null && location.getText() != null && !dateView.getText().toString().equals("Date") && !timeView.getText().toString().equals("Time")) {
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference().child("events");
                    String newKey = ref.push().getKey();
                    ref = database.getReference().child("events").child(newKey);

                    //THIS MUST BE CHANGED BC CONSTRUCTOR IS CHANGED//////
                    /////////////////////////////////
                    ///////////////////////////////Check what happens if groupid is null
                    ArrayList<String> upvote = new ArrayList<String>();
                    upvote.add(mAuth.getUid());
                    String groupID = null;
                    boolean openToAll = true;
                    if(!mSwitch.isChecked() && selectedGroup != null){
                        Log.d(TAG, String.valueOf(selectedGroup.getGroupName()));
                        groupID = selectedGroup.getGroupID();
                        Log.d(TAG, "ID: " + selectedGroup.getGroupID());
                        openToAll = false;
                    }
                    ref.setValue(new Event(dateView.getText().toString(), String.valueOf(openToAll), groupID, info.getText().toString(), mAuth.getUid(), location.getText().toString(), timeView.getText().toString(), title.getText().toString(), mAuth.getCurrentUser().getDisplayName(), upvote, newKey));
                    //Send back to the home page
                    Intent i = new Intent(getContext(), MainActivity.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(getContext(),"Please fill in all required information", Toast.LENGTH_LONG).show();
                }
            }
        });

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                    mListView.setVisibility(View.VISIBLE);
                }
                else{
                    mListView.setVisibility(View.INVISIBLE);
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position,long arg3) {
                if (position != 0) {
                    view.setSelected(true);
                    selectedGroup = (Group) mListView.getItemAtPosition(position);
                    Log.d(TAG, "Selected Group: " + selectedGroup.getGroupName());
                }
            }
        });

    }
}
