package com.example.myfirstapp;

import java.io.Serializable;

public class User implements Serializable {
    public String email, token;

    public User(){

    }
    public User(String email, String token) {
        this.email = email;
        this.token = token;
    }


}
