package com.example.gailsemilladucao.capstone.model;

public class categ {
    String name;
    String categ;
    int img;
    String download;
    String delete;

    public categ() {
    }

    public categ(String name, String categ, int img, String download, String delete) {
        this.name = name;
        this.categ = categ;
        this.img = img;
        this.download = download;
        this.delete = delete;
    }

    public categ(String name, String categ, int img) {
        this.name = name;
        this.categ = categ;
        this.img = img;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCateg() {
        return categ;
    }

    public void setCateg(String categ) {
        this.categ = categ;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }
}