package com.group25.proj2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ScoreList extends AppCompatActivity {

    ArrayList<ScoreObject> scores = new ArrayList();
    private ListView mScoreListView;
    ScoreAdapter scoreAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_list);

        // Initialize adapter for firebase list and set the ListView to correct element
        mScoreListView = (ListView) findViewById(R.id.list_scores);
        scoreAdapter =  new ScoreAdapter(ScoreList.this, scores);
        mScoreListView.setAdapter(scoreAdapter);
    }

    @Override
    protected void onStart(){
        super.onStart();
        // Read for Data change
        DoneActivity.mChildReference.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot details : dataSnapshot.getChildren()){
                    ScoreObject  score = details.getValue(ScoreObject.class);
                    // Add scores to array lsit
                    scores.add(score);

                }

                // Sort scores by highest score first
                Collections.sort(scores, new Comparator<ScoreObject>() {
                    @Override
                    public int compare(ScoreObject score2, ScoreObject score1)
                    {
                        return  score1.score - score2.score;
                    }
                });
                // Change the view of the array adapter to look different
                scoreAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError){

            }
        });
    }

}