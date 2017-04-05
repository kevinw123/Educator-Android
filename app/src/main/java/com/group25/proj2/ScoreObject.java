package com.group25.proj2;

/**
 * Created by kevinwong on 2017-04-03.
 */

public class ScoreObject {
    public int score;
    public String date;

    public ScoreObject(){

    }

    /**
     * Constructor to create ScoreObject
     * @param score
     * @param date
     */
    public ScoreObject(int score, String date){
        this.score = score;
        this.date = date;
    }

    /**
     * Get score property
     * @return
     */
    public int getScore() {
        return score;
    }

    /**
     * Set score property
     * @param score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Get date Property
     * @return
     */
    public String getDate() {
        return date;
    }

    /**
     * Set Date property
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }
}