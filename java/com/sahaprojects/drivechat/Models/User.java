package com.sahaprojects.drivechat.Models;

public class User {

   private String user_name,user_id,user_email,user_photo;

    public User(String user_name, String user_id, String user_email, String user_photo) {
        this.user_name = user_name;
        this.user_id = user_id;
        this.user_email = user_email;
        this.user_photo = user_photo;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }
}
