package com.tuanmhoang.aws.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SqsMessage {

    private String user_id;
    private int attempts;

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }
}
