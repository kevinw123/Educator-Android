package com.group25.proj2;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * Created by Gina on 4/1/2017.
 */

public class Audio {
    public static MediaPlayer musicPlayer;
    public static SoundPool soundPool;
    public static int rightAnswerSound;
    public static int wrongAnswerSound;
    public static AudioManager audioManager;

    public static int MAX_VOLUME_STEPS = 100;
    public static int DEFAULT_VOLUME_STEPS = 50;

    public static String MUSIC_PREF = "MUSIC_PREF";
    public static boolean playMusic;

    public static String MUSIC_VOLUME_PREF = "MUSIC_VOLUME_PREF";
    public static int musicVolumeSteps;

    public static String SOUNDFX_PREF = "SOUNDFX_PREF";
    public static boolean playSoundFX;

    public static String SOUND_VOLUME_PREF = "SOUND_VOLUME_PREF";
    public static int soundVolumeSteps;

    public static int MAX_SOUNDFX_STREAMS = 10;

    public static float convertToVolume(int volumeSteps){
        return (float) (1 - (Math.log(Audio.MAX_VOLUME_STEPS - volumeSteps) / Math.log(Audio.MAX_VOLUME_STEPS)));
    }
}
