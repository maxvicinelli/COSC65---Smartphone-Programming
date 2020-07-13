package com.frohlich.eventshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
//import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PHOTO_PERMISSION = 1 ;
    private static final int REQUEST_CODE_TAKE_FROM_CAMERA = 2;
    private static final int LOAD_IMG = 3;
    private static final String TAG = SettingsActivity.class.getName(); // tag for logger

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private Button lougoutButton;
    private ImageView mImageview;
    private EditText mName;
    private TextView mEmail;
    private Button mChange;

    private File mPhotoFile = null;
    private Uri mImageCaptureUri;
    private Bitmap rotatedBitmap;
    private Uri uriFinal = null;

    private boolean isFirstTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        lougoutButton = findViewById(R.id.logout);
        mImageview = findViewById(R.id.profilepicture);
        mName = findViewById(R.id.nameView);
        mEmail = findViewById(R.id.emailView);
        mChange = findViewById(R.id.changebutton);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        checkPermission();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            mEmail.setText(email);
            if (name != null){
                mName.setText(name);
            }
            else{
                isFirstTime = true;
                lougoutButton.setEnabled(false);
            }
            if (photoUrl != null){
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUrl);
                    mImageview.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, String.valueOf(isFirstTime));
        setupActionBar();

        mChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        lougoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }


// Action bar
    private void setupActionBar() {
        ActionBar bar = getSupportActionBar();
        if (bar != null){
            bar.setTitle("Settings");
            if (isFirstTime){
                bar.setTitle("Finish Setting Up Your Account");
                bar.setDisplayHomeAsUpEnabled(false);
            }
        }
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
        menu.findItem(R.id.menuItem_settings).setVisible(false);
        menu.findItem(R.id.menuItem_save).setVisible(true);
        return true;
    }

    //
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menuItem_save:
                if (mName.getText().toString() == null || mName.getText().toString().length() < 4){
                    Toast.makeText(this, "Must Enter Name", Toast.LENGTH_LONG).show();
                }
                else{
                    Log.d(TAG, "NAME: " + mName.getText().toString());
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference().child("users").child(mAuth.getUid());

                    if (uriFinal == null){
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(mName.getText().toString())
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "com.frohlich.eventshare.User profile updated.");
                                        }
                                    }
                                });

                        ref.setValue(new User(mName.getText().toString(), mAuth.getCurrentUser().getEmail(), null, mAuth.getUid(), null));
                    }
                    else{
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(mName.getText().toString())
                                .setPhotoUri(uriFinal)
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "com.frohlich.eventshare.User profile updated.");
                                        }
                                    }
                                });

                        //Adding the image to firebase
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        //StorageReference profilePic = storageRef.child(mAuth.getUid() + "/profile.jpg");
                        StorageReference profilePicRef = storageRef.child("images/" + mAuth.getUid());
                        UploadTask uploadTask = profilePicRef.putFile(uriFinal);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads

                                Toast.makeText(getApplicationContext(), "Profile photo upload failed", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getApplicationContext(), "Profile photo upload success", Toast.LENGTH_SHORT).show();
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.

                            }
                        });
                        ref.setValue(new User(mName.getText().toString(), mAuth.getCurrentUser().getEmail(), "images/" + mAuth.getUid(), mAuth.getUid(), null));
                    }
                    //Intent here
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                return true;
        }
        return false;
    }
    ////End Action BAr

    //Disbles back button
    @Override
    public void onBackPressed() {

    }

    private void checkPermission() {
        if(Build.VERSION.SDK_INT < 23) return; if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_PHOTO_PERMISSION){
            if(permissions[0].equalsIgnoreCase(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //startActivity(takePictureIntent, REQUEST_CODE_PHOTO_PERMISSION );
            }
        }
        else{

        }
    }

    private void showDialog(){
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(R.string.alert_dialog_two_buttons_title);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    //For camera popup
    public void doPositiveClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Positive click!");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            mPhotoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            ex.printStackTrace();
        }
        if (mPhotoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID  + ".fileprovider", mPhotoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        }
        try {
            // Start a camera capturing activity
            // REQUEST_CODE_TAKE_FROM_CAMERA is an integer tag you
            // defined to identify the activity in onActivityResult()
            // when it returns
            startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Camera negative popup
    public void doNegativeClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        try {
            mPhotoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            ex.printStackTrace();
        }
        if (mPhotoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", mPhotoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        }
        startActivityForResult(intent, LOAD_IMG);
    }

    //Activity result for cropping handling
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case REQUEST_CODE_TAKE_FROM_CAMERA:
                Bitmap rotatedBitmap = imageOrientationValidator(mPhotoFile);
                try {
                    FileOutputStream fOut = new FileOutputStream(mPhotoFile);
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Send image taken from camera for cropping
                mImageCaptureUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        mPhotoFile);
                beginCrop(mImageCaptureUri);
                break;

            case Crop.REQUEST_CROP: //We changed the RequestCode to the one being used by the library.
                // Update image view after image crop
                handleCrop(resultCode, data);
                break;

            case LOAD_IMG:
                uriFinal = data.getData();
                beginCrop(uriFinal);
                break;

        }
    }


    ///////ALERT DIALOG for selecting photo
    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int title) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("Select a photo", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int title = getArguments().getInt("title");

            AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());

            // Set the title for the dialogtitle
            myDialog.setTitle("Select Image");

            // set up two options
            myDialog.setPositiveButton(R.string.take_photo,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) { ((SettingsActivity) getActivity()).doPositiveClick();
                        }
                    });
            myDialog.setNegativeButton(R.string.select_from_gallery,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((SettingsActivity) getActivity()).doNegativeClick();
                        }
                    });

            return myDialog.create();

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }


    /////////////////////////CROPPING HELP
    private void beginCrop(Uri source) {
        // Continue only if the File was successfully created
        if (mPhotoFile != null) {
            Uri destination = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    mPhotoFile);
            Log.d("URI: ", destination.toString());
            Crop.of(source, destination).asSquare().start(this);
        }

    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                mImageview.setImageBitmap(bitmap);
                uriFinal = uri;
            } catch (Exception e) {
                Log.d("Error", "error");
            }

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap imageOrientationValidator(File photoFile) {
        ExifInterface ei;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", photoFile));
            ei = new ExifInterface(photoFile.getAbsolutePath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            rotatedBitmap = null;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);

                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);

                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);

                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;

                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotatedBitmap;
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

///////////////////////////////END CROPPING UTILS



}
