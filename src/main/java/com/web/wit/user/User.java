package com.web.wit.user;

public class User {
    private String username;

    public User(){

    }
    public User(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String toString(){
        return String.format("User: %s", username);
    }
}
