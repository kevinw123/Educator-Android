package com.group25.proj2;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class QuestionsActivity extends AppCompatActivity {
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Button nextButton = (Button) findViewById(R.id.questionsNextButton);
        nextButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(QuestionsActivity.this, LastGameActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });


        gestureDetector = new GestureDetector(this, new SingleTapUp());

        final Button aButton = (Button) findViewById(R.id.aButton);
        aButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return answerButtonEventHandler(aButton, event, BluetoothConstants.ACommand);
            }
        });

        final Button bButton = (Button) findViewById(R.id.bButton);
        bButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return answerButtonEventHandler(bButton, event, BluetoothConstants.BCommand);
            }
        });

        final Button cButton = (Button) findViewById(R.id.cButton);
        cButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return answerButtonEventHandler(cButton, event, BluetoothConstants.CCommand);
            }
        });

        final Button dButton = (Button) findViewById(R.id.dButton);
        dButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return answerButtonEventHandler(dButton, event, BluetoothConstants.DCommand);
            }
        });
    }

    private boolean answerButtonEventHandler(Button button, MotionEvent event, String command){
        if (gestureDetector.onTouchEvent(event)) {
            BluetoothActivity.sendToDE2(BluetoothConstants.DCommand);
            changeButtonColorOnUp(button);
            return true;
        }

        return answerButtonTouchHandler(button, event);

    }

    private boolean answerButtonTouchHandler(Button button, MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            changeButtonColorOnDown(button);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            changeButtonColorOnUp(button);
            return true;
        }

        return false;
    }

    private void changeButtonColorOnDown(Button b){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            b.setBackgroundColor(getResources().getColor(R.color.colorAnswerPress, getTheme()));
        }else {
            b.setBackgroundColor(getResources().getColor(R.color.colorAnswerPress));
        }
    }

    private void changeButtonColorOnUp(Button b){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            b.setBackgroundColor(getResources().getColor(R.color.colorText, getTheme()));
        }else {
            b.setBackgroundColor(getResources().getColor(R.color.colorText));
        }
    }
}
