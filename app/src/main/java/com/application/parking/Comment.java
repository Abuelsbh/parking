package com.application.parking;

public class Comment {
    String Comment;
    Float Rate;
    String User;

    public Comment(String comment, Float rate, String user) {
        Comment = comment;
        Rate = rate;
        User = user;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public void setRate(Float rate) {
        Rate = rate;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getComment() {
        return Comment;
    }

    public Float getRate() {
        return Rate;
    }

    public String getUser() {
        return User;
    }
}
