package edu.dartmouth.cs.myruns1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

import edu.dartmouth.cs.myruns1.asyncTasks.DeleteExercise;
import edu.dartmouth.cs.myruns1.asyncTasks.InsertExercise;
import edu.dartmouth.cs.myruns1.fragments.HistoryFragment;
import edu.dartmouth.cs.myruns1.fragments.StartFragment;
import edu.dartmouth.cs.myruns1.services.ActivityDetectionService;
import edu.dartmouth.cs.myruns1.services.TrackingService;
import edu.dartmouth.cs.myruns1.utils.ActivityDetectionDictionary;
import edu.dartmouth.cs.myruns1.utils.ExerciseEntry;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "map_activity";
    private GoogleMap mMap;
    private int PERMISSION_REQUEST_CODE = 1;
    private String input;
    private String activity;
    private Intent mTrackingServiceIntent;
    private Intent mActivityDetectionServiceIntent;
    private Marker mCurrentMarker;
    private ArrayList<Location> locations;
    public Marker mStartMarker;

    private LocalTime startTime;

    private TextView mTextView;

    private ExerciseDataSource datasource;
    private LatLng mLoadedFirstLtLg = new LatLng(5, 5);
    private LatLng mLoadedLastLtLg = new LatLng(5, 5);

    boolean mIsFirstLocation = true;
    public static final String INTENT_FROM = "intent_from";
    private long loadedID;
    private String intent_is_from = "nan";

    private ActivityDetectionDictionary dictionary; // holds number of times each activity was predicted by ActivityRecognition
    private int currHighConfidence = 0;

    private String currUnits = "Metric";

    private double[] mStats = new double[5];

    private edu.dartmouth.cs.myruns1.utils.Preference preference;

    private static final String LOCATIONS_KEY = "locations";
    private static final String INFO_KEY = "info";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Map");
        setContentView(R.layout.activity_map);

        startTime = LocalTime.now();

        Intent intent = getIntent();
        input = intent.getStringExtra(StartFragment.INPUT_TYPE);
        activity = intent.getStringExtra(StartFragment.ACTIVITY_TYPE);
        if (intent.hasExtra(INTENT_FROM)) {
            intent_is_from = intent.getStringExtra(INTENT_FROM);
            loadedID = intent.getExtras().getLong(HistoryFragment.ID_KEY);
        }

        mTextView = findViewById(R.id.textView);

        setUpActionBar();

        locations = new ArrayList<>();

        dictionary = new ActivityDetectionDictionary();

        datasource = new ExerciseDataSource(this);
        datasource.open();

