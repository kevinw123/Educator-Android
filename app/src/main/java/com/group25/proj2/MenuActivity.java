package com.group25.proj2;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.Iterator;
import java.util.Set;

import static com.group25.proj2.BluetoothConstants.startCommand;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        ImageButton menuPlayButton = (ImageButton) findViewById(R.id.menuPlayButton);
        menuPlayButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // TODO: Send PLAY flag over Bluetooth
                BluetoothActivity.sendToDE2(startCommand);
                Intent intent = new Intent(MenuActivity.this, StoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

    @Override
    public void onBackPressed() {
    }


}
