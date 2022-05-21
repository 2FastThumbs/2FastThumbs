package com.cs221.twofastthumbs.view;

import static com.cs221.twofastthumbs.view.TypeTestActivity.acc;
import static com.cs221.twofastthumbs.view.TypeTestActivity.wordsPerMinute;
import static com.cs221.twofastthumbs.view.TypeTestActivity.totalChars;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.cs221.twofastthumbs.R;
import com.parse.ParseUser;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ResultActivity extends AppCompatActivity {

    TextView time;
    TextView instructions;
    Button btnStartOver;
    TextView WPM;
    TextView accuracy;
    TextView characters;
    Button btnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        time = findViewById(R.id.time);
        instructions = findViewById(R.id.instructions);
        btnStartOver = findViewById(R.id.start);
        WPM = findViewById(R.id.WPM);
        accuracy = findViewById(R.id.accuracy);
        characters = findViewById(R.id.characters);
        btnSignOut = findViewById(R.id.btn_sign_out);

        btnSignOut.setOnClickListener(v -> {
            signOut();
        });
        btnStartOver.setOnClickListener(v -> {
            goMainActivity();
        });

        WPM.setText("WPM: " + wordsPerMinute);
        accuracy.setText("Accuracy: " + acc + "%");
        characters.setText("Total characters: " + totalChars);


    }

    private void signOut() {
        ParseUser.logOutInBackground(e -> {
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}