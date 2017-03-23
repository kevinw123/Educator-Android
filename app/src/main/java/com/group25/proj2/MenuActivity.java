package com.group25.proj2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
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
                //BluetoothActivity.sendToDE2(startCommand); // uncomment later

                Score.resetScore();

                Intent intent = new Intent(MenuActivity.this, StoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                popupSettings();
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

    private void popupSettings(){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.settings_popup, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("OKAY", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){

            }
        });

        final AlertDialog dialog = alert.create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                                      @Override
                                      public void onShow(DialogInterface arg0) {
                                          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                              dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorMenu, getTheme()));
                                          }else {
                                              dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.colorMenu));
                                          }
                                      }
                                  });

        dialog.show();
    }


}
