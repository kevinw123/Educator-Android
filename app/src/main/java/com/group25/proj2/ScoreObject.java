package com.group25.proj2;

/**
 * Created by kevinwong on 2017-04-03.
 */

public class ScoreObject {
    public int score;
    public String date;

    public ScoreObject(){

    }

    public ScoreObject(int score, String date){
        this.score = score;
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}