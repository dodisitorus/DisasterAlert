package com.dodi.disasteralert;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dodi.disasteralert.utils.BlurImage;
import com.dodi.disasteralert.utils.UserPreferences;
import com.dodi.disasteralert.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private boolean isViewRegister = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        LinearLayout linearLayout = findViewById(R.id.parent);
        // create blur background image
        Bitmap bg1 = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.bg_login_image2);
        Bitmap bitmap = BlurImage.fastblur(bg1, 1f, 50);
        BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
        linearLayout.setBackground(ob);

        // Set up the login form.
        mEmailView = findViewById(R.id.email);

        mPasswordView = findViewById(R.id.password);

        final Button backSignUpButton = findViewById(R.id.back_sign_in_button);
        final Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptClickBtn("login");
            }
        });

        Button mEmailSignUpButton = findViewById(R.id.email_sign_up_button);
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isViewRegister) {
                    attemptClickBtn("register");
                } else {
                    mEmailSignInButton.setVisibility(View.GONE);
                    backSignUpButton.setVisibility(View.VISIBLE);
                    mPasswordView.setText("");
                    mEmailView.setText("");
                    isViewRegister = true;
                }
            }
        });

        backSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmailSignInButton.setVisibility(View.VISIBLE);
                backSignUpButton.setVisibility(View.GONE);
                mPasswordView.setText("");
                mEmailView.setText("");
                isViewRegister = false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptClickBtn(String type) {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            if (type.equals("login")) {
                LoginFirebaseWithEmail(email, password);
            } else {
                createFirebaseWithEmail(email, password);
            }
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void LoginFirebaseWithEmail(String mEmail, String mPassword) {
        Utils.hideSoftKey(mPasswordView);
        mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showProgress(false);
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            UserPreferences.setData(UserPreferences.userIdKey, user.getUid(), getApplicationContext());
                            UserPreferences.setData(UserPreferences.nameKey, user.getDisplayName(), getApplicationContext());
                            UserPreferences.setData(UserPreferences.emailKey, user.getEmail(), getApplicationContext());
                            UserPreferences.setDataBool(UserPreferences.loginKey, true, getApplicationContext());
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            showProgress(false);
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createFirebaseWithEmail(String mEmail, String mPassword) {
        Utils.hideSoftKey(mPasswordView);
        mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showProgress(false);
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            UserPreferences.setData(UserPreferences.userIdKey, user.getUid(), getApplicationContext());
                            UserPreferences.setData(UserPreferences.nameKey, user.getDisplayName(), getApplicationContext());
                            UserPreferences.setData(UserPreferences.emailKey, user.getEmail(), getApplicationContext());
                            UserPreferences.setDataBool(UserPreferences.loginKey, true, getApplicationContext());
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            showProgress(false);
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

