package com.papfree.bloodlife;

import com.firebase.client.Firebase;
import com.firebase.client.Logger;

/**
 * Created by Aravind on 03/20/2016.
 */
public class BloodLifeApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /* Initialize Firebase */
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setLogLevel(Logger.Level.DEBUG);
    }
}
