package com.example.testlibrary;

public class Admin {
    private static final Admin ourInstance = new Admin();

    public static Admin getInstance() {
        return ourInstance;
    }

    private Admin() {
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String username;
    private String password;


    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

}
