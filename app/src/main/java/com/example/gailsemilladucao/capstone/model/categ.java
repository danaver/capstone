package com.example.gailsemilladucao.capstone.model;

public class categ {
    String name;
    String view;
    int img;
    String download;
    String delete;

    public categ() {
    }

    public categ(String name, String view, int img, String download, String delete) {
        this.name = name;
        this.view = view;
        this.img = img;
        this.download = download;
        this.delete = delete;
    }

    public categ(String name, int img) {
        this.name = name;
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

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
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
