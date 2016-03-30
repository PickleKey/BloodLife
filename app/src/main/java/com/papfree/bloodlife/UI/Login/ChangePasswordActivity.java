package com.papfree.bloodlife.UI.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.papfree.bloodlife.UI.BaseActivity;
import com.papfree.bloodlife.R;
import com.papfree.bloodlife.UI.MainActivity;
import com.papfree.bloodlife.Utils.Constants;



public class ChangePasswordActivity extends BaseActivity {
    private static final String LOG_TAG = ChangePasswordActivity.class.getSimpleName();
    private ProgressDialog mAuthProgressDialog;
    private Firebase mFirebaseRef;
    private SharedPreferences mSharedPref;
    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;
    private EditText mEditTextNewPasswordInput, mEditTextConfirmPasswordInput;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);

        initializeScreen();

        mEditTextNewPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    signInPassword();
                }
                return true;
            }
        });

    }


    /**
     * Sign in with Password provider when user clicks sign in button
     */
    public void onSignInPressed(View view) {
        signInPassword();
    }


    /**
     * Link layout elements from XML and setup the progress dialog
     */
    public void initializeScreen() {
        mEditTextNewPasswordInput = (EditText) findViewById(R.id.input_new_password);
        mEditTextConfirmPasswordInput = (EditText) findViewById(R.id.input_confirm_password);
        LinearLayout linearLayoutLoginActivity = (LinearLayout) findViewById(R.id.linear_layout_changePassword_activity);
        initializeBackground(linearLayoutLoginActivity);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getString(R.string.progress_dialog_changePassword_with_firebase));
        mAuthProgressDialog.setCancelable(false);
    }

    /**
     * Sign in with Password provider (used when user taps "Done" action on keyboard)
     */
    public void signInPassword() {
        final String newPassword = mEditTextNewPasswordInput.getText().toString();
        String confirmPassword = mEditTextConfirmPasswordInput.getText().toString();

        /**
         * If newPassword and changePassword are not empty show progress dialog and try to authenticate
         */
        if (newPassword.equals("")) {
            mEditTextNewPasswordInput.setError(getString(R.string.error_cannot_be_empty));
            return;
        }

        if (confirmPassword.equals("")) {
            mEditTextConfirmPasswordInput.setError(getString(R.string.error_cannot_be_empty));
            return;
        }
        if (!(newPassword.equals(confirmPassword))) {
            showErrorToast(getString(R.string.error_password_did_not_match));
            return;
        }
        mAuthProgressDialog.show();
        //TODO remove this line later.used for testing for now.
        /* Go to main activity */
       /* Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();*/
        final String unprocessedEmail = mFirebaseRef.getAuth().getProviderData().get(Constants.FIREBASE_PROPERTY_EMAIL).toString().toLowerCase();
        String oldPassword = mSharedPref.getString(Constants.PASSWORD_PROVIDER, mPassword);
        //TODO uncomment this for validating with firebase.
        final Firebase userRef = new Firebase(Constants.FIREBASE_URL_USERS).child(mEncodedEmail);
        final Firebase orgRef = new Firebase(Constants.FIREBASE_URL_ORGS).child(mEncodedEmail);

        if(orgRef != null){
            mFirebaseRef.changePassword(unprocessedEmail,oldPassword, newPassword, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    orgRef.child(Constants.FIREBASE_PROPERTY_USER_HAS_LOGGED_IN_WITH_PASSWORD).setValue(true);
                                        /* The password was changed */
                    Log.d(LOG_TAG, getString(R.string.log_message_password_changed_successfully)+ ":" + newPassword);

                        /* Go to main activity */
                    Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    Log.d(LOG_TAG, getString(R.string.log_error_failed_to_change_password) + firebaseError);
                    mAuthProgressDialog.dismiss();
                    showErrorToast(firebaseError.getMessage());

                        /* Go to Login activity */
                    Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }
            });
        }else {

            mFirebaseRef.changePassword(unprocessedEmail, oldPassword, newPassword, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    userRef.child(Constants.FIREBASE_PROPERTY_USER_HAS_LOGGED_IN_WITH_PASSWORD).setValue(true);
                                        /* The password was changed */
                    Log.d(LOG_TAG, getString(R.string.log_message_password_changed_successfully) + ":" + newPassword);

                        /* Go to main activity */
                    Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    Log.d(LOG_TAG, getString(R.string.log_error_failed_to_change_password) + firebaseError);
                    mAuthProgressDialog.dismiss();
                    showErrorToast(firebaseError.getMessage());

                        /* Go to Login activity */
                    Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }
            });
        }

    }


    /**
     * Show error toast to users
     */
    private void showErrorToast(String message) {
        Toast.makeText(ChangePasswordActivity.this, message, Toast.LENGTH_LONG).show();
    }


}
