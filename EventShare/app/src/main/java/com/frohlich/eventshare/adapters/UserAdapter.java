package com.frohlich.eventshare.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.frohlich.eventshare.CreateGroupActivity;
import com.frohlich.eventshare.R;
import com.frohlich.eventshare.User;
import com.frohlich.eventshare.utils.getProfileImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UserAdapter extends ArrayAdapter<User> {


    private static final String TAG = "Useradapter";
    private ArrayList<User> users;
    private Context context;

    private TextView userName, userEmail;
    private ImageView userProfilePic;
    private Button mAddButton;



    public UserAdapter(@NonNull Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        final User user = users.get(position);
        view = LayoutInflater.from(context).inflate(R.layout.user_model, parent, false);
        userName = view.findViewById(R.id.user_list_name);
        userEmail = view.findViewById(R.id.email_user_model);
        userProfilePic = view.findViewById(R.id.user_image);
        //mAddButton = view.findViewById(R.id.add_btn);
        //int type = 0;

        userName.setText(user.getName());
        userEmail.setText(user.getEmail());

        String stringURI = user.getUri();
        if (!stringURI.equals("DEFAULT")) {
            // Create a storage reference from our app
            try {
                getProfileImage.getProfileImg(userProfilePic, user.getUri());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //getProfileImage p = new getProfileImage(userProfilePic, "images/" + user.getUri());
        }
        else {
            userProfilePic.setImageResource(R.drawable.blankprofilepicture);
        }


//        mAddButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        return view;
    }

    @Nullable
    @Override
    public User getItem(int position) {
        return super.getItem(position);
    }

//   public ArrayList<User> getAddedUsers(){
//        return addedUsers;
//    }
}
