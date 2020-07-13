package edu.dartmouth.cs.myruns1.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import edu.dartmouth.cs.myruns1.MapActivity;
import edu.dartmouth.cs.myruns1.R;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class TrackingService extends Service {
    private static final String TAG = "service";
    private static final long UPDATE_INTERVAL = 3000;
    private static final long FAST_INTERVAL = 1000;
    private static final int SERVICE_NOTIFICATION_ID = 1;
    public static final String BROADCAST_LOCATION = "location update";
    public static final String LOCATION_KEY = "location_key";
    private NotificationManager notificationManager;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PendingIntent mPendingIntent;
    private LocationRequest mLocationRequest;



    public TrackingService() {
        Log.d(TAG, "TrackingService: Thread ID is: " + Thread.currentThread().getId());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "TrackingService: onCreate() Thread ID is: " + Thread.currentThread().getId());

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FAST_INTERVAL);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "TrackingService: onStartCommand() Thread ID is: " + Thread.currentThread().getId());
        createNotification();

        mFusedLocationProviderClient = new FusedLocationProviderClient(this);
        Intent mIntentService = new Intent(this, TrackedUserIntentService.class);

        mPendingIntent = PendingIntent.getService(this, 1, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT);
        requestLocationUpdatesHandler();
        return START_STICKY;
    }

    private void requestLocationUpdatesHandler() {
        if (mFusedLocationProviderClient != null) {
            Task<Void> task = mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mPendingIntent);

            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "requested location updates");
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "location updates failed to start");
                }
            });
        }
    }

    private void createNotification() {
        Intent notificationIntent = new Intent(this, MapActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // Create notification and its channel
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelID = "tracking";
        String channelName = "MyRuns";
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);

        Notification notification = new Notification.Builder(this, channelID)
                .setContentTitle("Exercise Tracker")
                .setContentText("Background service is tracking your exercise")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(SERVICE_NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "TrackingService: onStartCommand() Thread ID is: " + Thread.currentThread().getId());
        super.onDestroy();
        removeTrackingUpdatesHandler();
    }

    private void removeTrackingUpdatesHandler() {
        if (mFusedLocationProviderClient != null) {
            Task<Void> task = mFusedLocationProviderClient.removeLocationUpdates(mPendingIntent);
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "removed tracking updates!");
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "failed to remove tracking updates");
                }
            });
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
