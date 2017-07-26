package com.example.giuseppegarone.tickettoragnarok;

import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TopPlayersActivity extends AppCompatActivity {

    /*we will use these constants later to pass the score nickname and id to another activity
    public static final String SCORE_NICKNAME = "net.simplifiedcoding.firebasedatabaseexample.artistname";
    public static final String SCORE_ID = "net.simplifiedcoding.firebasedatabaseexample.artistid";*/

    //view objects
    EditText editTextName;
    Button buttonAddScore;
    ListView listViewScores;
    TextView header;
    Typeface customFont;

    //a list to store all the scores from firebase database
    List<Score> scores;

    //our database reference object
    DatabaseReference databaseClassifica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_players);

        // Orientamento landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //getting the reference of classifica node
        databaseClassifica = FirebaseDatabase.getInstance().getReference("Classifica");

        //getting views
        editTextName = (EditText) findViewById(R.id.editTextName);
        listViewScores = (ListView) findViewById(R.id.listViewScores);
        header = (TextView) findViewById(R.id.textView);

        customFont = Typeface.createFromAsset(getAssets(), "gameplay.ttf");
        header.setTypeface(customFont);

        buttonAddScore = (Button) findViewById(R.id.buttonAddScore);

        //list to store scores
        scores = new ArrayList<>();


        //adding an onclicklistener to button
        buttonAddScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling the method addScore()
                //the method is defined below
                //this method is actually performing the write operation
                addScore();
            }
        });
    }

    /*
    * This method is saving a new score to the
    * Firebase Realtime Database
    * */
    private void addScore() {
        //getting the values to save
        String nickname = editTextName.getText().toString().trim();
        String score = "12";

        //checking if the value is provided
        if (!TextUtils.isEmpty(nickname)) {

            //getting a unique id using push().getKey() method
            //it will create a unique id and we will use it as the Primary Key for our Score
            String id = databaseClassifica.push().getKey();

            //creating a Score Object
            Score punteggio = new Score(nickname, score);

            //Saving the Score
            databaseClassifica.child(id).setValue(punteggio);

            //setting edittext to blank again
            editTextName.setText("");

            //displaying a success toast
            Toast.makeText(this, "New Score added", Toast.LENGTH_LONG).show();
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = databaseClassifica.orderByChild("score");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous score list
                scores.clear();

                //iterating through all the nodes
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    //getting score
                    Score punteggio = child.getValue(Score.class);
                    //adding score to the list
                    scores.add(punteggio);
                }

                //creating adapter
                TopPlayersList scoreAdapter = new TopPlayersList(TopPlayersActivity.this, scores);
                //attaching adapter to the listview
                listViewScores.setAdapter(scoreAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}