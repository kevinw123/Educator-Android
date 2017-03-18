package com.group25.proj2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MovementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement);

        Button nextButton = (Button) findViewById(R.id.movementNextButton);
        nextButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MovementActivity.this, QuestionsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        ImageButton upButton = (ImageButton) findViewById(R.id.upButton);
        upButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                move(BluetoothConstants.upCommand);

            }
        });

        ImageButton downButton = (ImageButton) findViewById(R.id.downButton);
        downButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                move(BluetoothConstants.downCommand);
            }
        });

        ImageButton leftButton = (ImageButton) findViewById(R.id.leftButton);
        leftButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                move(BluetoothConstants.leftCommand);
            }
        });

        ImageButton rightButton = (ImageButton) findViewById(R.id.rightButton);
        rightButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                move(BluetoothConstants.rightCommand);
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    private void checkEnemy(){
        String command = BluetoothActivity.readFromDE2();
        System.out.println(command);
        if (command.equals(BluetoothConstants.QUESTION_DE2)) {
            Intent intent = new Intent(MovementActivity.this, QuestionsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
    }

    private void move(String direction){
        BluetoothActivity.sendToDE2(direction);
        checkEnemy();
    }

}
