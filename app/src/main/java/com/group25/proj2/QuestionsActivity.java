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

    /* Question variables */
    private static Question[] questions; // Defined array of Question objects
    public static int NUM_QUESTIONS = 4;
    private int question_index; // Index of question to display
    private Question curQuestion; // Question object to display
    private String correctChoice;

    private int questionScore; // Score to receive for this question (maximum is 3, lose 1 for each life lost)

    /* Views to display questionScore and high questionScore */
    private TextView scoreView;
    private TextView highscoreView;

    /* Lives left (maximum is 3) */
    private int lives;
    private ImageView livesViews[]; // Array of images to represent lives left

    private GestureDetector gestureDetector; // Used to detect button click - onTouch and onClick interfere with each other

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        /* Set the question to display based on the passed in index */
        Intent intent = getIntent();
        question_index = intent.getIntExtra("QUESTION_INDEX", 0);
        curQuestion = questions[question_index];

        /* Initialize lives and score to receive for this question */
        initLives();
        questionScore = 3;

        /* Draw score and high score to screen */
        scoreView = (TextView) findViewById(R.id.scoreQuestions);
        highscoreView = (TextView) findViewById(R.id.highscoreQuestions);
        Score.drawScores(scoreView, highscoreView);

        gestureDetector = new GestureDetector(this, new SingleTapUp()); // Used to detect button click - onClick and onTouch interfere with each other

        final Button aButton = (Button) findViewById(R.id.aButton);
        aButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return answerButtonEventHandler(aButton, event, "A");
            }
        });

        final Button bButton = (Button) findViewById(R.id.bButton);
        bButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return answerButtonEventHandler(bButton, event, "B");
            }
        });

        final Button cButton = (Button) findViewById(R.id.cButton);
        cButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return answerButtonEventHandler(cButton, event, "C");
            }
        });

        final Button dButton = (Button) findViewById(R.id.dButton);
        dButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return answerButtonEventHandler(dButton, event, "D");
            }
        });

        drawQuestionArea();
    }

    /**
     * Disable device back button
     */
    @Override
    public void onBackPressed() {
    }

    /**
     * Draw question to screen
     * @param question is the question text to display
     */
    private void drawQuestion(String question) {
        TextView questionView = (TextView) findViewById(R.id.questionText);
        questionView.setText(question);
    }

    /**
     * Draw the answers to screen
     * @param answerView is the view that displays an answer
     * @param choice indicates whether answer is associated with A, B, C, D
     * @param answer is the answer text to display
     */
    private void drawAnswer(TextView answerView, String choice, String answer) {
        answerView.setText(choice + ". " + answer);
    }

    /**
     * Draws the question and multiple choice answers to screen
     * Sets correct answer
     */
    private void drawQuestionArea() {
        drawQuestion(curQuestion.getQuestion());
        drawAnswer((TextView) findViewById(R.id.aText), "A", curQuestion.getAnswerA());
        drawAnswer((TextView) findViewById(R.id.bText), "B", curQuestion.getAnswerB());
        drawAnswer((TextView) findViewById(R.id.cText), "C", curQuestion.getAnswerC());
        drawAnswer((TextView) findViewById(R.id.dText), "D", curQuestion.getAnswerD());
        setCorrectAnswer(curQuestion.getCorrectChoice());
    }

    /**
     * Initialize lives left to 0
     * Initialize views and display image for each life
     */
    private void initLives() {
        lives = 3;
        livesViews = new ImageView[3];
        livesViews[0] = (ImageView) findViewById(R.id.heart0);
        livesViews[1] = (ImageView) findViewById(R.id.heart1);
        livesViews[2] = (ImageView) findViewById(R.id.heart2);
        for (int i = 0; i < 3; i++) {
            livesViews[i].setImageResource(R.mipmap.hearts);
        }
    }

    private void setCorrectAnswer(String correctChoice) {
        this.correctChoice = correctChoice;
    }

    /**
     * Plays sound based on whether answer was correct or incorrect
     * @param right is true if answer was correct
     */
    private void playSound(boolean right) {
        if (right) {
            Audio.soundPool.play(Audio.rightAnswerSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);
        } else {
            Audio.soundPool.play(Audio.wrongAnswerSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);
        }
    }

    /**
     * Update the score and switch back to MovementActivity
     */
    private void right() {
        playSound(true);

        Score.updateScore(questionScore, scoreView, highscoreView);

        Intent intent = new Intent(QuestionsActivity.this, MovementActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    /**
     * Decrement the lives and question score, change the life image accordingly
     * If no more lives left, switch to loss screen
     */
    private void wrong() {
        playSound(false);
        lives--;
        questionScore--;
        livesViews[lives].setImageResource(R.mipmap.hearts_black);
        if (lives == 0) {
            DoneActivity.setWon(false);
            Intent intent = new Intent(QuestionsActivity.this, DoneActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
    }

    /**
     * Check if answer was correct or incorrect
     * If correct, switch to MovementActivity
     * Otherwise, decrement lives, question score, and check if we should switch to loss screen
     * @param choice
     */
    private void checkChoice(String choice) {
        if (choice.equals(correctChoice)) {
            right();
        } else {
            wrong();
        }
    }

    /**
     * Checks if an answer button was clicked
     * If clicked, check if it was the correct answer
     * @param button the button to check for click
     * @param event
     * @param choice is the choice associated with the button
     */
    private boolean answerButtonEventHandler(Button button, MotionEvent event, String choice) {
        if (gestureDetector.onTouchEvent(event)) {
            changeButtonColorOnUp(button);
            checkChoice(choice);
            return true;
        }

        return answerButtonTouchHandler(button, event);

    }

    /**
     * Checks if a button was touched
     * Change color accordingly on touch and release
     */
    private boolean answerButtonTouchHandler(Button button, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            changeButtonColorOnDown(button);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            changeButtonColorOnUp(button);
            return true;
        }

        return false;
    }

    private void changeButtonColorOnDown(Button b) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            b.setBackgroundColor(getResources().getColor(R.color.colorAnswerPress, getTheme()));
        } else {
            b.setBackgroundColor(getResources().getColor(R.color.colorAnswerPress));
        }
    }

    private void changeButtonColorOnUp(Button b) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            b.setBackgroundColor(getResources().getColor(R.color.colorText, getTheme()));
        } else {
            b.setBackgroundColor(getResources().getColor(R.color.colorText));
        }
    }

    public static void initQuestions() {
        questions = new Question[NUM_QUESTIONS];

        questions[0] = new Question("What is 1+1?", "1", "2", "3", "4", "B");
        questions[1] = new Question("What is the capital city of Canada?", "Vancouver, BC", "Edmonton, AB", "Toronto, ON", "Ottawa, ON", "D");
        questions[2] = new Question("Which object is the largest?", "Elephant", "Peanut", "Moon", "Eiffel Tower", "C");
        questions[3] = new Question("Where can you find polar bears?", "Antarctica", "Arctic", "Iceland", "Greenland", "B");
    }
}
