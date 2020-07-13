package edu.dartmouth.cs.myruns1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;


import edu.dartmouth.cs.myruns1.adapters.ViewPagerAdapter;
import edu.dartmouth.cs.myruns1.fragments.HistoryFragment;
import edu.dartmouth.cs.myruns1.fragments.StartFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CS 65 : ";


    //private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;
    public static ViewPagerAdapter mViewPagerAdapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Main Activity");

        viewPager = findViewById(R.id.pager);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(mViewPagerAdapter);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPager.registerOnPageChangeCallback(createPageChangeCallback());
    }


     private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener  = new BottomNavigationView.OnNavigationItemSelectedListener() {
         @Override
         public boolean onNavigationItemSelected(@NonNull MenuItem item) {
             switch (item.getItemId()) {
                 case R.id.start:
                     //display start fragment
                     Log.d("START", "START");
                     viewPager.setCurrentItem(0);
                     return true;
                 case R.id.history:
                     //display history fragment
                     Log.d("HISTORY", "HISTORY");
                     viewPager.setCurrentItem(1);
                     return true;
             }
             return false;
         }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent_settings = new Intent(this, SettingsActivity.class);
                startActivity(intent_settings);
                return true;
            case R.id.edit_profile:
                Intent intent_edit_profile = new Intent(this, ProfileActivity.class);
                intent_edit_profile.putExtra("intent", 0);
                startActivity(intent_edit_profile);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private ViewPager2.OnPageChangeCallback createPageChangeCallback() {
        return new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.history).setChecked(false);
                        bottomNavigationView.getMenu().findItem(R.id.start).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.start).setChecked(false);
                        bottomNavigationView.getMenu().findItem(R.id.history).setChecked(true);
                        break;
                }
            }
        };
    }
}
