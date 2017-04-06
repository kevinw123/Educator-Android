package com.group25.proj2;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * Created by Gina on 4/1/2017.
 */

public class Audio {
    public static int MAX_VOLUME_STEPS = 100;
    public static int DEFAULT_VOLUME_STEPS = 50; // Default volume if unchanged in settings

    /* Background music variables*/
    public static MediaPlayer musicPlayer;
    public static AudioManager audioManager;

    /* Sound effects variables */
    public static SoundPool soundPool;
    public static int MAX_SOUNDFX_STREAMS = 10; // Maximum number of streams that can be played at a time from soundPool
    public static int moveSound;
    public static int rightAnswerSound;
    public static int wrongAnswerSound;
    public static int winSound;
    public static int loseSound;
    public static int buttonPressSound;

    /* Music volume settings */
    public static String MUSIC_VOLUME_PREF = "MUSIC_VOLUME_PREF";
    public static int musicVolumeSteps;

    /* Sound volume settings */
    public static String SOUND_VOLUME_PREF = "SOUND_VOLUME_PREF";
    public static int soundVolumeSteps;
    /*
     * Converts volume steps (0 to 100) to a volume float between 0 and 1
     */
    public static float convertToVolume(int volumeSteps){
        return (float) (1 - (Math.log(Audio.MAX_VOLUME_STEPS - volumeSteps) / Math.log(Audio.MAX_VOLUME_STEPS)));
    }
}
