package com.group25.proj2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {
    private TextView highscoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        highscoreView = (TextView) findViewById(R.id.highscoreMenu);
        Score.drawHighscore(highscoreView);

        ImageButton menuPlayButton = (ImageButton) findViewById(R.id.menuPlayButton);
        menuPlayButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // TODO: Send PLAY flag over Bluetooth
                //BluetoothActivity.sendToDE2(startCommand); // uncomment later

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


}
