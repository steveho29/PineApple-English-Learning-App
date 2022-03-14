package com.ZellyCookies.PineApple.Utils;

import java.io.Serializable;

//User class
public class User implements Serializable {
    private String user_id;
    private String phone_number;
    private String email;
    private String username;
    private boolean SE;
    private boolean db;
    private boolean ui;
    private boolean oop;
    private String description;
    private String sex;
    private String preferSex;
    private String dateOfBirth;
    private String profileImageUrl;
    private double latitude;
    private double longtitude;

    public User() {
    }

    //define attributes
    public User(String sex, String preferSex, String user_id, String phone_number, String email, String username, boolean SE, boolean db, boolean ui,boolean oop, String description, String dateOfBirth, String profileImageUrl, double latitude, double longtitude) {
        this.sex = sex;
        this.user_id = user_id;
        this.phone_number = phone_number;
        this.email = email;
        this.username = username;
        this.SE = SE;
        this.db = db;
        this.ui = ui;
        this.oop = oop;
        this.description = description;
        this.preferSex = preferSex;
        this.dateOfBirth = dateOfBirth;
        this.profileImageUrl = profileImageUrl;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    //define behaviors
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSE() {
        return SE;
    }

    public void setSE(boolean SE) {
        this.SE = SE;
    }

    public boolean isDatabase() {
        return db;
    }

    public void setDatabase(boolean db) {
        this.db = db;
    }

    public boolean isDesign() {
        return ui;
    }

    public void setDesign(boolean ui) {
        this.ui = ui;
    }

    public boolean isOop() {
        return oop;
    }

    public void setOop(boolean oop) {
        this.oop = oop;
    }

    public String getPreferSex() {
        return preferSex;
    }

    public void setPreferSex(String preferSex) {
        this.preferSex = preferSex;
    }

    // Added new attribute date of birth.
    public String getDateOfBirth() { return dateOfBirth; }

    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", SE=" + SE +
                ", Database=" + db +
                ", UI=" + ui +
                ", OOP=" + oop +
                ", description='" + description + '\'' +
                ", sex='" + sex + '\'' +
                //", preferSex='" + preferSex + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                '}';
    }
}