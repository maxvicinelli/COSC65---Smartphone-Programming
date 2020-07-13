package com.frohlich.eventshare;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = LoginActivity.class.getName(); // tag for logger
    // private LoginViewModel loginViewModel;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        final EditText emailEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("LOGTAG", "ONCLICK");
                final String email = emailEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                Boolean username_good = checkUsername(email);
                Boolean password_good = checkPassword(password);

                if (username_good && password_good){
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        //Toast.makeText(LoginActivity.this, "Login Authentication failed.",
                                                //Toast.LENGTH_SHORT).show();

                                        mAuth.createUserWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            // Sign in success, update UI with the signed-in user's information
                                                            Log.d(TAG, "createUserWithEmail:success");
                                                            FirebaseUser user = mAuth.getCurrentUser();

                                                            ///Add to user database
                                                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                            DatabaseReference ref = database.getReference().child("users").child(mAuth.getUid());
                                                            ref.setValue(new User(null, user.getEmail(), null, user.getUid(), null));

                                                            updateUI(user);
                                                        } else {
                                                            // If sign in fails, display a message to the user.
                                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                            Toast.makeText(LoginActivity.this, "Creation Authentication failed.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            updateUI(null);
                                                        }
                                                    }
                                                });
                                        // ...
                                    }

                                    // ...
                                }
                            });
                }
            }
        });

//        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
//
//        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
//            @Override
//            public void onChanged(@Nullable LoginFormState loginFormState) {
//                if (loginFormState == null) {
//                    return;
//                }
//                loginButton.setEnabled(loginFormState.isDataValid());
//                if (loginFormState.getUsernameError() != null) {
//                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
//                }
//                if (loginFormState.getPasswordError() != null) {
//                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
//                }
//            }
//        });
//
//        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
//            @Override
//            public void onChanged(@Nullable LoginResult loginResult) {
//                if (loginResult == null) {
//                    return;
//                }
//                loadingProgressBar.setVisibility(View.GONE);
//                if (loginResult.getError() != null) {
//                    showLoginFailed(loginResult.getError());
//                }
//                if (loginResult.getSuccess() != null) {
//                    updateUiWithUser(loginResult.getSuccess());
//                }
//                setResult(Activity.RESULT_OK);
//
//                //Complete and destroy login activity once successful
//                finish();
//            }
//        });
//
//        TextWatcher afterTextChangedListener = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // ignore
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // ignore
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString());
//            }
//        };
//        usernameEditText.addTextChangedListener(afterTextChangedListener);
//        passwordEditText.addTextChangedListener(afterTextChangedListener);
//        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    loginViewModel.login(usernameEditText.getText().toString(),
//                            passwordEditText.getText().toString());
//                }
//                return false;
//            }
//        });
//
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadingProgressBar.setVisibility(View.VISIBLE);
//                loginViewModel.login(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString());
//            }
//        });
    }

    private boolean checkPassword(String password) {
        Log.d(TAG, "checking password");
        if (password.length() > 6 && !password.equals(password.toLowerCase())){
            return true;
        }
        else{
            Toast.makeText(LoginActivity.this, R.string.password_error, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean checkUsername(String username) {
        Log.d(TAG, "checking email");
        if (username != null && username.length() > 6 && username.contains("@")){
            return true;

        }
        else{
            Log.d(TAG, "email bad");
            Toast.makeText(LoginActivity.this, getString(R.string.IncorrectEmail), Toast.LENGTH_LONG).show();

            return false;
        }
    }


    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null){
            if (currentUser.getDisplayName() == null){
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }

        }

    }

    @Override
    public void onBackPressed() {

    }

}
