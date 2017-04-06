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
import static com.group25.proj2.BluetoothConstants.playCommand;

public class MenuActivity extends AppCompatActivity {

    /* Views that display score and high score */
    private TextView highscoreView;
    private TextView highscoreSettingsView;

    private static boolean startUp = true; // Indicates first time in MenuActivity after application startup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initHighScore(); // Initialize high score from storage
        QuestionsActivity.initQuestions(); // Initialize question objects

        /* On menu button press, signal to DE2, reset the score to 0, and switch to StoryActivity */
        ImageButton menuPlayButton = (ImageButton) findViewById(R.id.menuPlayButton);
        menuPlayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Audio.soundPool.play(Audio.buttonPressSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);

                BluetoothActivity.sendToDE2(playCommand);

                Score.resetScore();
                Intent intent = new Intent(MenuActivity.this, StoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        /* On settings button press, pop up settings window */
        ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupSettings();
            }
        });

        /* Only initialize music and sound effects when the application is first opened */
        if (startUp == true) {
            initSounds();
        }
    }

    /**
     * Disable the device back button
     */
    @Override
    public void onBackPressed() {
    }

    /**
     * Initialize music volume and sound effect volume to the values saved in storage
     */
    private void initSoundControls() {
        SharedPreferences settings = getSharedPreferences(Score.PREF, Context.MODE_PRIVATE);
        Audio.musicVolumeSteps = settings.getInt(Audio.MUSIC_VOLUME_PREF, Audio.DEFAULT_VOLUME_STEPS);
        Audio.soundVolumeSteps = settings.getInt(Audio.SOUND_VOLUME_PREF, Audio.DEFAULT_VOLUME_STEPS);
    }

    /**
     * Initialize background music
     */
    private void initMusic() {
        Audio.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Audio.musicPlayer = MediaPlayer.create(this, R.raw.music);

        Audio.musicPlayer.start();
        Audio.musicPlayer.setVolume(Audio.convertToVolume(Audio.musicVolumeSteps), Audio.convertToVolume(Audio.musicVolumeSteps));
        Audio.musicPlayer.setLooping(true);
    }

    /**
     * Load sound effects
     */
    private void initSoundFX() {
        Audio.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        AudioAttributes audioAttributes;

        /* Create SoundPool using appropriate functions based on Android build version */
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

        /* Load all sound effects */
        Audio.moveSound = soundPool.load(this, R.raw.move, 1);
        Audio.rightAnswerSound = soundPool.load(this, R.raw.correct, 2);
        Audio.wrongAnswerSound = soundPool.load(this, R.raw.wrong, 3);
        Audio.winSound = soundPool.load(this, R.raw.win, 4);
        Audio.loseSound = soundPool.load(this, R.raw.lose, 5);
        Audio.buttonPressSound = soundPool.load(this, R.raw.press, 6);
    }

    /**
     *  Initialize volumes, background music, and sound effects
     */
    private void initSounds() {
        initSoundControls();
        initMusic();
        initSoundFX();
        startUp = false;
    }

    /**
     * Initialize high score to the high score saved in storage, and draw its view
     */
    private void initHighScore() {
        SharedPreferences settings = getSharedPreferences(Score.PREF, Context.MODE_PRIVATE);
        Score.highScore = settings.getInt(Score.HIGHSCORE_PREF, 0);

        highscoreView = (TextView) findViewById(R.id.highscoreMenu);
        Score.drawHighscore(highscoreView);
    }

    /**
     * Pop-up settings window
     */
    private void popupSettings() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.settings_popup, null);

        /* Build AlertDialog to close pop up when "OKAY" is pressed */
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("OKAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        /* Set pop-up text color */
        final AlertDialog dialog = alert.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                /* Use appropriate getColor() function based on Android build version */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorMenu, getTheme()));
                } else {
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.colorMenu));
                }
            }
        });

        dialog.show();

        /* Draw the high score view */
        highscoreSettingsView = (TextView) alertLayout.findViewById(R.id.highscoreSettings);
        Score.drawHighscore(highscoreSettingsView);

        /* On reset high score button press, reset high score */
        Button resetHighScoreButton = (Button) alertLayout.findViewById(R.id.resetHighScoreButtonSettings);
        resetHighScoreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetHighScoreView();
                resetSavedHighScore();
            }
        });

        /* Initialize music volume control bar */
        SeekBar musicVolumeControl = (SeekBar) alertLayout.findViewById(R.id.volumeMusicControl);
        musicVolumeControl.setMax(Audio.MAX_VOLUME_STEPS);
        musicVolumeControl.setProgress(Audio.musicVolumeSteps);
        musicVolumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            };

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            };

            /* On progress change, save new music volume to SharedPreferences and change the music volume */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences settings = getSharedPreferences(Score.PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(Audio.MUSIC_VOLUME_PREF, progress);
                editor.commit();

                Audio.musicVolumeSteps = progress;
                Audio.musicPlayer.setVolume(Audio.convertToVolume(Audio.musicVolumeSteps), Audio.convertToVolume(Audio.musicVolumeSteps));
            }
        });

        /* Initialize sound volume control bar */
        SeekBar soundVolumeControl = (SeekBar) alertLayout.findViewById(R.id.volumeSoundControl);
        soundVolumeControl.setMax(Audio.MAX_VOLUME_STEPS);
        soundVolumeControl.setProgress(Audio.soundVolumeSteps);
        soundVolumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            };

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            };

            /* On progress change, save new sound effects volume to SharedPreferences */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences settings = getSharedPreferences(Score.PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(Audio.SOUND_VOLUME_PREF, progress);
                editor.commit();

                Audio.soundVolumeSteps = progress;
            }
        });

    }

    /**
     * Reset the high score in storage to 0
     */
    private void resetSavedHighScore() {
        SharedPreferences settings = getSharedPreferences(Score.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Score.HIGHSCORE_PREF, Score.highScore);
        editor.commit();
    }

    /**
     * Redraw high score view to 0
     */
    private void resetHighScoreView() {
        Score.highScore = 0;
        Score.drawHighscore(highscoreView);
        Score.drawHighscore(highscoreSettingsView);
    }
}
