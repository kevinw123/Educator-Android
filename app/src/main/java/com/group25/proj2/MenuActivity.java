package com.group25.proj2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import static com.group25.proj2.Audio.soundPool;
import static com.group25.proj2.BluetoothConstants.startCommand;

public class MenuActivity extends AppCompatActivity {
    private TextView highscoreView;
    private TextView highscoreSettingsView;

    private static boolean startUp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initHighscore();
        QuestionsActivity.initQuestions();

        ImageButton menuPlayButton = (ImageButton) findViewById(R.id.menuPlayButton);
        menuPlayButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // TODO: Send PLAY flag over Bluetooth
                BluetoothActivity.sendToDE2(startCommand); // uncomment later

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

        if (startUp == true) {
            initSounds();
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void initSoundControls(){
        SharedPreferences settings = getSharedPreferences(Score.PREF, Context.MODE_PRIVATE);

        Audio.playMusic = settings.getBoolean(Audio.MUSIC_PREF, true);
        Audio.musicVolumeSteps = settings.getInt(Audio.MUSIC_VOLUME_PREF, Audio.DEFAULT_VOLUME_STEPS);

        Audio.playSoundFX = settings.getBoolean(Audio.SOUNDFX_PREF, true);
        Audio.soundVolumeSteps = settings.getInt(Audio.SOUND_VOLUME_PREF, Audio.DEFAULT_VOLUME_STEPS);
    }

    private void initMusic(){
        if (Audio.playMusic) {
            Audio.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            Audio.musicPlayer = MediaPlayer.create(this, R.raw.music);
            System.out.println("Playing music");
            Audio.musicPlayer.start();
            Audio.musicPlayer.setVolume(Audio.convertToVolume(Audio.musicVolumeSteps), Audio.convertToVolume(Audio.musicVolumeSteps));
            Audio.musicPlayer.setLooping(true);
        }
    }

    private void initSoundFX(){
        if (Audio.playSoundFX) {
            Audio.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            AudioAttributes audioAttributes;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();

                soundPool = new SoundPool.Builder()
                        .setAudioAttributes(audioAttributes)
                        .setMaxStreams(Audio.MAX_SOUNDFX_STREAMS)
                        .build();

            } else {
                soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            }

            Audio.rightAnswerSound = soundPool.load(this, R.raw.correct, 1);
            Audio.wrongAnswerSound = soundPool.load(this, R.raw.wrong, 2);
        }
    }

    private void initSounds(){
        initSoundControls();
        initMusic();
        initSoundFX();
        startUp = false;
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

        highscoreSettingsView = (TextView) alertLayout.findViewById(R.id.highscoreSettings);
        Score.drawHighscore(highscoreSettingsView);

        Button resetHighscoreButton = (Button) alertLayout.findViewById(R.id.resetHighscoreButtonSettings);
        resetHighscoreButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                resetHighscoreView();
                resetSavedHighscore();
            }
        });

        // Init music volume control bar

        SeekBar musicVolumeControl = (SeekBar) alertLayout.findViewById(R.id.volumeMusicControl);
        musicVolumeControl.setMax(Audio.MAX_VOLUME_STEPS);
        musicVolumeControl.setProgress(Audio.musicVolumeSteps);
        musicVolumeControl.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onStartTrackingTouch(SeekBar seekBar){

            };

            @Override
            public void onStopTrackingTouch(SeekBar seekBar){

            };

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                SharedPreferences settings = getSharedPreferences(Score.PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(Audio.MUSIC_VOLUME_PREF, progress);
                editor.commit();

                Audio.musicVolumeSteps = progress;
                Audio.musicPlayer.setVolume(Audio.convertToVolume(Audio.musicVolumeSteps), Audio.convertToVolume(Audio.musicVolumeSteps));
            }
        });

        // Init sound volume control bar

        SeekBar soundVolumeControl = (SeekBar) alertLayout.findViewById(R.id.volumeSoundControl);
        soundVolumeControl.setMax(Audio.MAX_VOLUME_STEPS);
        soundVolumeControl.setProgress(Audio.soundVolumeSteps);
        soundVolumeControl.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onStartTrackingTouch(SeekBar seekBar){

            };

            @Override
            public void onStopTrackingTouch(SeekBar seekBar){

            };

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                SharedPreferences settings = getSharedPreferences(Score.PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(Audio.SOUND_VOLUME_PREF, progress);
                editor.commit();

                Audio.soundVolumeSteps = progress;
            }
        });

    }

    private void resetSavedHighscore(){
        SharedPreferences settings = getSharedPreferences(Score.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Score.HIGHSCORE_PREF, Score.highscore);
        editor.commit();
    }

    private void resetHighscoreView(){
        Score.highscore = 0;
        Score.drawHighscore(highscoreView);
        Score.drawHighscore(highscoreSettingsView);
    }


}
