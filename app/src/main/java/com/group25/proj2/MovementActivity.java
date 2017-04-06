package com.group25.proj2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MovementActivity extends AppCompatActivity {
    private final int SPEECH_RECOGNITION_CODE = 1;

    /* Views to display score and high score */
    private TextView scoreView;
    private TextView highscoreView;

    /* Movement arrow buttons */
    private ImageButton upButton;
    private ImageButton downButton;
    private ImageButton leftButton;
    private ImageButton rightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement);

        /* Draw score and high score to screen */
        scoreView = (TextView) findViewById(R.id.scoreMovement);
        highscoreView = (TextView) findViewById(R.id.highscoreMovement);
        Score.drawScores(scoreView, highscoreView);

        /* On movement button press, send signal to DE2 to move the character */
        upButton = (ImageButton) findViewById(R.id.upButton);
        upButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                move(BluetoothConstants.upCommand);
            }
        });

        downButton = (ImageButton) findViewById(R.id.downButton);
        downButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                move(BluetoothConstants.downCommand);
            }
        });

        leftButton = (ImageButton) findViewById(R.id.leftButton);
        leftButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                move(BluetoothConstants.leftCommand);
            }
        });

        rightButton = (ImageButton) findViewById(R.id.rightButton);
        rightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                move(BluetoothConstants.rightCommand);
            }
        });

        /* Microphone button for speech recognition of movements */
        ImageButton micButton = (ImageButton) findViewById(R.id.micButton);
        micButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startSpeechToText();
            }
        });
    }

    /**
     * Disable back button press
     */
    @Override
    public void onBackPressed() {
    }

    /**
     * Parse response received from DE2
     * If DE2 detected enemy, fetch question index and switch to QuestionActivity
     * Otherwise, if DE2 detected princess, switch to LastGameActivity
     */
    private void parseDE2MovementResponse() {

        /* Wait for Bluetooth command */
        for (int i = 0; i < 10000; i++) {
            String command = BluetoothActivity.readFromDE2();
            System.out.println("received" + command);

            /* If response received, parse it */
            if (!command.equals("")) {

                /* Try parsing an integer (indicates question index) */
                try {
                    int commandInt = Integer.parseInt(command);
                    if (commandInt >= 0 && commandInt < QuestionsActivity.NUM_QUESTIONS) {
                        System.out.println("RECEIVED QUESTION: " + command);
                        Intent intent = new Intent(MovementActivity.this, QuestionsActivity.class);
                        intent.putExtra("QUESTION_INDEX", commandInt);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }
                } catch (NumberFormatException e) {
                    /* Response is a letter, check if we should switch to LastGameActivity */
                    if (command.equals(BluetoothConstants.LASTGAME_DE2)){
                        Intent intent = new Intent(MovementActivity.this, LastGameActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }
                }

                break;
            }
        }

    }

    /**
     * Sends signal to DE2 to move the player
     * Retrieves and parses response from DE2
     * @param direction is the direction of movement
     */
    private void move(String direction) {
        Audio.soundPool.play(Audio.moveSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);

        BluetoothActivity.sendToDE2(direction);
        parseDE2MovementResponse();
    }

    /**
     * Start speech to text intent. This opens up Google Speech Recognition API dialog box to listen the speech input.
     */
    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback for speech recognition activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    performMovementsFromText(text);
                }
                break;
            }
        }
    }

    private void performMovementsFromText(String result) {
        if (result.equals("up")) {
            upButton.performClick();
        } else if (result.equals("down")) {
            downButton.performClick();
        } else if (result.equals("right")) {
            rightButton.performClick();
        } else if (result.equals("left")) {
            leftButton.performClick();
        } else {
            System.out.println("Nothing");
        }
    }
}
