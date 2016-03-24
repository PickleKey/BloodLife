package com.papfree.bloodlife.Model;

import java.util.HashMap;

public class User {

    private String name;
    private String age;
    private String email;
    private boolean hasLoggedInWithPassword;
    private HashMap<String, Object> timestampJoined;

    public User() {
    }

    public User(String name, String age, String email, HashMap<String, Object> timestampJoined) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.timestampJoined = timestampJoined;
        this.hasLoggedInWithPassword = false;
    }

    public User(String name, String email, HashMap<String, Object> timestampJoined) {
        this.name = name;
        this.email = email;
        this.timestampJoined = timestampJoined;
        this.hasLoggedInWithPassword = false;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }

    public boolean isHasLoggedInWithPassword() {
        return hasLoggedInWithPassword;
    }


}
