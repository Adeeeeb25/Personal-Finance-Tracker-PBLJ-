package com.finance.model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String passwordPlain; // Demo-only plain-text storage

    public User() {
    }

    public User(String username, String passwordPlain) {
        this.username = username;
        this.passwordPlain = passwordPlain;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordPlain() {
        return passwordPlain;
    }

    public void setPasswordPlain(String passwordPlain) {
        this.passwordPlain = passwordPlain;
    }
}
