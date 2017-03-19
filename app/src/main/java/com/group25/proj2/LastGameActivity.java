package com.group25.proj2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class LastGameActivity extends AppCompatActivity{
    private TextView scoreView;
    private TextView highscoreView;

    public static final int GAMEOVERDELAY = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_game);

        scoreView = (TextView) findViewById(R.id.scoreLastGame);
        highscoreView = (TextView) findViewById(R.id.highscoreLastGame);
        Score.drawScores(scoreView, highscoreView);

        ImageButton tttButton = (ImageButton) findViewById(R.id.tttButton);
        tttButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                Intent intent = new Intent(LastGameActivity.this, TicTacToePromptActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        ImageButton clarifaiButton = (ImageButton) findViewById(R.id.clarifaiButton);
        clarifaiButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                // TODO: Implement Clarifai game

                // For testing
                Intent intent = new Intent(LastGameActivity.this, DoneActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        ImageButton fractionsButton = (ImageButton) findViewById(R.id.fractionsButton);
        fractionsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                Intent intent = new Intent(LastGameActivity.this, FractionsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        ImageButton flagGameButton = (ImageButton) findViewById(R.id.flagsButton);
        flagGameButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){

                Intent intent = new Intent(LastGameActivity.this, MapsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}
