package com.group25.proj2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class LastGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_game);

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
    }
}
