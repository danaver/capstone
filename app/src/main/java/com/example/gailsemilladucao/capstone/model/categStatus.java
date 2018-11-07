package com.example.gailsemilladucao.capstone.model;

import com.google.gson.annotations.SerializedName;

public class categStatus {

    int adjective;
    int animal;
    @SerializedName("body part")
    int body_part;
    int event;
    int food;
    int number;
    int person;
    int place;
    int thing;
    int verb;

    public categStatus() {
    }

    public categStatus(int adjective, int animal, int body_part, int event, int food, int number, int person, int place, int thing, int verb) {
        this.adjective = adjective;
        this.animal = animal;
        this.body_part = body_part;
        this.event = event;
        this.food = food;
        this.number = number;
        this.person = person;
        this.place = place;
        this.thing = thing;
        this.verb = verb;
    }

    public int getAdjective() {
        return adjective;
    }

    public void setAdjective(int adjective) {
        this.adjective = adjective;
    }

    public int getAnimal() {
        return animal;
    }

    public void setAnimal(int animal) {
        this.animal = animal;
    }

    public int getBody_part() {
        return body_part;
    }

    public void setBody_part(int body_part) {
        this.body_part = body_part;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public int getFood() {
        return food;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getPerson() {
        return person;
    }

    public void setPerson(int person) {
        this.person = person;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public int getThing() {
        return thing;
    }

    public void setThing(int thing) {
        this.thing = thing;
    }

    public int getVerb() {
        return verb;
    }

    public void setVerb(int verb) {
        this.verb = verb;
    }
}
