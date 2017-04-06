package com.group25.proj2;

import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.group25.proj2.DoneActivity.setWon;

public class FractionsActivity extends AppCompatActivity {

    /* Instructions pop-up text (to be displayed in LastGameActivity) */
    public static final String gameTitle = "FRACTIONS";
    public static final String gameInstructions = "Compare the left fraction and the right fraction. Swipe towards the larger fraction!";
    public static final String scoreInstructions = "For each correct swipe, you get 1 point.";
    public static final String livesInstructions = "You must swipe correctly 3 times IN A ROW to roundWin the game!";

    /* Views that display score and high score */
    private TextView scoreView;
    private TextView highscoreView;

    /* Views to display left fraction */
    private RelativeLayout fractionLeftView;
    private TextView numeratorLeftView;
    private TextView denominatorLeftView;

    /* Views to display right fraction */
    private RelativeLayout fractionRightView;
    private TextView numeratorRightView;
    private TextView denominatorRightView;

    /* Numerator and denominator values */
    private double numeratorLeft;
    private double numeratorRight;
    private double denominatorLeft;
    private double denominatorRight;

    /* Variables for swiping */
    private ImageButton swipeButton;
    private static boolean detectSwipe; // Indicates whether the swipe should be detected

    /* Integers to represent the swipe direction */
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int EITHER = 2;
    private int correctDirection;

    /* Variables used to detect the swipe direction */
    private float initialX, finalX;
    private static final int MIN_DISTANCE = 150;

    /* Variables used to implement each round's time limit */
    private TextView timerView; // View that counts down the seconds left per round
    private CountDownTimer roundTimer;
    private static final int ROUNDTIME = 10000; // Length of each round
    private static final int ROUNDTIMELEFT_INIT = 10; // Initial value for time left in seconds
    private int roundTimeLeft;

