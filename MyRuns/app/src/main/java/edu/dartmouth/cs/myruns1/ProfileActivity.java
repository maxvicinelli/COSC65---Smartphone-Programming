package edu.dartmouth.cs.myruns1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.provider.MediaStore;
import android.view.View;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import edu.dartmouth.cs.myruns1.utils.Preference;


public class ProfileActivity extends AppCompatActivity {

    public static final String DIALOG_ID_KEY = "1";

    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    public static final int REQUEST_CODE_TAKE_FROM_GALLERY = 1;
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    private static final int ID_PHOTO_PICKER_FROM_CAMERA = 0;
    private static final int ID_PHOTO_PICKER_FROM_GALLERY = 1;
    private static final String TAG = "lifecycle";

    private Button mChangeButton;
    private EditText mNameView, mEmailView, mPasswordView, mPhoneView, mMajorView, mClassView;
    private RadioButton mFemaleView, mMaleView;
    private RadioGroup mGenderView;
    private Uri  mCroppedUri;
    private File mPhotoFile = null;
    private ImageView mImageView;
    private String mPicPath;

    private Preference preference;

    private Bitmap rotatedBitmap;

    private boolean editingProfile = false;
    private static final int EDITING_PROFILE = 0;
    private String initial_password = "___";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpActionBar();

        setContentView(R.layout.activity_profile);
        setTitle("Sign Up");

        checkPermissions();

        mChangeButton = findViewById(R.id.change_button);
        mNameView = findViewById(R.id.edit_name);
        mEmailView = findViewById(R.id.edit_email);
        mPasswordView = findViewById(R.id.edit_pass);
        mPhoneView = findViewById(R.id.edit_phone);
        mMajorView = findViewById(R.id.edit_major);
        mClassView = findViewById(R.id.edit_class);
        mImageView = findViewById(R.id.image_view);


