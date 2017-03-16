package com.group25.proj2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import static com.group25.proj2.TicTacToeActivity.won;

public class DoneActivity extends AppCompatActivity {
    public static boolean won;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

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
                BluetoothActivity.sendToDE2(BluetoothConstants.startCommand);
                Intent intent = new Intent(DoneActivity.this, StoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        ImageButton menuButton = (ImageButton) findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                Intent intent = new Intent(DoneActivity.this, MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

    public static void setWon(boolean gameWon){
        if (gameWon){
            won = true;
        } else {
            won = false;
        }

    }
}
