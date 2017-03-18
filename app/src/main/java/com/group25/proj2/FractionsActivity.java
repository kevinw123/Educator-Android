package com.group25.proj2;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.group25.proj2.DoneActivity.setWon;

public class FractionsActivity extends AppCompatActivity {
    private ImageButton swipeButton;

    private TextView numeratorLeftView;
    private TextView numeratorRightView;
    private TextView denominatorLeftView;
    private TextView denominatorRightView;

    private double numeratorLeft;
    private double numeratorRight;
    private double denominatorLeft;
    private double denominatorRight;

    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int EITHER = 2;
    private int correctDirection;

    private float x1, x2;
    private static final int MIN_DISTANCE = 150;

    private int roundsLeft;

    private TextView timerView;
    private CountDownTimer roundTimer;
    private static final int ROUNDTIME = 10000;
    private static final int ROUNDTIMELEFT_INIT = 10;
    private int roundTimeLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fractions);

        numeratorLeftView = (TextView) findViewById(R.id.numeratorLeft);
        numeratorRightView = (TextView) findViewById(R.id.numeratorRight);
        denominatorLeftView = (TextView) findViewById(R.id.demoninatorLeft);
        denominatorRightView = (TextView) findViewById(R.id.denominatorRight);

        timerView = (TextView) findViewById(R.id.timeLeft);

        swipeButton = (ImageButton) findViewById(R.id.swipeButton);
        swipeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        return true;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        float distance = x2 - x1;
                        if (Math.abs(distance) > MIN_DISTANCE){
                            if (distance < 0){
                                checkDirection(LEFT);
                            } else {
                                checkDirection(RIGHT);
                            }
                            return true;
                        } else {
                            return false;
                        }
                }
                return false;
            }
        });

        initGame();
    }

    private void launchGameOverScreen(){
        Intent intent = new Intent(this, DoneActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void win(){
        roundsLeft--;
        if (roundsLeft > 0) {
            Toast.makeText(getApplicationContext(), "Correct. " + roundsLeft + " more round(s)!", Toast.LENGTH_LONG).show();
            startGame();
        } else {
            setWon(true);
            launchGameOverScreen();
        }
    }

    private void lose(String loseMessage){
        Toast.makeText(getApplicationContext(), loseMessage, Toast.LENGTH_LONG).show();
        setWon(false);
        launchGameOverScreen();
    }

    private void checkDirection(int direction){
        if (direction == correctDirection){
            win();
        } else {
            lose("Incorrect. You lose!");
        }
    }

    private int getRandomNum(){
        Random rand = new Random();
        return rand.nextInt(9) + 1;
    }

    private void setFractions(){
        numeratorLeft = getRandomNum();
        numeratorRight = getRandomNum();
        denominatorLeft = getRandomNum();
        denominatorRight = getRandomNum();
    }

    private void drawFractions(){
        setFractions();
        numeratorLeftView.setText(Integer.toString((int)numeratorLeft));
        numeratorRightView.setText(Integer.toString((int)numeratorRight));
        denominatorLeftView.setText(Integer.toString((int)denominatorLeft));
        denominatorRightView.setText(Integer.toString((int)denominatorRight));
    }

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
        System.out.println(correctDirection);
    }

    private void updateTime(){
        roundTimeLeft--;
        timerView.setText(Integer.toString(roundTimeLeft) + " SECONDS LEFT");
    }

    private void initTimer(){
        roundTimer  = new CountDownTimer(ROUNDTIME, 1000) {

            public void onTick(long millisUntilFinished) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateTime();
                    }
                });
            }

            public void onFinish() {
                lose("Time out. You lose!");
            }

        };
    }

    private void startTimer(){
        roundTimeLeft = ROUNDTIMELEFT_INIT;
        timerView.setText(Integer.toString(roundTimeLeft) + " SECONDS LEFT");

        roundTimer.cancel();
        roundTimer.start();
    }

    private void startGame(){
        drawFractions();
        setCorrectDirection();
        startTimer();
    }

    private void initGame(){
        roundsLeft = 3;
        initTimer();
        startGame();
    }
}
