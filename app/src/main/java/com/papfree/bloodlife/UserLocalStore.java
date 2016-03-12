package com.papfree.bloodlife;

import android.content.Context;
import android.content.SharedPreferences;

public class UserLocalStore {

    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalStore;

    public UserLocalStore(Context context){

        userLocalStore = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user){
        SharedPreferences.Editor spEditor = userLocalStore.edit();
        spEditor.putString("name",user.name);
        spEditor.putInt("age", user.age);
        spEditor.putString("email", user.email);
        spEditor.putString("password", user.password);
        spEditor.commit();
    }

    public User getLoggedInUser(){
        String name = userLocalStore.getString("name", "");
        int age =userLocalStore.getInt("age", -1);
        String email = userLocalStore.getString("email", "");
        String password = userLocalStore.getString("password", "");

        User storedUser = new User(name, age, email, password);
        return storedUser;
    }

    public  void setUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor = userLocalStore.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.commit();
    }

    public boolean getUserLoogedIn(){
        if(userLocalStore.getBoolean("loggedIn", false) == true){
            return true;
        }else{
            return false;
        }
    }

    public void clearUserData(){
        SharedPreferences.Editor spEditor = userLocalStore.edit();
        spEditor.clear();
        spEditor.commit();
    }


}
