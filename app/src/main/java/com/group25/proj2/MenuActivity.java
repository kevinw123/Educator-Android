package com.group25.proj2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import static com.group25.proj2.BluetoothConstants.startCommand;

public class MenuActivity extends AppCompatActivity {
    private TextView highscoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initHighscore();

        ImageButton menuPlayButton = (ImageButton) findViewById(R.id.menuPlayButton);
        menuPlayButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // TODO: Send PLAY flag over Bluetooth
                BluetoothActivity.sendToDE2(startCommand);

                Score.resetScore();

                Intent intent = new Intent(MenuActivity.this, StoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    private void initHighscore(){
        SharedPreferences settings = getSharedPreferences(Score.PREF, Context.MODE_PRIVATE);
        Score.highscore = settings.getInt(Score.HIGHSCORE_PREF, 0);

        highscoreView = (TextView) findViewById(R.id.highscoreMenu);
        Score.drawHighscore(highscoreView);
    }


}
