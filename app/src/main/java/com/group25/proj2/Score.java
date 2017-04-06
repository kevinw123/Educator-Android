package com.group25.proj2;

import android.widget.TextView;

/**
 * Created by Gina on 3/18/2017.
 */

public class Score {
    public static int score;
    public static int highScore;

    public static final String PREF = "SHARED_PREF";
    public static final String HIGHSCORE_PREF = "HIGHSCORE";

    public static void resetScore(){
        score = 0;
    }

    public static void drawScore(TextView scoreView){
        scoreView.setText("SCORE: " + Integer.toString(score));
    }

    public static void drawHighscore(TextView highscoreView){
        highscoreView.setText("HIGHSCORE: " + Integer.toString(highScore));
    }

    public static void drawScores(TextView scoreView, TextView highscoreView){
        drawScore(scoreView);
        drawHighscore(highscoreView);
    }

    public static void addScore(int scoreToAdd){
        score += scoreToAdd;
    }

    public static void updateScore(int scoreToAdd, TextView scoreView, TextView highscoreView){
        addScore(scoreToAdd);
        if (score > highScore){
            highScore = score;
        }
        drawScores(scoreView, highscoreView);
    }
}
