package edu.dartmouth.cs.myruns1.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationResult;

public class TrackedUserIntentService extends IntentService {

    protected static final String TAG = TrackedUserIntentService.class.getSimpleName();


    public TrackedUserIntentService() {
        super(TAG);
        Log.d("tag", "called constructor");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        LocationResult locationResult = LocationResult.extractResult(intent);

        if (locationResult != null) {
            Location location = locationResult.getLastLocation();

            broadcastLocation(location);
        }
    }

    private void broadcastLocation(Location location) {
        Log.d("tag", "sending location");
        Intent intent = new Intent(TrackingService.BROADCAST_LOCATION);
        intent.putExtra(TrackingService.LOCATION_KEY, location);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
