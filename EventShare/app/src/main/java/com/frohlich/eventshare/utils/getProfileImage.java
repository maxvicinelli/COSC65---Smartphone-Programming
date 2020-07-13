package com.frohlich.eventshare.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.frohlich.eventshare.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class getProfileImage {


    public static void getProfileImg(final ImageView view, final String location) throws IOException {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        // Create a reference with an initial file path and name
        StorageReference pathReference = storageRef.child(location);

//        final long ONE_MEGABYTE = 1024 * 1024;
//        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                // Data for "images/island.jpg" is returns, use this as needed
//                Bitmap bmp= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//                view.setImageBitmap(bmp);
//                Log.d("getProfileImage success", location + bmp.toString());
////                if (bmp == null){
////                    view.setImageResource(R.drawable.blankprofilepicture);
////                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//                Log.d("getProfileImage fail", location);
//                view.setImageResource(R.drawable.blankprofilepicture);
//            }
//        });

        final File localFile = File.createTempFile("image", "jpg");

        pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Bitmap bm = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                view.setImageBitmap(bm);
                Log.d("getProfileImage success", location + bm.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                view.setImageResource(R.drawable.blankprofilepicture);
            }
        });

    }

}
