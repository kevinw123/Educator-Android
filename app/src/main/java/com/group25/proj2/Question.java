package com.group25.proj2;

/**
 * Created by Gina on 3/23/2017.
 */

public class Question {
    String question;
    String answerA;
    String answerB;
    String answerC;
    String answerD;
    String correctChoice;

    /**
     * Constructor for Question object
     * @param question the question to ask
     * @param answerA
     * @param answerB
     * @param answerC
     * @param answerD
     * @param correctChoice the correct answer
     */
    public Question(String question, String answerA, String answerB, String answerC, String answerD, String correctChoice){
        this.question = question;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.correctChoice = correctChoice;
    }

    public String getCorrectChoice() {
        return correctChoice;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswerA() {
        return answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public String getAnswerC() {
        return answerC;
    }

    public String getAnswerD() {
        return answerD;
    }

}