        if (savedInstanceState != null) {
            mCroppedUri = savedInstanceState.getParcelable(URI_INSTANCE_STATE_KEY);
            try {
                if (mCroppedUri != null) {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCroppedUri);
                    mImageView.setImageBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        mFemaleView = findViewById(R.id.radioButtonF);
        mMaleView = findViewById(R.id.radioButtonM);
        mGenderView = findViewById(R.id.gender_group);



        preference = new Preference(this);

        Intent intent = getIntent();
        int purpose = intent.getIntExtra("intent", -1);
        if (purpose == EDITING_PROFILE) {
            loadProfile();
        }
        mChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangePhotoClicked(view);
            }
        });
    }

    private void loadProfile() {
        editingProfile = true;

        if (preference.getProfileEmail() != "nan") {
            mEmailView.setText(preference.getProfileEmail());
        }
        if (preference.getProfilePassword() != "nan") {
            mPasswordView.setText(preference.getProfilePassword());
            initial_password = preference.getProfilePassword();

        }
        if (preference.getProfileName() != "nan") {
            mNameView.setText(preference.getProfileName());
        }
        if (preference.getProfileGender() != -1) {
            if (preference.getProfileGender() == 1) {
                mMaleView.setChecked(true);
            }
            else {
                mFemaleView.setChecked(true);
            }
        }
        if (preference.getProfilePhone() != "nan") {
            mPhoneView.setText(preference.getProfilePhone());
        }
        if (preference.getProfileMajor() != "nan") {
            mMajorView.setText(preference.getProfileMajor());
        }
        if (preference.getProfileClass() != "nan") {
            mClassView.setText(preference.getProfileClass());
        }
        if (preference.getProfilePic() != "nan") {
            mPicPath = preference.getProfilePic();
            mCroppedUri = Uri.parse(mPicPath);
            mImageView.setImageURI(mCroppedUri);
        }

        mEmailView.setEnabled(false);


    }

    private void checkPermissions() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (mCroppedUri != null) {
            Log.d(TAG, "saved cropped image");
            outState.putParcelable(URI_INSTANCE_STATE_KEY, mCroppedUri);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This permission is important for this app").setTitle("Important Permission Required");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CODE_TAKE_FROM_CAMERA);
                    }
                });
                builder.show();
            } else {
                //Never ask again and handle your app without permission
            }
        }
    }

    private void onPhotoPickerItemSelected(int item) {
        Intent intent;

        // Log.d(TAG, "number" + item);

        // Take picture from Camera
        if (item == ID_PHOTO_PICKER_FROM_CAMERA) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mPhotoFile != null) {
                mCroppedUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, mPhotoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCroppedUri);
            }
            try {
                startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
            } catch (ActivityNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        // Take picture from gallery
        else if (item == ID_PHOTO_PICKER_FROM_GALLERY) {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (mPhotoFile != null) {
                mCroppedUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, mPhotoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCroppedUri);
            }
            startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_GALLERY);
        }
    }

    // Called when change button is selected, begin dialog
    public void onChangePhotoClicked(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Profile Image");
        // which corresponds to dialog button, 0 for camera, 1 for gallery
        DialogInterface.OnClickListener dlistener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onPhotoPickerItemSelected(which);
            }
        };
        builder.setItems(R.array.ui_profile_photo_picker_items, dlistener);
        builder.show();

    }


    // Create file for taken image, save to mPicPath
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

        mPicPath = "file:" + image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

                mCroppedUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, mPhotoFile);
                beginCrop(mCroppedUri);
                break;
            case Crop.REQUEST_CROP:
                handleCrop(resultCode, data);
                break;
            case ID_PHOTO_PICKER_FROM_GALLERY:
                mCroppedUri = data.getData();
                beginCrop(mCroppedUri);
                break;
        }


    }

    // Cropping Methods

    private void beginCrop(Uri source) {
        if (mPhotoFile != null) {
            Uri destination = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, mPhotoFile);
            Log.d("URI: ", destination.toString());
            Crop.of(source, destination).asSquare().start(this);
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                mImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.d("Error", "error");
            }

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    //Ensures image is oriented correctly
    private Bitmap imageOrientationValidator(File photoFile) {
        ExifInterface exif_interface;
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID,
                    photoFile));
            exif_interface = new ExifInterface(photoFile.getAbsolutePath());
            int orientation = exif_interface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            rotatedBitmap = null;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bm, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bm, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bm, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bm;
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotatedBitmap;
    }

    // Rotates image based on given angle
    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix m = new Matrix();
        m.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), m, true);
    }


    // Saves profile when register button is clicked
    private void saveProfile() {
        mNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String name = mNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String phone = mPhoneView.getText().toString();
        String major = mMajorView.getText().toString();
        String dartmouthClass = mClassView.getText().toString();

        int gender = -1;
        if (mFemaleView.isChecked())
            gender = 0;
        else if (mMaleView.isChecked())
            gender = 1;

        boolean cancel = false;
        View focusView = null;

        // check validity of password
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("This field is required");
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError("Password must be at least six characters");
            focusView = mPasswordView;
            cancel = true;
        }

        // check validity of email
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("This field is required");
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError("Email must contain @");
            focusView = mEmailView;
            cancel = true;
        }

        // check validity of phone
        if (!isPhoneValid(phone)) {
            mPhoneView.setError("Phone number must be 9 digits");
            focusView = mPhoneView;
            cancel = true;
        }

        //check for valid gender
        if (gender == -1) {
            focusView = mGenderView;
            cancel = true;
        }

        if (TextUtils.isEmpty(major)) {
            mMajorView.setError("This field is required");
            focusView = mMajorView;
            cancel = true;
        }

        if (TextUtils.isEmpty(dartmouthClass)) {
            mClassView.setError("This field is required");
            focusView = mClassView;
            cancel = true;
        }

        if (TextUtils.isEmpty(phone)) {
            mClassView.setError("This field is required");
            focusView = mPhoneView;
            cancel = true;
        }

        if (cancel) {
            if (focusView instanceof EditText)
                focusView.requestFocus();
            else if (focusView instanceof RadioGroup)
                Toast.makeText(this, "Gender is required", Toast.LENGTH_SHORT).show();
        }
        else {

            preference.clearProfileInfo();
            preference.setProfileName(name);
            preference.setProfileClass(dartmouthClass);
            preference.setProfileEmail(email);
            preference.setProfileMajor(major);
            preference.setProfilePassword(password);
            preference.setProfilePhone(phone);
            preference.setProfileGender(gender);

            if (mPicPath != null) {
                if (!mPicPath.equalsIgnoreCase("nan")) {
                    preference.setProfilePic(mPicPath);
                }
            } else {
                Toast.makeText(this, "You don't have a profile picture!", Toast.LENGTH_LONG).show();
            }

            editingProfile = false;

            Toast.makeText(this, "Successfully Registered", Toast.LENGTH_SHORT).show();

            if (initial_password.equals(preference.getProfilePassword())) {
                finish();
            }
            else {
                Log.d(TAG, "you changed it");
                Intent intent = new Intent(this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }

    }


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private boolean isPhoneValid(String phone) {
        return phone.length() > 8;
    }


    //Set up action bar, specifically back button and register button
    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (editingProfile) {
            getMenuInflater().inflate(R.menu.menu_save, menu);
            return super.onCreateOptionsMenu(menu);
        }
        else {
            getMenuInflater().inflate(R.menu.menu_register, menu);
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Back button
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_register:
                saveProfile();
                return true;
            case R.id.action_save:
                saveProfile();
        }
        return super.onOptionsItemSelected(item);
    }



}
