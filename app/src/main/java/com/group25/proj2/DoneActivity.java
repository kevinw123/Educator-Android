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
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DoneActivity extends AppCompatActivity {
    public static boolean won; // Indicates whether entire game was won

    /* Views that display score and high score */
    private TextView scoreView;
    private TextView highscoreView;

    /* Views to view and reset scores */
    private TextView confirmResetScoresView;
    private Button resetScoresButton;
    private Button viewScoresButton;

    /* Variables for scores in the database */
    // TODO: comment database variables
    String androidId; // Unique Android device ID
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRootReference = firebaseDatabase.getReference();
    public static DatabaseReference mChildReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        updateHighScore();

        /* Draw score and high score */
        scoreView = (TextView) findViewById(R.id.scoreDone);
        highscoreView = (TextView) findViewById(R.id.highscoreDone);
        Score.drawScores(scoreView, highscoreView);

        /* Draw "YOU WIN" or "YOU LOSE", play corresponding sound effect */
        TextView doneMessage = (TextView) findViewById(R.id.doneMessage);
        if (won){
            Audio.soundPool.play(Audio.winSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);
            doneMessage.setText("YOU WIN!");

            BluetoothActivity.sendToDE2(BluetoothConstants.winGameCommand);
        } else {
            Audio.soundPool.play(Audio.loseSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);
            doneMessage.setText("YOU LOSE");
        }

        /* On replay button press, signal to DE2, reset score, and switch to StoryActivity */
        ImageButton replayButton = (ImageButton) findViewById(R.id.replayButton);
        replayButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Audio.soundPool.play(Audio.buttonPressSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);

                BluetoothActivity.sendToDE2(BluetoothConstants.playCommand);

                Score.resetScore();
                Intent intent = new Intent(DoneActivity.this, StoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        /* On menu button press, signal to DE2, reset score, and switch to MenuActivity */
        ImageButton menuButton = (ImageButton) findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Audio.soundPool.play(Audio.buttonPressSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);

                Score.resetScore();
                Intent intent = new Intent(DoneActivity.this, MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        /* On reset scores button press, pop up confirmation to delete all scores in the database */
        resetScoresButton = (Button) findViewById(R.id.resetScoresButton);
        resetScoresButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                popupResetScoresConfirmation();
            }
        });

        /* On view scores button press, switch to ScoreListActivity */
        viewScoresButton = (Button) findViewById(R.id.viewScoresButton);
        viewScoresButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(DoneActivity.this, ScoreList.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        setColors();

        // TODO: add comments for database
        androidId =  Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        mChildReference = mRootReference.child(androidId);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        ScoreObject object = new ScoreObject(Score.score, formattedDate);
        mChildReference.push().setValue(object);
    }

    /*
     * Disable the device back button
     */
    @Override
    public void onBackPressed() {
    }

    /*
     * Sets screen color scheme (background and text colors) based on win or loss
     */
    private void setColors(){
        RelativeLayout thisView = (RelativeLayout) findViewById(R.id.activity_done);
        if (won){
            /* Use appropriate getColor() function based on the Android build version */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                thisView.setBackgroundColor(getResources().getColor(R.color.colorWin, getTheme()));
                resetScoresButton.setTextColor(getResources().getColor(R.color.colorWin, getTheme()));
                viewScoresButton.setTextColor(getResources().getColor(R.color.colorWin, getTheme()));
            }else {
                /* getResources().getColor(int id) is deprecated */
                thisView.setBackgroundColor(getResources().getColor(R.color.colorWin));
                resetScoresButton.setTextColor(getResources().getColor(R.color.colorWin));
                viewScoresButton.setTextColor(getResources().getColor(R.color.colorWin));
            }

        } else {
            /* Use appropriate getColor() function based on the Android build version */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                thisView.setBackgroundColor(getResources().getColor(R.color.colorLose, getTheme()));
                resetScoresButton.setTextColor(getResources().getColor(R.color.colorLose, getTheme()));
                viewScoresButton.setTextColor(getResources().getColor(R.color.colorLose, getTheme()));
            }else {
                /* getResources().getColor(int id) is deprecated */
                thisView.setBackgroundColor(getResources().getColor(R.color.colorLose));
                resetScoresButton.setTextColor(getResources().getColor(R.color.colorLose));
                viewScoresButton.setTextColor(getResources().getColor(R.color.colorLose));
            }
        }
    }

    /*
     * Indicate win or loss using gameWon
     */
    public static void setWon(boolean gameWon){
        if (gameWon){
            won = true;
        } else {
            won = false;
        }
    }

    /*
     * Check if high score needs to be updated, and save into storage
     */
    private void updateHighScore(){
        /* Fetch saved high score from storage */
        SharedPreferences settings = getSharedPreferences(Score.PREF, Context.MODE_PRIVATE);
        int savedHighScore = settings.getInt(Score.HIGHSCORE_PREF, 0);

        /* Save high score into SharedPreferences if it is higher */
        if (Score.highScore > savedHighScore){
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(Score.HIGHSCORE_PREF, Score.highScore);
            editor.commit();
        }
    }

    /*
     * Remove high score and scores from storage
     */
    private void resetSavedScores(){
        /* Reset high score in SharedPreferences to 0 */
        SharedPreferences settings = getSharedPreferences(Score.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Score.HIGHSCORE_PREF, Score.highScore);
        editor.commit();

        /* Remove scores from the database */
        mChildReference.removeValue();
    }

    /*
     * Redraw 0 to the high score view
     */
    private void resetHighScoreView(){
        Score.highScore = 0;
        Score.drawHighscore(highscoreView);
    }

    /*
     * Pop up window to confirm whether user wants to reset high score and all scores
     * On confirm, reset high score ald all scores
     * Otherwise, close pop up window
     */
    private void popupResetScoresConfirmation(){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.confirmation_popup, null);

        /* Build to pop up window */
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(false);

        /* On confirm, reset high score view, and remove scores in storage */
        alert.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                resetHighScoreView();
                resetSavedScores();
            }
        });

        /* On cancel, close the pop up window */
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){

            }
        });

        /* On pop up, set button color scheme based on win or loss */
        final AlertDialog dialog = alert.create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                /* Use appropriate getColor() function based on the Android build version */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (won){
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorWin, getTheme()));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorWin, getTheme()));
                    } else {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorLose, getTheme()));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorLose, getTheme()));
                    }
                }else {
                    /* getResources().getColor(int id) is deprecated */
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

        /* Set pop up window text color based on win or loss */
        confirmResetScoresView = (TextView) alertLayout.findViewById(R.id.confirmationTitle);
        /* Use appropriate getColor() function based on the Android build version */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (won){
                confirmResetScoresView.setTextColor(getResources().getColor(R.color.colorWin, getTheme()));
            } else {
                confirmResetScoresView.setTextColor(getResources().getColor(R.color.colorLose, getTheme()));
            }
        }else {
            /* getResources().getColor(int id) is deprecated */
            if (won) {
                confirmResetScoresView.setTextColor(getResources().getColor(R.color.colorWin));
            } else {
                confirmResetScoresView.setTextColor(getResources().getColor(R.color.colorLose));
            }
        }

    }
}

