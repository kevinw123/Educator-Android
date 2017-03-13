package com.group25.proj2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MovementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement);

        ImageButton upButton = (ImageButton) findViewById(R.id.upButton);
        upButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // TODO: Send UP flag over Bluetooth
                BluetoothActivity.sendToDE2(BluetoothConstants.upCommand);
                // For testing
                Intent intent = new Intent(MovementActivity.this, QuestionsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        ImageButton downButton = (ImageButton) findViewById(R.id.downButton);
        downButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // TODO: Send DOWN flag over Bluetooth
                BluetoothActivity.sendToDE2(BluetoothConstants.downCommand);
            }
        });

        ImageButton leftButton = (ImageButton) findViewById(R.id.leftButton);
        leftButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // TODO: Send LEFT flag over Bluetooth
                BluetoothActivity.sendToDE2(BluetoothConstants.leftCommand);
            }
        });

        ImageButton rightButton = (ImageButton) findViewById(R.id.rightButton);
        rightButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // TODO: Send RIGHT flag over Bluetooth
                BluetoothActivity.sendToDE2(BluetoothConstants.rightCommand);
            }
        });
    }
}
