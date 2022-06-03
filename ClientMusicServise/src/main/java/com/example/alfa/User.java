package com.example.alfa;
public class User {
    public String name;
    public String mail;
    public String password;

    public User(){
        name = "";
        mail = "";
        password = "";
    }

    public User(String pName, String pPass){
        name = pName;
        mail = "";
        password = pPass;
    }

    public User(String pName, String pPass, String pMail){
        name = pName;
        mail = pMail;
        password = pPass;
    }
}

