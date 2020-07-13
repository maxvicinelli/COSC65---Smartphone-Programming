package com.frohlich.eventshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    public static final String EVENT_INFO_KEY = "eventInfoKey";
    public static ActionBar bar;

    public static final String INTENT_FROM = "intent from";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        if (intent.hasExtra(INTENT_FROM)) {
            if (intent.getStringExtra(INTENT_FROM).equals("CGA")) {
                navView.setSelectedItemId(R.id.navigation_notifications);
            }
        }
        setupActionBar();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the actionbar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menuItem_settings).setVisible(true);
        return true;
    }

    //Two options in the menu bar: settings and edit profile. Link these via the proper intents to new activities
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menuItem_settings:
                Intent intentt = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentt);
                return true;
        }
        return false;
    }

    private void setupActionBar() {
        bar = getSupportActionBar();
        if (bar != null){
            bar.setTitle("Events");
        }
    }
}
