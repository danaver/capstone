package com.example.gailsemilladucao.capstone.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class wordbanks implements Serializable {
    private String English;
    private String Cebuano;
    private String Pronunciation;
    private String Category;
    private String Audio;
    private String Picture;
    private String Effect;
    private int Status;

    public wordbanks() {
    }

    public wordbanks(String english, String cebuano, String pronunciation, String category, String audio, int status) {
        English = english;
        Cebuano = cebuano;
        Pronunciation = pronunciation;
        Category = category;
        Audio = audio;
        Status = status;
    }

    public wordbanks(String english, String cebuano, String pronunciation, String category, String audio, String picture, String effect, int status) {
        English = english;
        Cebuano = cebuano;
        Pronunciation = pronunciation;
        Category = category;
        Audio = audio;
        Picture = picture;
        Effect = effect;
        Status = status;
    }

    public wordbanks(String english, String cebuano, String pronunciation, String category, String audio, String picture, int status) {
        English = english;
        Cebuano = cebuano;
        Pronunciation = pronunciation;
        Category = category;
        Audio = audio;
        Picture = picture;
        Status = status;
    }

    public wordbanks(String english, String cebuano, String audio, String picture, String effect) {
        English = english;
        Cebuano = cebuano;
        Audio = audio;
        Picture = picture;
        Effect = effect;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public String getEffect() {
        return Effect;
    }

    public void setEffect(String effect) {
        Effect = effect;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getEnglish() {
        return English;
    }

    public void setEnglish(String english) {
        English = english;
    }

    public String getCebuano() {
        return Cebuano;
    }

    public void setCebuano(String cebuano) {
        Cebuano = cebuano;
    }

    public String getPronunciation() {
        return Pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        Pronunciation = pronunciation;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getAudio() {
        return Audio;
    }

    public void setAudio(String audio) {
        Audio = audio;
    }
}