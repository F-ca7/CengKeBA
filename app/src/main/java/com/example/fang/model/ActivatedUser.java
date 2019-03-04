package com.example.fang.model;

import java.util.List;

/**
 * Created by FANG on 2018/2/15.
 */

public class ActivatedUser {

    /**
     * username : 张新豪
     * term : ["2017-2018学年下学期","第10教学周"]
     */

    private String username;
    private List<String> term;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getTerm() {
        return term;
    }

    public void setTerm(List<String> term) {
        this.term = term;
    }
}

