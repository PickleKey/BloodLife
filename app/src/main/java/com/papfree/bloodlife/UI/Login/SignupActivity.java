package com.papfree.bloodlife.UI.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.papfree.bloodlife.R;
import com.papfree.bloodlife.Model.User;
import com.papfree.bloodlife.UI.BaseActivity;
import com.papfree.bloodlife.Utils.Constants;
import com.papfree.bloodlife.Utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;


public class SignupActivity extends BaseActivity {
    private static final String LOG_TAG = SignupActivity.class.getSimpleName();
    private ProgressDialog mAuthProgressDialog;
    private Firebase mFirebaseRef;
    private EditText mEditTextUsernameCreate, mEditTextEmailCreate, mEditTextAgeCreate;
    private String mUserName, mAge,mUserEmail, mPassword;
    private SecureRandom mRandom = new SecureRandom();

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);

        /**
         * Link layout elements from XML and setup the progress dialog
         */
        initializeScreen();

    }

    /**
     * Override onCreateOptionsMenu to inflate nothing
     *
     * @param menu The menu with which nothing will happen
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * Link layout elements from XML and setup the progress dialog
     */
    public void initializeScreen() {
        mEditTextUsernameCreate = (EditText) findViewById(R.id.input_name);
        mEditTextEmailCreate = (EditText) findViewById(R.id.input_email);
        mEditTextAgeCreate = (EditText) findViewById(R.id.input_age);
        //mEditTextPasswordCreate = (EditText) findViewById(R.id.edit_text_password_create);
        LinearLayout linearLayoutCreateAccountActivity = (LinearLayout) findViewById(R.id.linear_layout_create_account_activity);
        initializeBackground(linearLayoutCreateAccountActivity);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getResources().getString(R.string.progress_dialog_check_inbox));
        mAuthProgressDialog.setCancelable(false);
    }

    /**
     * Open LoginActivity when user taps on "Sign in" textView
     */
    public void onSignInPressed(View view) {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Open OrgSignUpActivity when user taps on "Sign up" TextView
     */
    public void onOrgSignUpPressed(View view) {
        Intent intent = new Intent(SignupActivity.this, OrgSignupActivity.class);
        startActivity(intent);
    }

    /**
     * Create new account using Firebase email/password provider
     */
    public void onCreateAccountPressed(View view) {

        mUserName = mEditTextUsernameCreate.getText().toString();
        mUserEmail = mEditTextEmailCreate.getText().toString().toLowerCase();
        mAge = mEditTextAgeCreate.getText().toString();
        mPassword = new BigInteger(130, mRandom).toString(32);

        /**
         * Check that email and user name are okay
         */
        boolean validEmail = isEmailValid(mUserEmail);
        boolean validUserName = isUserNameValid(mUserName);
        boolean validAge = isAgeValid(mAge);
        if (!validEmail || !validUserName || !validAge) return;

        /**
         * If everything was valid show the progress dialog to indicate that
         * account creation has started
         */
        mAuthProgressDialog.show();

        /**
         * Create new user with specified email and password
         */
        mFirebaseRef.createUser(mUserEmail, mPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {

                /**
                 * If user was successfully created, run resetPassword() to send temporary 24h
                 * password to the user's email and make sure that user owns specified email
                 */
                mFirebaseRef.resetPassword(mUserEmail, new Firebase.ResultHandler() {
                    @Override
                    public void onSuccess() {
                        mAuthProgressDialog.dismiss();
                        Log.i(LOG_TAG, getString(R.string.log_message_auth_successful));

                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SignupActivity.this);
                        SharedPreferences.Editor spe = sp.edit();

                        /**
                         * Save name and email to sharedPreferences to create User database record
                         * when the registered user will sign in for the first time
                         */
                        spe.putString(Constants.KEY_SIGNUP_EMAIL, mUserEmail).apply();

                        /**
                         * Encode user email replacing "." with ","
                         * to be able to use it as a Firebase db key
                         */
                        createUserInFirebaseHelper();

                        /**
                         *  Password reset email sent, open app chooser to pick app
                         *  for handling inbox email intent
                         */
                        /*Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                        try {
                            startActivity(intent);
                            finish();
                        } catch (android.content.ActivityNotFoundException ex) {
                            *//* User does not have any app to handle email *//*
                        }*/
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        /* Error occurred, log the error and dismiss the progress dialog */
                        Log.d(LOG_TAG, getString(R.string.log_error_occurred) +
                                firebaseError);
                        mAuthProgressDialog.dismiss();
                    }
                });

            }

            @Override
            public void onError(FirebaseError firebaseError) {
                                                /* Error occurred, log the error and dismiss the progress dialog */
                Log.d(LOG_TAG, getString(R.string.log_error_occurred) +
                        firebaseError);
                mAuthProgressDialog.dismiss();
                                                /* Display the appropriate error message */
                if (firebaseError.getCode() == FirebaseError.EMAIL_TAKEN) {
                    mEditTextEmailCreate.setError(getString(R.string.error_email_taken));
                } else {
                    showErrorToast(firebaseError.getMessage());
                }

            }
        });

    }

    /**
     * Creates a new user in Firebase from the Java POJO
     */
    private void createUserInFirebaseHelper() {

        final String encodedEmail = Utils.encodeEmail(mUserEmail);
        final Firebase userLocation = new Firebase(Constants.FIREBASE_URL_USERS).child(encodedEmail);
        /**
         * See if there is already a user (for example, if they already logged in with an associated
         * Google account.
         */
        userLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /* If there is no user, make one */
                if (dataSnapshot.getValue() == null) {
                    /* Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap */
                    HashMap<String, Object> timestampJoined = new HashMap<>();
                    timestampJoined.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    User newUser = new User(mUserName, mAge, encodedEmail, timestampJoined);
                    userLocation.setValue(newUser);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(LOG_TAG, getString(R.string.log_error_occurred) + firebaseError.getMessage());
            }
        });

    }

    private boolean isEmailValid(String email) {
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            mEditTextEmailCreate.setError(String.format(getString(R.string.error_invalid_email_not_valid),
                    email));
            return false;
        }
        return isGoodEmail;

    }

    private boolean isUserNameValid(String userName) {

        if (userName.equals("")) {
            mEditTextUsernameCreate.setError(getResources().getString(R.string.error_cannot_be_empty));
            return false;
        }

        return true;
    }

    private boolean isAgeValid(String age) {

        if (!(StringUtils.isNumeric(age) && age.length() == 2)) {
            mEditTextAgeCreate.setError(getResources().getString(R.string.age_cannot_be_string));
            return false;
        }else if(age.equals("")){
            mEditTextAgeCreate.setError(getResources().getString(R.string.error_cannot_be_empty));
            return false;
        }

        return true;
    }

    /**
     * Show error toast to users
     */
    private void showErrorToast(String message) {
        Toast.makeText(SignupActivity.this, message, Toast.LENGTH_LONG).show();
    }

}





