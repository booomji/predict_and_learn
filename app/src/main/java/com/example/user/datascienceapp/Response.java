package com.example.user.datascienceapp;

import java.io.Serializable;

public class Response implements Serializable {
    private String exercise_id;
    private String card_id;
    private String response;
    private String gender;

    public String getExercise() {
        return exercise_id;
    }

    public void setExercise(String exercise_id) {
        this.exercise_id = exercise_id;
    }

    public String getCard() {
        return card_id;
    }

    public void setCard(String card_id) {
        this.card_id = card_id;
    }
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }


}
