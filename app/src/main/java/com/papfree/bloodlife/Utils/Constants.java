package com.papfree.bloodlife.Utils;

public final class Constants {

    /**
     * Constants for Firebase object properties
     */
    public static final String FIREBASE_URL = "https://bloodlife.firebaseio.com";
    public static final String FIREBASE_PROPERTY_EMAIL = "email";
    public static final String FIREBASE_LOCATION_USERS = "users";
    public static final String FIREBASE_LOCATION_ORGS = "organizations";
    public static final String FIREBASE_URL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USERS;
    public static final String FIREBASE_URL_ORGS = FIREBASE_URL + "/" + FIREBASE_LOCATION_ORGS;
    public static final String FIREBASE_PROPERTY_USER_HAS_LOGGED_IN_WITH_PASSWORD = "hasLoggedInWithPassword";
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";


    public static final String KEY_ENCODED_EMAIL = "ENCODED_EMAIL";
    public static final String KEY_PROVIDER = "PROVIDER";
    public static final String GOOGLE_PROVIDER = "google";
    public static final String KEY_SIGNUP_EMAIL = "SIGNUP_EMAIL";
    public static final String PASSWORD_PROVIDER = "password";
    public static final String KEY_GOOGLE_EMAIL = "GOOGLE_EMAIL";
    public static final String PROVIDER_DATA_DISPLAY_NAME = "displayName";
}
