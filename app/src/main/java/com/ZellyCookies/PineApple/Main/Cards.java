package com.ZellyCookies.PineApple.Main;

public class Cards {
    private String userId;
    private String name, profileImageUrl, bio, interest, dob;
    private int age;
    private int distance;

    public Cards(String userId, String name, String dob, int age, String profileImageUrl, String bio, String interest, int distance) {
        this.userId = userId;
        this.name = name;
        this.dob = dob;
        this.age = age;
        this.profileImageUrl = profileImageUrl;
        this.bio = bio;
        this.interest = interest;
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public String getDob() {
        return dob;
    }

    public String getBio() {
        return bio;
    }

    public String getInterest() {
        return interest;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
