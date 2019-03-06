package com.example.fang.model;

public class LoginInfo {
    private String name;
    private String token;
    private String id;

    public LoginInfo(LoginStatusEnum status) {
        this.status = status;
    }

    private LoginStatusEnum status;

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public String getId() {
        return id;
    }

    public LoginStatusEnum getStatus() {
        return status;
    }


    public LoginInfo(String name, String id, String token, LoginStatusEnum status) {
        this.status = status;
        if (status!=LoginStatusEnum.SUCCESS)
            return;
        this.name = name;
        this.token = token;
        this.id = id;
    }



}
