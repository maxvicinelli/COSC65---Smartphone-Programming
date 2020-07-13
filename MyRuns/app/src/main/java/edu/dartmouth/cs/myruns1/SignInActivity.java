package edu.dartmouth.cs.myruns1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

import edu.dartmouth.cs.myruns1.utils.Preference;

public class SignInActivity extends AppCompatActivity {


    Preference preference;
    private Button mSignInButton;
    private Button mRegisterButton;
    private EditText mPasswordView;
    private AutoCompleteTextView mEmailView;
    private View mProgressView;
    private View mLoginFormView;

    private static final String TAG = "CS65";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        getSupportActionBar().setTitle("Sign In");

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mSignInButton = findViewById(R.id.sign_in_button);
        mRegisterButton = findViewById(R.id.register_button);
        mProgressView = findViewById(R.id.login_progress);
        mLoginFormView = findViewById(R.id.login_form);

        preference = new Preference(this);

        if ((preference.getProfileEmail() != "nan") && preference.getProfilePassword() != "nan") {
            mEmailView.setText(preference.getProfileEmail());
            mPasswordView.setText(preference.getProfilePassword());
        }

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //TODO: Only Starts MainAcitivity if crededntials are approved
            public void onClick(View v) {
                attemptLogin();
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, ProfileActivity.class);
                intent.putExtra("intent", 1);
                startActivity(intent);
            }
        });


    }

    private void attemptLogin() {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if ((preference.getProfileEmail().equals("nan")) || (preference.getProfilePassword().equals("nan"))) {
            Toast.makeText(this, "you have to register first", Toast.LENGTH_SHORT).show();

            Log.d(TAG, "login failed");

            if (TextUtils.isEmpty(password)) {
                mPasswordView.setError("This field is required");
                focusView = mPasswordView;
                cancel = true;
            }
            if (!isEmailValid(email)) {
                mEmailView.setError("This field is required");
                focusView = mEmailView;
                cancel = true;
            }
        }

        if ( (!mPasswordView.getText().toString().equals(preference.getProfilePassword())) ||
                (!mEmailView.getText().toString().equals(preference.getProfileEmail()))) {

            mLoginFormView.setVisibility(View.GONE);
            mProgressView.setVisibility(View.VISIBLE);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mLoginFormView.setVisibility(View.VISIBLE);
                    mProgressView.setVisibility(View.GONE);
                }
            }, 50000);


            this.recreate();
            Toast.makeText(this, "Email or password incorrect", Toast.LENGTH_SHORT).show();
            cancel = true;
        }


        if (cancel) {
            if (focusView != null) {
                focusView.requestFocus();
            }
        }

        else {
            mLoginFormView.setVisibility(View.GONE);
            mProgressView.setVisibility(View.VISIBLE);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                }
            }, 2000);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mProgressView.setVisibility(View.GONE);
        mLoginFormView.setVisibility(View.VISIBLE);
    }
}
