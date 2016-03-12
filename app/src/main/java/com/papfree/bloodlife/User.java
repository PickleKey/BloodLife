package com.papfree.bloodlife;

public class User {

    String name,email,password;
    int age;

    public User(String name, int age, String email, String password){
        this.name =name;
        this.age =age;
        this.email=email;
        this.password =password;
    }

    public User(String email, String password) {
        this.email=email;
        this.password=password;
    }
}
