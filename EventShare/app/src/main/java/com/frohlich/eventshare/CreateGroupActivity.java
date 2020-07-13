package com.frohlich.eventshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.frohlich.eventshare.adapters.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class CreateGroupActivity extends AppCompatActivity {

    private static final String TAG = "CGA";
    private ArrayList<User> userList;
    private ArrayList<User> tempList = new ArrayList<User>();;
    private ArrayList<User> addedUserList;
    private UserAdapter userAdapter;
    private UserAdapter addedUserAdapter;
    private ListView mListView;
    private EditText mEditText;
    private androidx.appcompat.widget.SearchView searchView;
    private ListView mAddedUsersListView;

    public String INTENT_FROM = "intent from";
    public String CGA = "CGA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        setUpActionBar();

        setTitle("Create New Group");

        userList = new ArrayList<User>();
        addedUserList = new ArrayList<User>();
        mListView = findViewById(R.id.user_list);
        mEditText = findViewById(R.id.group_name);
        mAddedUsersListView = findViewById(R.id.added_user_list);
        TextView textView = new TextView(this);
        textView.setText("Users Added: ");
        mAddedUsersListView.addHeaderView(textView);

        addedUserAdapter = new UserAdapter(this, addedUserList);
        mAddedUsersListView.setAdapter(addedUserAdapter);

        searchView = findViewById(R.id.search_view_id);

        searchView.setQueryHint("Add Users");

        mListView.setVisibility(View.INVISIBLE);

        getAllUsers();

        Log.d(TAG, "creating setOnQ text listener");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // called when user presses search button from keyboard
                // if search query is not empty then search
                if (!TextUtils.isEmpty(s.trim())) {
                    // search text contains text, search it
                    searchUsers(s);

                } else {
                    // search text empty, get all users
                    Log.d(TAG, "pressed icon and getting all users");
                    //getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // called whenever user presses any single letter
                if (!TextUtils.isEmpty(s.trim())) {
                    // search text contains text, search it
                    searchUsers(s);

                } else {
                    // search text empty, get all users
                    searchUsers("");
                }
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.setVisibility(View.VISIBLE);
                mAddedUsersListView.setVisibility(View.INVISIBLE);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mListView.setVisibility(View.INVISIBLE);
                mAddedUsersListView.setVisibility(View.VISIBLE);
                //ArrayList<User> addedUser = userAdapter.getAddedUsers();
                //mAddedUsersListView.setAdapter(new UserAdapter(Objects.requireNonNull(getApplicationContext()), addedUser));
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User u = userAdapter.getItem(position);
                if (!addedUserList.contains(u)){
                    addedUserList.add(u);
                    //addedUserAdapter.add(u);

                    userList.remove(u);
                    tempList.remove(u);
                    //userAdapter.remove(u);

                    addedUserAdapter.notifyDataSetChanged();
                    userAdapter.notifyDataSetChanged();

                    //searchView.setQuery("",false);
                }
            }
        });

        mAddedUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0){
                    User u = addedUserAdapter.getItem(position - 1);
                    addedUserList.remove(u);
                    userList.add(u);
                    //userAdapter.add(u);
                    //addedUserAdapter.remove(u);

                    addedUserAdapter.notifyDataSetChanged();
                    userAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //MenuItem item = menu.findItem(R.id.action_search);
        //searchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(item);
        return super.onCreateOptionsMenu(menu);
    }

    private void searchUsers(final String query) {
        Log.d(TAG, "searchUsers() called");
        tempList.clear();
        for (User user: userList){
                if (user.getName().toLowerCase().contains(query.toLowerCase())
                        || user.getEmail().toLowerCase().contains(query.toLowerCase())) {

                    //Check if the user is already added if so, don't show it
                    if (!addedUserList.contains(user)){
                        tempList.add(user);
                    }
                }
        }
        userAdapter = new UserAdapter(Objects.requireNonNull(getApplicationContext()), tempList);
        //userAdapter.notifyDataSetChanged();
        mListView.setAdapter(userAdapter);
        //userAdapter.notifyDataSetChanged();
    }

    private void getAllUsers() {
        Log.d(TAG, "getAllUsers() called");
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);

                    if (!user.getEmail().equals(fUser.getEmail())) {
                        Log.d(TAG, "added user: " + user.getName());
                        userList.add(user);
                        tempList.add(user);

                    }
                }
                userAdapter = new UserAdapter(Objects.requireNonNull(getApplicationContext()), userList);
                mListView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save: 
                saveGroup();
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void saveGroup() {
        if (mEditText.getText().toString().length() >= 1 && addedUserList != null && addedUserList.size() >= 1){
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference().child("groups");
            final String newKey = ref.push().getKey();
            assert newKey != null;
            Log.d(TAG, "new key:" + newKey);
            ref = database.getReference().child("groups").child(newKey);
            ArrayList<String> userIds = new ArrayList<String>();
            for (User u: addedUserList){
                userIds.add(u.getUid());
            }
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            userIds.add(mAuth.getUid());
            ref.setValue(new Group(mEditText.getText().toString(), mAuth.getCurrentUser().getDisplayName(), mAuth.getUid(), userIds, newKey));

            //Now add it to the user
            ref = database.getReference().child("users").child(mAuth.getUid()).child("groupList");
            final DatabaseReference finalRef = ref;
            ref.addListenerForSingleValueEvent( new ValueEventListener(){

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> groups = (ArrayList<String>) dataSnapshot.getValue();
                        if (groups != null){
                            Log.d(TAG, groups.toString());
                            groups.add(newKey);
                        }
                        else{
                            groups = new ArrayList<String>();
                            groups.add(newKey);
                        }
                        finalRef.setValue(groups);
                    }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            finish();

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(INTENT_FROM, CGA);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "Please properly fill in group info", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


}
