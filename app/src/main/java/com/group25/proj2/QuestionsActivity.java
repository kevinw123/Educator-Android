package com.group25.proj2;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class QuestionsActivity extends AppCompatActivity {
    private static Question[] questions;
    public static int NUM_QUESTIONS = 4;
    private int question_index;
    private Question curQuestion;

    private GestureDetector gestureDetector;
    private String correctChoice;

    private int score;
    private TextView scoreView;
    private TextView highscoreView;

    private int lives;
    private ImageView livesViews[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Intent intent = getIntent();
        // TODO: change back later
        question_index = intent.getIntExtra("QUESTION_INDEX", 0);
        curQuestion = questions[question_index];

        initLives();

        score = 3;
        scoreView = (TextView) findViewById(R.id.scoreQuestions);
        highscoreView = (TextView) findViewById(R.id.highscoreQuestions);
        Score.drawScores(scoreView, highscoreView);

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

        drawQuestionArea();
    }

    @Override
    public void onBackPressed() {
    }

    private void drawQuestion(String question){
        TextView questionView = (TextView) findViewById(R.id.questionText);
        questionView.setText(question);
    };

    private void drawAnswer(TextView answerView, String choice, String answer){
        answerView.setText(choice + ". " + answer);
    }

    private void drawQuestionArea(){

        drawQuestion(curQuestion.getQuestion());
        drawAnswer((TextView) findViewById(R.id.aText), "A", curQuestion.getAnswerA());
        drawAnswer((TextView) findViewById(R.id.bText), "B", curQuestion.getAnswerB());
        drawAnswer((TextView) findViewById(R.id.cText), "C", curQuestion.getAnswerC());
        drawAnswer((TextView) findViewById(R.id.dText), "D", curQuestion.getAnswerD());
        setCorrectAnswer(curQuestion.getCorrectChoice());
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

    private void playSound(boolean right){
        if (Audio.playSoundFX){
            if (right){
                Audio.soundPool.play(Audio.rightAnswerSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);
            } else {
                Audio.soundPool.play(Audio.wrongAnswerSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);
            }
        }
    }
    private void right(){
        playSound(true);

        Score.updateScore(score, scoreView, highscoreView);

        Intent intent = new Intent(QuestionsActivity.this, MovementActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void wrong(){
        playSound(false);
        lives--;
        score--;
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

    public static void initQuestions(){
        questions = new Question[NUM_QUESTIONS];

        questions[0] = new Question("What is 1+1?", "1", "2", "3", "4", "B");
        questions[1] = new Question("What is the capital city of Canada?", "Vancouver, BC", "Edmonton, AB", "Toronto, ON", "Ottawa, ON", "D");
        questions[2] = new Question("Which object is the largest?", "Elephant", "Peanut", "Moon", "Eiffel Tower", "C");
        questions[3] = new Question("Where can you find polar bears?", "Antarctica", "Arctic", "Iceland", "Greenland", "B");
    }
}
