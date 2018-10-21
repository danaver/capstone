package com.example.gailsemilladucao.capstone.model;

import com.google.gson.annotations.SerializedName;

public class wordbank{
    private String English;
    private String Cebuano;
    private String Pronunciation;
    @SerializedName("POS")
    private String pos;
    private String Audio;
    private String Picture;
    private String Effect;
    private String Status;

    public wordbank(String english, String cebuano, String pronunciation, String pos, String audio, String status) {
        English = english;
        Cebuano = cebuano;
        Pronunciation = pronunciation;
        this.pos = pos;
        Audio = audio;
        Status = status;
    }

    public wordbank(String english, String cebuano, String pronunciation, String pos, String audio, String picture, String effect, String status) {
        English = english;
        Cebuano = cebuano;
        Pronunciation = pronunciation;
        this.pos = pos;
        Audio = audio;
        Picture = picture;
        Effect = effect;
        Status = status;
    }

    public wordbank(String english, String cebuano, String pronunciation, String pos, String audio, String picture, String status) {
        English = english;
        Cebuano = cebuano;
        Pronunciation = pronunciation;
        this.pos = pos;
        Audio = audio;
        Picture = picture;
        Status = status;
    }

    public wordbank(String english, String cebuano, String audio, String picture, String effect) {
        English = english;
        Cebuano = cebuano;
        Audio = audio;
        Picture = picture;
        Effect = effect;
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

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getAudio() {
        return Audio;
    }

    public void setAudio(String audio) {
        Audio = audio;
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

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}