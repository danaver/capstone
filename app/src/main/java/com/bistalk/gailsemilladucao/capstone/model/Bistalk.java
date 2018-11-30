package com.bistalk.gailsemilladucao.capstone.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Bistalk {
    int update;
    @SerializedName("wordbank")
    List<wordbanks> wordbankList;

    public Bistalk() {
    }

    public Bistalk(int update, List<wordbanks> wordbankList) {
        this.update = update;
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

    public List<wordbanks> getWordbankList() {
        return wordbankList;
    }

    public void setWordbankList(List<wordbanks> wordbankList) {
        this.wordbankList = wordbankList;
    }



}
