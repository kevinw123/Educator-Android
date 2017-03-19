package com.group25.proj2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class StoryActivity extends AppCompatActivity {
    private TextView scoreView;
    private TextView highscoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        scoreView = (TextView) findViewById(R.id.scoreStory);
        highscoreView = (TextView) findViewById(R.id.highscoreStory);
        Score.drawScores(scoreView, highscoreView);

        ImageButton storyScrollButton = (ImageButton) findViewById(R.id.storyScrollButton);
        storyScrollButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // TODO: Send SCROLL flag over Bluetooth

                // For testing
                Intent intent = new Intent(StoryActivity.this, MovementActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}