    private int roundsLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fractions);

        /* Draw score and high score */
        scoreView = (TextView) findViewById(R.id.scoreFractions);
        highscoreView = (TextView) findViewById(R.id.highscoreFractions);
        Score.drawScores(scoreView, highscoreView);

        /* Initialize views to display left fraction */
        fractionLeftView = (RelativeLayout) findViewById(R.id.leftFraction);
        numeratorLeftView = (TextView) findViewById(R.id.numeratorLeft);
        denominatorLeftView = (TextView) findViewById(R.id.demoninatorLeft);

        /* Initialize views to display right fraction */
        fractionRightView = (RelativeLayout) findViewById(R.id.rightFraction);
        numeratorRightView = (TextView) findViewById(R.id.numeratorRight);
        denominatorRightView = (TextView) findViewById(R.id.denominatorRight);

        timerView = (TextView) findViewById(R.id.timeLeft);

        /*
         * If swipe detection enabled, check distance between ACTION_UP and ACTION_DOWN to detect swipe
         * If swiped, check if direction is correct
         */
        swipeButton = (ImageButton) findViewById(R.id.swipeButton);
        swipeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (detectSwipe) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = event.getX();
                            return true;
                        case MotionEvent.ACTION_UP:
                            finalX = event.getX();
                            float distance = finalX - initialX; // Distance between touch and release
                            if (Math.abs(distance) > MIN_DISTANCE) {
                                disableSwipe();
                                if (distance < 0) {
                                    checkDirection(LEFT);
                                } else {
                                    checkDirection(RIGHT);
                                }
                                return true;
                            } else {
                                return false;
                            }
                    }
                }
                return false;
            }
        });

        initGame();
    }

    /**
     * Disable swipe detection
     */
    private void disableSwipe(){
        detectSwipe = false;
    }

    /**
     * Enable swipe detection
     */
    private void enableSwipe(){
        detectSwipe = true;
    }

    /**
     * Disable the device back button
     */
    @Override
    public void onBackPressed() {
    }

    /**
     * Launch the game over screen
     */
    private void launchGameOverScreen(){
        Intent intent = new Intent(this, DoneActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    /**
     * On correct answer, highlight the correct fraction
     * @param direction is the correct fraction's direction
     */
    private void highlightWin(int direction){
        if (direction == LEFT) {
            /* Use appropriate getColor() function based on the Android build version */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fractionLeftView.setBackgroundColor(getResources().getColor(R.color.colorHighlightWin, getTheme()));
            } else {
                /* getResources().getColor(int id) is deprecated */
                fractionLeftView.setBackgroundColor(getResources().getColor(R.color.colorHighlightWin));
            }
        } else {
            /* Use appropriate getColor() function based on the Android build version */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fractionRightView.setBackgroundColor(getResources().getColor(R.color.colorHighlightWin, getTheme()));
            } else {
                /* getResources().getColor(int id) is deprecated */
                fractionRightView.setBackgroundColor(getResources().getColor(R.color.colorHighlightWin));
            }
        }
    }

    /**
     * If no rounds left, launch win screen
     * Otherwise, launch new round
     */
    private void roundWinTimerCallback(){
        if (roundsLeft > 0) {
            startGame();
        } else {
            setWon(true);
            launchGameOverScreen();
        }
    }

    /**
     * Highlights the correct fraction, cancels round timer, and updates score
     * If no rounds left, launch win screen
     * Otherwise, launch new round
     * @param direction is the correct fraction's direction
     */
    private void roundWin(int direction){
        roundTimer.cancel();

        Audio.soundPool.play(Audio.rightAnswerSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);

        /* Decrement rounds, and display Toast indicating the number of rounds left */
        roundsLeft--;
        if (roundsLeft > 0){
            Toast.makeText(getApplicationContext(), "Correct! Rounds left: " + roundsLeft, Toast.LENGTH_LONG).show();
        }

        /* Highlight the correct fraction, update score, and launch new round or game over screen after a delay */
        highlightWin(direction);
        Score.updateScore(1, scoreView, highscoreView);
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roundWinTimerCallback();
                    }
                });
            }
        }, LastGameActivity.GAMEOVERDELAY);
    }

    /**
     * Cancels round timer and launches loss screen
     * If no rounds left, launch game over screen
     * Otherwise, launch new round
     * @param loseMessage is the Toast text to display
     */
    private void lose(String loseMessage){
        roundTimer.cancel();

        Audio.soundPool.play(Audio.wrongAnswerSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);

        Toast.makeText(getApplicationContext(), loseMessage, Toast.LENGTH_LONG).show();

        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setWon(false);
                        launchGameOverScreen();
                    }
                });
            }
        }, LastGameActivity.GAMEOVERDELAY);
    }

    /**
     * Check if swiped direction is correct
     * @param direction is the swipe direction
     */
    private void checkDirection(int direction){
        if (correctDirection == EITHER){
            roundWin(direction);
        } else if (direction == correctDirection){
            roundWin(direction);
        } else {
            lose("Incorrect. You lose!");
        }
    }

    /**
     * Return a random integer between 1 and 9
     * @return the random int
     */
    private int getRandomNum(){
        Random rand = new Random();
        return rand.nextInt(9) + 1;
    }

    /**
     * Sets the fraction views to random fractions
     */
    private void setFractions(){
        numeratorLeft = getRandomNum();
        numeratorRight = getRandomNum();
        denominatorLeft = getRandomNum();
        denominatorRight = getRandomNum();
    }

    /**
     * Initializes the fractions' background colors
     */
    private void initFractionBackgrounds(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fractionLeftView.setBackgroundColor(getResources().getColor(R.color.colorLastGame, getTheme()));
        }else {
            fractionLeftView.setBackgroundColor(getResources().getColor(R.color.colorLastGame));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fractionRightView.setBackgroundColor(getResources().getColor(R.color.colorLastGame, getTheme()));
        }else {
            fractionRightView.setBackgroundColor(getResources().getColor(R.color.colorLastGame));
        }
    }

    /**
     * Draws the fraction views with random fractions between 0 and 9
     */
    private void drawFractions(){
        initFractionBackgrounds();

        setFractions();
        numeratorLeftView.setText(Integer.toString((int)numeratorLeft));
        numeratorRightView.setText(Integer.toString((int)numeratorRight));
        denominatorLeftView.setText(Integer.toString((int)denominatorLeft));
        denominatorRightView.setText(Integer.toString((int)denominatorRight));
    }

    /**
     * Sets the correct direction of the round
     */
    private void setCorrectDirection(){
        double leftFraction = numeratorLeft / denominatorLeft;
        double rightFraction = numeratorRight / denominatorRight;

        if (leftFraction > rightFraction){
            correctDirection = LEFT;
        } else if (rightFraction > leftFraction){
            correctDirection = RIGHT;
        } else {
            correctDirection = EITHER;
        }
    }

    /**
     * Update the timer view to display the seconds left
     */
    private void updateTimerView(){
        roundTimeLeft--;
        timerView.setText(Integer.toString(roundTimeLeft) + " SECONDS LEFT");
    }

    /**
     * Initialize each round's countdown timer
     * If timer is done, lose the game
     */
    private void initTimer(){
        /* Count down each second for a total of 10 seconds */
        roundTimer  = new CountDownTimer(ROUNDTIME, 1000) {

            /* Update countdown view per second */
            public void onTick(long millisUntilFinished) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateTimerView();
                    }
                });
            }

            /* Lose game on time out */
            public void onFinish() {
                lose("Time out. You lose!");
            }

        };
    }

    /**
     * Start each round's countdown timer
     */
    private void startTimer(){
        roundTimeLeft = ROUNDTIMELEFT_INIT;
        timerView.setText(Integer.toString(roundTimeLeft) + " SECONDS LEFT");

        roundTimer.cancel();
        roundTimer.start();
    }

    /**
     * Start a round
     */
    private void startGame(){
        drawFractions();
        setCorrectDirection();
        startTimer();
        enableSwipe();
    }

    /**
     * Initialize the game
     */
    private void initGame(){
        roundsLeft = 3;
        initTimer();
        startGame();
    }
}
