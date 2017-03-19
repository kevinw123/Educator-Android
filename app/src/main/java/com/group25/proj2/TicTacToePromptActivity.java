package com.group25.proj2;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import static com.group25.proj2.TicTacToeActivity.setPlayerPiece;

public class TicTacToePromptActivity extends AppCompatActivity {
    private TextView scoreView;
    private TextView highscoreView;

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe_prompt);

        scoreView = (TextView) findViewById(R.id.scoreTTTPrompt);
        highscoreView = (TextView) findViewById(R.id.highscoreTTTPrompt);
        Score.drawScores(scoreView, highscoreView);

        gestureDetector = new GestureDetector(this, new SingleTapUp());

        final Button xButton = (Button) findViewById(R.id.tttX);
        xButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean toHandle = xoButtonHandler(xButton, event);
                return toHandle;
            }
        });

        final Button oButton = (Button) findViewById(R.id.tttO);
        oButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean toHandle = xoButtonHandler(oButton, event);
                return toHandle;
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    protected void launchTicTacToeGame(){
        Intent intent = new Intent(this, TicTacToeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private boolean xoButtonHandler(Button button, MotionEvent event){
        if (gestureDetector.onTouchEvent(event)) {
            changeButtonColorOnUp(button);

            setPlayerPiece(button.getText().toString());

            launchTicTacToeGame();

            return true;
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
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
            b.setBackgroundColor(getResources().getColor(R.color.colorBlack, getTheme()));
        }else {
            b.setBackgroundColor(getResources().getColor(R.color.colorBlack));
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
