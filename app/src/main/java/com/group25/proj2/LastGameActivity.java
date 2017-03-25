package com.group25.proj2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

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
        tttButton.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){
                popupInstructions(TicTacToeActivity.gameTitle, TicTacToeActivity.gameInstructions, TicTacToeActivity.scoreInstructions, TicTacToeActivity.livesInstructions);
                return true;
            }
        });

        ImageButton clarifaiButton = (ImageButton) findViewById(R.id.clarifaiButton);
        clarifaiButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(LastGameActivity.this, ImaggaActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
        clarifaiButton.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){
                System.out.println("LONG CLICK");
                return true;
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
        fractionsButton.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){
                popupInstructions(FractionsActivity.gameTitle, FractionsActivity.gameInstructions, FractionsActivity.scoreInstructions, FractionsActivity.livesInstructions);
                return true;
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
        flagGameButton.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){
                popupInstructions(MapsActivity.gameTitle, MapsActivity.gameInstructions, MapsActivity.scoreInstructions, MapsActivity.livesInstructions);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    private void popupInstructions(String gameTitle, String gameInstructions, String scoreInstructions, String livesInstructions){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.last_game_popup, null);

        TextView gameTitleView = (TextView) alertLayout.findViewById(R.id.gameTitle);
        TextView gameInstructionsView = (TextView) alertLayout.findViewById(R.id.gameInstructions);
        TextView scoreInstructionsView = (TextView) alertLayout.findViewById(R.id.scoreInstructions);
        TextView livesInstructionsView = (TextView) alertLayout.findViewById(R.id.livesInstructions);

        gameTitleView.setText(gameTitle);
        gameInstructionsView.setText(gameInstructions);
        scoreInstructionsView.setText(scoreInstructions);
        livesInstructionsView.setText(livesInstructions);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("OKAY", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){

            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();

    }
}
