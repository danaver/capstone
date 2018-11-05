package com.example.gailsemilladucao.capstone.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Bistalk {
    int update;
    @SerializedName("users")
    List<users> userList;
    @SerializedName("wordbank")
    List<wordbanks> wordbankList;


    public Bistalk(int update,List<users> userList, List<wordbanks> wordbankList) {
        this.update = update;
        this.userList = userList;
        this.wordbankList = wordbankList;
    }


    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }

    public Bistalk(List<wordbanks> wordbankList) {
        this.wordbankList = wordbankList;
    }

    public List<users> getUserList() {
        return userList;
    }

    public void setUserList(List<users> userList) {
        this.userList = userList;
    }

    public List<wordbanks> getWordbankList() {
        return wordbankList;
    }

    public void setWordbankList(List<wordbanks> wordbankList) {
        this.wordbankList = wordbankList;
    }
}
