package com.group25.proj2;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import static com.group25.proj2.TicTacToeActivity.won;

public class DoneActivity extends AppCompatActivity {
    public static boolean won;
    private TextView scoreView;
    private TextView highscoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);
        setBackgroundColor();

        scoreView = (TextView) findViewById(R.id.scoreDone);
        highscoreView = (TextView) findViewById(R.id.highscoreDone);
        Score.drawScores(scoreView, highscoreView);

        TextView doneMessage = (TextView) findViewById(R.id.doneMessage);
        if (won){
            // TODO: Send WON flag over Bluetooth
            doneMessage.setText("YOU WIN!");
        } else {
            // TODO: Send LOSE flag over Bluetooth
            doneMessage.setText("YOU LOSE");
        }


        ImageButton replayButton = (ImageButton) findViewById(R.id.replayButton);
        replayButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // BluetoothActivity.sendToDE2(BluetoothConstants.startCommand); // uncomment later

                Score.resetScore();
                Intent intent = new Intent(DoneActivity.this, StoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        ImageButton menuButton = (ImageButton) findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                Score.resetScore();
                Intent intent = new Intent(DoneActivity.this, MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

    private void setBackgroundColor(){
        RelativeLayout thisView = (RelativeLayout) findViewById(R.id.activity_done);
        if (won){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                thisView.setBackgroundColor(getResources().getColor(R.color.colorWin, getTheme()));
            }else {
                thisView.setBackgroundColor(getResources().getColor(R.color.colorWin));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                thisView.setBackgroundColor(getResources().getColor(R.color.colorLose, getTheme()));
            }else {
                thisView.setBackgroundColor(getResources().getColor(R.color.colorLose));
            }
        }
    }

    public static void setWon(boolean gameWon){
        if (gameWon){
            won = true;
        } else {
            won = false;
        }

    }
}
