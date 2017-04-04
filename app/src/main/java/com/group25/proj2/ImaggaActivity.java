package com.group25.proj2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static com.group25.proj2.DoneActivity.setWon;

public class ImaggaActivity extends AppCompatActivity {

    private static final String TAG = "ImaggaActivity";
    public static final String gameTitle = "IMAGGA";
    public static final String gameInstructions = "Given a word, take a picture of an object that matches the word!";
    public static final String scoreInstructions = "If you get the correct picture, you get 1 point.";
    public static final String livesInstructions = "You have 2 lives. For each incorrect picture, you lose 1 life. You must take a picture that matches the object before you run out of lives!";
    private static final int CAMERA_REQUEST = 1337;
    private String[] tags = new String[10];
    private ImageView imageView;
    private String selectedImagePath;
    String[] objectArray = {"PEN", "HAND", "PAPER"};
    private static int randomNum;
    private TextView scoreView;
    private TextView highscoreView;
    private int numOfLives = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagga);
        imageView = (ImageView) findViewById(R.id.image);
        Button cameraButton = (Button) findViewById(R.id.cameraButton);
        scoreView = (TextView) findViewById(R.id.scoreImagga);
        highscoreView = (TextView) findViewById(R.id.highscoreImagga);
        Score.drawScores(scoreView, highscoreView);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        TextView objectText = (TextView) findViewById(R.id.objectText);
        // Get random number for object to take picture of
        randomNum = getRandomNumber();
        objectText.setText(objectArray[randomNum]);

    }

    /*
     * Generates random number for objects to take a picture of
     */
    public int getRandomNumber(){
        Random r = new Random();
        return r.nextInt(objectArray.length);
    }

    /*
     * Prevents back button from being pressed
     */
    @Override
    public void onBackPressed() {
    }

    /*
     * Activity triggered after picture taken from camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            String result = "";
            final Uri selectedImageUri = getImageUri(getApplicationContext(), photo);
            selectedImagePath = getPath(selectedImageUri);
            PostImageToImaggaAsync postImageToImaggaAsync = new PostImageToImaggaAsync(selectedImagePath);
            try {
                result = postImageToImaggaAsync.execute().get();
            } catch (ExecutionException | InterruptedException e){

            }
            if(result != ""){
                // If we got the correct result, win.
                System.out.println(result);
                if(result.contains(objectArray[randomNum].toLowerCase())){
                    System.out.println("Got " + objectArray[randomNum]);
                    Score.updateScore(1, scoreView, highscoreView);
                    setWon(true);
                    launchGameOverScreen();
                }
                else {
                    // Decrement Lives and check if it is 0, if so then go to lsoe screen
                    numOfLives--;
                    if(numOfLives == 1) {
                        Toast.makeText(getApplicationContext(), "Incorrect! You have one life left. Try again!",
                                Toast.LENGTH_SHORT).show();
                    }
                    if(numOfLives == 0){
                        lose();
                    }
                }
            }
        }
    }

    /*
     * Lost in the game!
     */
    private void lose(){
        setWon(false);
        launchGameOverScreen();
    }

    /*
     * Launch the Game Over screen
     */
    private void launchGameOverScreen(){
        Intent intent = new Intent(this, DoneActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    /*
     * Return the Uri of the Bitmap provided.
     * Used to get String path to access photo
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /*
     * Return string path of the URI given
     */
    private String getPath(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }
}
