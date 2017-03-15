package com.group25.proj2;

import android.content.Intent;
import android.graphics.Point;
import android.media.Image;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class QuestionsActivity extends AppCompatActivity {
    private GestureDetector gestureDetector;
    private String correctChoice;

    private int lives;
    private ImageView livesViews[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        drawQuestion("What is 1 + 1?");
        drawAnswer((TextView) findViewById(R.id.aText), "A", "1");
        drawAnswer((TextView) findViewById(R.id.bText), "B", "2");
        drawAnswer((TextView) findViewById(R.id.cText), "C", "3");
        drawAnswer((TextView) findViewById(R.id.dText), "D", "4");
        setCorrectAnswer("B");

        initLives();

        Button nextButton = (Button) findViewById(R.id.questionsNextButton);
        nextButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(QuestionsActivity.this, LastGameActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });


        gestureDetector = new GestureDetector(this, new SingleTapUp());

        final Button aButton = (Button) findViewById(R.id.aButton);
        aButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return answerButtonEventHandler(aButton, event, BluetoothConstants.ACommand, "A");
            }
        });

        final Button bButton = (Button) findViewById(R.id.bButton);
        bButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return answerButtonEventHandler(bButton, event, BluetoothConstants.BCommand, "B");
            }
        });

        final Button cButton = (Button) findViewById(R.id.cButton);
        cButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return answerButtonEventHandler(cButton, event, BluetoothConstants.CCommand, "C");
            }
        });

        final Button dButton = (Button) findViewById(R.id.dButton);
        dButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return answerButtonEventHandler(dButton, event, BluetoothConstants.DCommand, "D");
            }
        });
    }

    private void drawQuestion(String question){
        TextView questionView = (TextView) findViewById(R.id.questionText);
        questionView.setText(question);
    };

    private void drawAnswer(TextView answerView, String choice, String answer){
        answerView.setText(choice + ". " + answer);
    }

    private void initLives(){
        lives = 3;
        livesViews = new ImageView[3];
        livesViews[0] = (ImageView) findViewById(R.id.heart0);
        livesViews[1] = (ImageView) findViewById(R.id.heart1);
        livesViews[2] = (ImageView) findViewById(R.id.heart2);
        for (int i = 0; i < 3; i++){
            livesViews[i].setImageResource(R.mipmap.hearts);
        }
    }

    private void setCorrectAnswer(String correctChoice){
        this.correctChoice = correctChoice;
    }

    private void right(){
        Intent intent = new Intent(QuestionsActivity.this, StoryActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void wrong(){
        lives--;
        livesViews[lives].setImageResource(R.mipmap.hearts_black);
        if (lives == 0){
            DoneActivity.setWon(false);
            Intent intent = new Intent(QuestionsActivity.this, DoneActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
    }

    private void checkChoice(String choice){
        if (choice.equals(correctChoice)){
            right();
        } else {
            wrong();
        }
    }

    private boolean answerButtonEventHandler(Button button, MotionEvent event, String command, String choice){
        if (gestureDetector.onTouchEvent(event)) {
            BluetoothActivity.sendToDE2(command);
            changeButtonColorOnUp(button);
            checkChoice(choice);
            return true;
        }

        return answerButtonTouchHandler(button, event);

    }

    private boolean answerButtonTouchHandler(Button button, MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            changeButtonColorOnDown(button);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            changeButtonColorOnUp(button);
            return true;
        }

        return false;
    }

    private void changeButtonColorOnDown(Button b){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            b.setBackgroundColor(getResources().getColor(R.color.colorAnswerPress, getTheme()));
        }else {
            b.setBackgroundColor(getResources().getColor(R.color.colorAnswerPress));
        }
    }

    private void changeButtonColorOnUp(Button b){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            b.setBackgroundColor(getResources().getColor(R.color.colorText, getTheme()));
        }else {
            b.setBackgroundColor(getResources().getColor(R.color.colorText));
        }
    }
}