//        if (savedInstanceState != null) {
////            String info_string = savedInstanceState.getString(INFO_KEY);
////            String locations_string = savedInstanceState.getString(LOCATIONS_KEY);
////
////            loadMapWithTracking(info_string, locations_string);
////        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void loadMapWithTracking(String info_string, String locations_string) {
        String[] statsStrings = info_string.split(" ");
        for (int i=0; i<mStats.length; i++) {
            mStats[i] = Double.parseDouble(statsStrings[i]);
        }
        addPreviousPolys(locations_string);
        String[] locations = locations_string.split(",");

        String firstLocaleString = locations[0];
        double latitude = Double.parseDouble(firstLocaleString.split(" ")[0]);
        double longitude = Double.parseDouble(firstLocaleString.split(" ")[1]);
        mStartMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Start"));

        String lastLocaleString = locations[locations.length-1];
        latitude = Double.parseDouble(lastLocaleString.split(" ")[0]);
        longitude = Double.parseDouble(lastLocaleString.split(" ")[1]);
        mCurrentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Current position!"));

        mIsFirstLocation = false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        Log.d(TAG, "saving instance state");
        outState.putString(LOCATIONS_KEY , addLocations());

        String stats = "";
        DecimalFormat df = new DecimalFormat("0.00");
        for (double mStat : mStats) {
            stats += df.format(mStat) + " ";
        }
        outState.putString(INFO_KEY, stats);
        Log.d(TAG, "put these stats: " + stats);

        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void updateTextView() {
        //Update TextView to hold correct, newly updated statistics

        String text = "";
        text += "Activity: " + activity + "\n";
        DecimalFormat df = new DecimalFormat("0.00");
        setUnits();
        if (currUnits.equals("Metric")) {
            // no conversion neccessary, just round and add units
            text += "Speed: " + df.format(mStats[0]) + " m/s" + "\n";
            text += "Avg Speed: " + df.format(mStats[1]) + " m/s" + "\n";
            text += "Climbed: " + df.format(mStats[2]) + " m" + "\n";
            text += "Calories: " + df.format(mStats[3]) + " cal" + "\n";
            text += "Distance: " + df.format(mStats[4]) + " m" + "\n";
        }
        else {
            // round, add units, and convert speed and distance to imperial
            text += "Speed: " + df.format(mStats[0]*2.23694) + " mph" + "\n";
            text += "Avg Speed: " + df.format(mStats[1]*2.23694) + " mph" + "\n";
            text += "Climbed: " + df.format(mStats[2]*3.28084) + " ft" + "\n";
            text += "Calories: " + df.format(mStats[3]) + " cal" + "\n";
            text += "Distance: " + df.format(mStats[4]*0.000621371) + " mi" + "\n";
        }
        mTextView.setText(text);
    }


    private void initializeStats() {
        Location location = locations.get(0);
        mStats[0] = location.getSpeed();
        mStats[1] = location.getSpeed();
        mStats[2] = 0.00;
        mStats[3] = 0.00;
        mStats[4] = 0.00;
    }

    private void updateStats() {
        int statsLength = locations.size();
        Location location = locations.get(statsLength-1);
        mStats[0] = location.getSpeed();
        double avgSpeed = mStats[1];
        mStats[1] = (location.getSpeed() + (avgSpeed*(statsLength-1)))/statsLength;
        double prevClimb = mStats[2];
        mStats[2] = prevClimb + (location.getAltitude() - locations.get(statsLength-2).getAltitude());
        double prevDistance = mStats[4];
        mStats[4] = prevDistance + location.distanceTo(locations.get(statsLength-2));
        mStats[3] = mStats[4]*.06;
    }

    private void addPreviousPolys(String stringLocations) {
        // called when you have a list of Locations you want to draw on the map
        // called when loading from HF and from OnSaveInstanceState

        String[] latLongs = stringLocations.split(",");
        for (int i=0; i<latLongs.length-1; i++) {
            double latitude = Double.parseDouble(latLongs[i].split(" ")[0]);
            double longitude = Double.parseDouble(latLongs[i].split(" ")[1]);

            LatLng latlng1 = new LatLng(latitude, longitude);
            if (i == 0) mLoadedFirstLtLg = latlng1;
            latitude = Double.parseDouble(latLongs[i+1].split(" ")[0]);
            longitude = Double.parseDouble(latLongs[i+1].split(" ")[1]);
            LatLng latlng2 = new LatLng(latitude, longitude);
            if (i+1 == latLongs.length-1) mLoadedLastLtLg = latlng2;
            mMap.addPolyline(new PolylineOptions().add(
                    latlng1, latlng2
            ).width(5).color(Color.BLACK));
        }
    }

    private void loadExercise() {
        //called when navigating from history fragment

        ExerciseEntry exercise = datasource.getExercise(loadedID);
        activity = exercise.getActivityType();
        mStats[0] = 0.00;
        mStats[1]= Double.parseDouble(exercise.getAvgSpeed());
        mStats[2] = Double.parseDouble(exercise.getClimb());
        mStats[3] = Double.parseDouble(exercise.getCalorie().split(" ")[0]);
        mStats[4] = Double.parseDouble(exercise.getDistance().split(" ")[0])*1000;
        updateTextView();

        addPreviousPolys(exercise.getLocations());

        mMap.addMarker(new MarkerOptions().position(mLoadedFirstLtLg).title("Start"));
        mMap.addMarker(new MarkerOptions().position(mLoadedLastLtLg).title("End"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLoadedFirstLtLg, 17));
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mTrackingServiceIntent = new Intent(this, TrackingService.class);
        mActivityDetectionServiceIntent = new Intent(this, ActivityDetectionService.class);


        if (!checkPermission())
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        else {
            if (intent_is_from.equals(HistoryFragment.HISTORY)) {
                loadExercise();
            } else {
                startTrackingService();
                LocalBroadcastManager.getInstance(this).registerReceiver(mLocationBroadcastReciever,
                        new IntentFilter(TrackingService.BROADCAST_LOCATION));

                if (input != null) {
                    if (input.equals("Automatic")) {
                        activity = "Unknown";
                        startActivityRecognitionService();
                        LocalBroadcastManager.getInstance(this).registerReceiver(mActivityBroadcastReciever,
                                new IntentFilter(ActivityDetectionService.BROADCAST_DETECTED_ACTIVITY));
                    }
                }
            }
        }
    }

    BroadcastReceiver mLocationBroadcastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TrackingService.BROADCAST_LOCATION)){
                Location location = intent.getParcelableExtra(TrackingService.LOCATION_KEY);
                locations.add(location);
                LatLng iAmHere = new LatLng(location.getLatitude(), location.getLongitude());

                if (mIsFirstLocation) {
                    // First location recieved
                    mIsFirstLocation = false;
                    mStartMarker = mMap.addMarker(new MarkerOptions().position(iAmHere).title("Start!"));
                    mCurrentMarker = mStartMarker;
                    initializeStats();

                } else {
                    if (!mCurrentMarker.equals(mStartMarker)) mCurrentMarker.remove();
                    mCurrentMarker = mMap.addMarker(new MarkerOptions().position(iAmHere).title("Current position!"));
                    drawLines();
                    //update mStats array
                    updateStats();
                }
                updateTextView();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(iAmHere, 17));
            }
        }
    };

    BroadcastReceiver mActivityBroadcastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ActivityDetectionService.BROADCAST_DETECTED_ACTIVITY)) {
                int type = intent.getIntExtra("type", -1);
                int confidence = intent.getIntExtra("confidence", 0);
                handleUserActivity(type, confidence);
            }
        }
    };

    private void handleUserActivity(int type, int confidence) {
        String label = "";
        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = "In Vehicle";
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                label = "On Bicycle";
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = "On Foot";
                break;
            }
            case DetectedActivity.RUNNING: {
                label = "Running";
                break;
            }
            case DetectedActivity.STILL: {
                label = "Still";
                break;
            }
            case DetectedActivity.TILTING: {
                label = "Tilting";
                break;
            }
            case DetectedActivity.WALKING: {
                label = "Walking";
                break;
            }
            case DetectedActivity.UNKNOWN: {
                label = "Unknown";
                break;
            }
        }

        // increase the amount of times the prediciton has been seen in the dictionary
        dictionary.incrementVal(label);

        if (confidence > currHighConfidence) {
            activity = label;
            currHighConfidence = confidence;
        }
    }

    @Override
    protected void onDestroy() {
        // stop both services when app stops
        stopService(mTrackingServiceIntent);
        stopService(mActivityDetectionServiceIntent);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTrackingService();
        } else {
            finish();
        }
    }

    private void startTrackingService() {
        startForegroundService(mTrackingServiceIntent);
    }

    private void startActivityRecognitionService() {
        startService(mActivityDetectionServiceIntent);
    }

    private void setUnits() {
        preference = new edu.dartmouth.cs.myruns1.utils.Preference(this);
        if (!preference.getUnits().equals("nan")) {
            currUnits = preference.getUnits();
        }
    }

    private void drawLines() {
        // Draw polyline between last two locations in array
        Location currLocation = locations.get(locations.size()-1);
        Location lastLocation = locations.get(locations.size()-2);
        mMap.addPolyline(new PolylineOptions().add(
                new LatLng(currLocation.getLatitude(), currLocation.getLongitude()),
                new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                .width(5)
                .color(Color.BLACK));
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;

    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (intent_is_from.equals(HistoryFragment.HISTORY)) {
            getMenuInflater().inflate(R.menu.menu_delete, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_save, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private ArrayList<String> loadInputList() {
        // Create an ArrayList of ExerciseEntry inputs before insertion
        // For stuff like comment, heartbeat, etc, just add a blank string

        DecimalFormat df = new DecimalFormat("0.00");
        ArrayList<String> inputs = new ArrayList<>();
        inputs.add(input);
        inputs.add(activity);
        //date
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        Date date = new Date();
        inputs.add(sdf.format(date));
        //time
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        Date date2 = new Date(System.currentTimeMillis());
        inputs.add(sdf2.format(date2));
        //duration
        Duration duration = Duration.between(startTime, LocalTime.now());
        long seconds = duration.getSeconds();
        inputs.add(seconds/60 + " mins");
        //distance
        DecimalFormat distanceDF = new DecimalFormat("0.00000");
        inputs.add(distanceDF.format(mStats[4]/1000) + " kms");
        //cals
        inputs.add(df.format(mStats[3]) + " cals");
        //heartbeat
        inputs.add("");
        //comment
        inputs.add("");
        //climb
        inputs.add(df.format(mStats[2]));
        //avg speed
        inputs.add(df.format(mStats[1]));

        // locations
        inputs.add(addLocations());
        return inputs;
    }

    private String addLocations() {
        // Return a string of latitudes and longitudes from locations array
        String stringLocations = "";
        for (Location l : locations) {
            stringLocations += l.getLatitude() + " " + l.getLongitude() + ",";
        }
        return stringLocations;
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            Log.d(TAG, "delete button pressed");
            new DeleteExercise(loadedID, datasource).execute();

        }
        if (item.getItemId() == R.id.action_save) {
            Log.d(TAG, "save button pressed");
            ArrayList<String> inputs = loadInputList();
            if (activity.equals("Automatic")) inputs.set(1, dictionary.getMax());
            new InsertExercise(datasource, inputs).execute();
        }
        finish();
        return super.onOptionsItemSelected(item);
    }
}
