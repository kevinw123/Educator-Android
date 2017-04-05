package com.group25.proj2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DoneActivity extends AppCompatActivity {
    public static boolean won;
    private TextView scoreView;
    private TextView highscoreView;
    private TextView confirmationTextView;
    private Button resetHighscoreButton;
    private Button viewScoreButton;

    String androidId;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRootReference = firebaseDatabase.getReference();
    public static DatabaseReference mChildReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        saveHighscore();

        scoreView = (TextView) findViewById(R.id.scoreDone);
        highscoreView = (TextView) findViewById(R.id.highscoreDone);
        Score.drawScores(scoreView, highscoreView);

        TextView doneMessage = (TextView) findViewById(R.id.doneMessage);
        if (won){
            Audio.soundPool.play(Audio.winSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);
            doneMessage.setText("YOU WIN!");

            BluetoothActivity.sendToDE2(BluetoothConstants.winGameCommand);
        } else {
            Audio.soundPool.play(Audio.loseSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);
            doneMessage.setText("YOU LOSE");
        }


        ImageButton replayButton = (ImageButton) findViewById(R.id.replayButton);
        replayButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Audio.soundPool.play(Audio.pressSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);

                BluetoothActivity.sendToDE2(BluetoothConstants.startCommand);

                Score.resetScore();
                Intent intent = new Intent(DoneActivity.this, StoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        ImageButton menuButton = (ImageButton) findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Audio.soundPool.play(Audio.pressSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);

                Score.resetScore();
                Intent intent = new Intent(DoneActivity.this, MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        resetHighscoreButton = (Button) findViewById(R.id.resetHighscoreButton);
        resetHighscoreButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                popupConfirmation();
            }
        });

        viewScoreButton = (Button) findViewById(R.id.viewScoresButton);
        viewScoreButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(DoneActivity.this, ScoreList.class);
                startActivity(intent);
            }
        });
        setColors();

        androidId =  Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        mChildReference = mRootReference.child(androidId);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        ScoreObject object = new ScoreObject(Score.score, formattedDate);
        mChildReference.push().setValue(object);
    }

    @Override
    public void onBackPressed() {
    }

    private void setColors(){
        RelativeLayout thisView = (RelativeLayout) findViewById(R.id.activity_done);
        if (won){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                thisView.setBackgroundColor(getResources().getColor(R.color.colorWin, getTheme()));
                resetHighscoreButton.setTextColor(getResources().getColor(R.color.colorWin, getTheme()));
                viewScoreButton.setTextColor(getResources().getColor(R.color.colorWin, getTheme()));
            }else {
                thisView.setBackgroundColor(getResources().getColor(R.color.colorWin));
                resetHighscoreButton.setTextColor(getResources().getColor(R.color.colorWin));
                viewScoreButton.setTextColor(getResources().getColor(R.color.colorWin));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                thisView.setBackgroundColor(getResources().getColor(R.color.colorLose, getTheme()));
                resetHighscoreButton.setTextColor(getResources().getColor(R.color.colorLose, getTheme()));
                viewScoreButton.setTextColor(getResources().getColor(R.color.colorLose, getTheme()));
            }else {
                thisView.setBackgroundColor(getResources().getColor(R.color.colorLose));
                resetHighscoreButton.setTextColor(getResources().getColor(R.color.colorLose));
                viewScoreButton.setTextColor(getResources().getColor(R.color.colorLose));
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

    private void saveHighscore(){
        SharedPreferences settings = getSharedPreferences(Score.PREF, Context.MODE_PRIVATE);
        int savedHighscore = settings.getInt(Score.HIGHSCORE_PREF, 0);

        if (Score.highscore > savedHighscore){
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(Score.HIGHSCORE_PREF, Score.highscore);
            editor.commit();
        }
    }

    private void resetSavedHighscore(){
        SharedPreferences settings = getSharedPreferences(Score.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Score.HIGHSCORE_PREF, Score.highscore);
        editor.commit();
        mChildReference.removeValue();
    }

    private void resetHighscoreView(){
        Score.highscore = 0;
        Score.drawHighscore(highscoreView);
    }

    private void popupConfirmation(){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.confirmation_popup, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                resetHighscoreView();
                resetSavedHighscore();
            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){

            }
        });

        final AlertDialog dialog = alert.create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (won){
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorWin, getTheme()));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorWin, getTheme()));
                    } else {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorLose, getTheme()));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorLose, getTheme()));
                    }
                }else {
                    if (won) {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.colorWin));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorWin));
                    } else {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.colorLose));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorLose));
                    }
                }
            }
        });

        dialog.show();

        confirmationTextView = (TextView) alertLayout.findViewById(R.id.confirmationTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (won){
                confirmationTextView.setTextColor(getResources().getColor(R.color.colorWin, getTheme()));
            } else {
                confirmationTextView.setTextColor(getResources().getColor(R.color.colorLose, getTheme()));
            }
        }else {
            if (won) {
                confirmationTextView.setTextColor(getResources().getColor(R.color.colorWin));
            } else {
                confirmationTextView.setTextColor(getResources().getColor(R.color.colorLose));
            }
        }

    }
}

