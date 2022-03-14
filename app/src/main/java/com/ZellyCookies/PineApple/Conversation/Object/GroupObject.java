package com.ZellyCookies.PineApple.Conversation.Object;

import com.ZellyCookies.PineApple.Utils.User;

import java.io.Serializable;

//group feature methods
public class GroupObject implements Serializable {
    private String chatId;

    private User userMatch;

    public GroupObject(String chatId, User userMatch) {
        this.chatId = chatId;
        this.userMatch = userMatch;
    }

    public GroupObject(User userMatch) {
        this.userMatch = userMatch;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public User getUserMatch() {
        return userMatch;
    }

    public void setUserMatch(User userMatch) {
        this.userMatch = userMatch;
    }
}
