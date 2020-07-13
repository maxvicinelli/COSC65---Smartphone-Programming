package edu.dartmouth.cs.myruns1.services;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class DetectedActivityIntentService extends IntentService {

    protected static final String TAG = DetectedActivityIntentService.class.getSimpleName();


    public DetectedActivityIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        List<DetectedActivity> detectedActivities = result.getProbableActivities();

        for (DetectedActivity activity : detectedActivities) {
            broadcastActivity(activity);
        }
    }

    private void broadcastActivity(DetectedActivity activity) {
        Intent intent = new Intent(ActivityDetectionService.BROADCAST_DETECTED_ACTIVITY);
        intent.putExtra("type", activity.getType());
        intent.putExtra("confidence", activity.getConfidence());
        //only send broadcast if confidence greater than 70%
        if (activity.getConfidence() > 70) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}
