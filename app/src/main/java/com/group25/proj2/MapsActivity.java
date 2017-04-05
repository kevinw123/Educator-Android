package com.group25.proj2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.nfc.Tag;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.group25.proj2.DoneActivity.setWon;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String gameTitle = "FLAGS";
    public static final String gameInstructions = "Examine the flag image. Pan and/or zoom the map, and click the flag's country!";
    public static final String scoreInstructions = "For each correct country, you get 1 point.";
    public static final String livesInstructions = "You have 3 lives. For each incorrect country, you lose 1 life. You must find 3 countries before your lives run out!";

    private TextView scoreView;
    private TextView highscoreView;

    private GoogleMap mMap;
    private ImageView flagImage;
    private int[] flagArray = new int[]
            {
                    R.mipmap.canada, R.mipmap.china, R.mipmap.france, R.mipmap.japan,
                    R.mipmap.russia, R.mipmap.spain, R.mipmap.uk, R.mipmap.usa
            };
    private String[] countryName = new String[]
            {
                    "Canada", "China", "France", "Japan", "Russia", "Spain", "United Kingdom", "United States"
            };
    private int randomNum;
    private int numOfLives = 3;
    private int numOfCorrect = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        scoreView = (TextView) findViewById(R.id.scoreMaps);
        highscoreView = (TextView) findViewById(R.id.highscoreMaps);
        Score.drawScores(scoreView, highscoreView);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        flagImage = (ImageView) findViewById(R.id.flagImage);
        setFlagImage();
    }

    @Override
    public void onBackPressed() {
    }

    /*
     * Changes the flag on the screen based on random number generated
     */
    public void setFlagImage(){
        int i =  getRandomNumber();
        flagImage.setImageResource(flagArray[i]);
    }

    /*
     * Generates random number based on amount of flags we have in the game
     */
    public int getRandomNumber(){
        Random r = new Random();
        randomNum = r.nextInt(8);
        return randomNum;
    }

    /*
     * Set the game won to lose and go to gameover screen
     */
    private void lose(){
        setWon(false);
        launchGameOverScreen();
    }

    /*
     * Launch intent to go to DoneActivity
     */
    private void launchGameOverScreen(){
        Intent intent = new Intent(this, DoneActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);


        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();

                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                Marker marker = mMap.addMarker(getMarkerOptions(latLng));
                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                // Declare latitude and longitudes so we can track where user clicked
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;
                List<android.location.Address> addresses = new ArrayList<android.location.Address>();
                try {
                    addresses = gcd.getFromLocation(latitude, longitude, 1);
                }
                catch(IOException e) {
                    Log.e("MYAPP", "exception: " + e.toString());
                }
                if (addresses.size() > 0)
                {
                    // Get the country where user clicked
                    String selectionCountry = addresses.get(0).getCountryName();
                    if(selectionCountry.equals(countryName[randomNum])){
                        numOfCorrect++;
                        Score.updateScore(1, scoreView, highscoreView);
                        // If users get it correct, display a toast
                        if(numOfCorrect < 3) {
                            Audio.soundPool.play(Audio.rightAnswerSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);
                            Toast.makeText(getApplicationContext(), "Correct! You picked " + selectionCountry + ". Number of correct answers: " + Integer.toString(numOfCorrect),
                                    Toast.LENGTH_SHORT).show();
                        }
                        // If users get 3 correct they win
                        if(numOfCorrect == 3){
                            setWon(true);
                            launchGameOverScreen();
                        }
                        // Set the flag for the game

                        if(numOfCorrect != 3) {
                            setFlagImage();
                        }
                    } else {
                        // If user gets answer wrong then lose a life
                        numOfLives--;
                        if(numOfLives > 0) {
                            Audio.soundPool.play(Audio.wrongAnswerSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);
                            Toast.makeText(getApplicationContext(), "Incorrect! You picked " + selectionCountry + ". Number of lives left: " + Integer.toString(numOfLives),
                                    Toast.LENGTH_SHORT).show();
                        }
                        if(numOfLives == 0){
                            // Lose when you have no more lives
                            lose();
                        }
                    }
                }
                marker.showInfoWindow();
            }
        });

        // Allows for dragging marker on Google Maps
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.hideInfoWindow();
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                LatLng latLng = marker.getPosition();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                mMap.clear();
                marker = mMap.addMarker(getMarkerOptions(marker.getPosition()));
                marker.showInfoWindow();
            }
        });
    }

    MarkerOptions getMarkerOptions(LatLng latLng){
        return new MarkerOptions().position(latLng).title(latLng.latitude + ", " + latLng.longitude).draggable(true);
    }
}
