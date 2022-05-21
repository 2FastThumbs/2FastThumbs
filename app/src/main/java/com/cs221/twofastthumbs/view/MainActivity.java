package com.cs221.twofastthumbs.view;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cs221.twofastthumbs.R;
import com.cs221.twofastthumbs.TypeRacer;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    TextView time;              // shows time at top of screen during test
    TextView instructions;      // shows instructions before and after test
    TextView prompt;            // the prompt that the user must copy in the test
    TextView warning;
    TextView warning2;
    Button start;               // start button
    Button btnSignOut;          // sign out button
    SeekBar timeSetting;        // time setting seek bar
    TextView timeView;          // text to show what the time is set to
    RadioButton exampleButton, wordButton, codeButton;

    public static String[] example =
                    { "This is an example sentence.",
                    "Let's hope that this code works properly.",
                    "If not, I don't know what I'll do!",
                    "Anyways, we're looping back to the start." };

    public static String[] words =
                    { "reaction traction interaction contraction",
                    "state rate date advocate accommodate facilitate",
                    "water daughter slaughter potter hotter squatter",
                    "pick trick brick click chick slapstick lunatic",
                    "case brace face place interface commonplace",
                    "land demand beforehand contraband misunderstand" };

    public static String[] code =
                    { "for(int i = 0; i < 5; i++)",
                    "return i + (x % 6);",
                    "if(getValue(x / 15) == 4)",
                    "System.out.println(String.format(%f, 3.14159));",
                    "list.stream().map(x -> x + 2).collect(Collectors.toList());" };

    public static String[] text;

    public static long minutes;         // number of minutes of testing time

    CountDownTimer timer;
    Boolean timer_isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        time = (TextView) findViewById(R.id.time);
        instructions = (TextView) findViewById(R.id.instructions);
        prompt = (TextView) findViewById(R.id.prompt);
        warning = (TextView) findViewById(R.id.warning);
        warning2 = (TextView) findViewById(R.id.warning2);
        start = (Button) findViewById(R.id.start);

        btnSignOut = (Button) findViewById(R.id.btn_sign_out);

        timeSetting = (SeekBar) findViewById(R.id.timeSetting);
        timeView = (TextView) findViewById(R.id.timeView);

        exampleButton = (RadioButton) findViewById(R.id.exampleButton);
        wordButton = (RadioButton) findViewById(R.id.wordButton);
        codeButton = (RadioButton) findViewById(R.id.codeButton);

        warning.setVisibility(View.INVISIBLE);      // hide warnings at startup
        warning2.setVisibility(View.GONE);

        text = null;

        btnSignOut.setOnClickListener(v -> {
            signOut();
        });

        timeSetting.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                minutes = i / 11 + 1;
                timeView.setText("Time: " + minutes + " minutes"); // update time view
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                warning.setVisibility(View.GONE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                minutes = seekBar.getProgress() / 11 + 1;
                timeView.setText("Time: " + minutes + " minutes");
                timer = new CountDownTimer(minutes * 60 * 1000, 1000) {
                    @Override
                    public void onTick(long l) {
                        timer_isRunning = true;
                    }

                    @Override
                    public void onFinish() {
                        timer_isRunning = false;                       // stop timer
                    }
                };
            }
        });


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timer == null || text == null){
                    if(timer == null) {
                        warning.setVisibility(View.VISIBLE);
                        warning.setText(R.string.setTime);
                    }
                    if(text == null){
                        warning2.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    timer.start();                                 // start timer
                    goTestScreen();
                }
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        warning2.setVisibility(View.GONE);
        switch(view.getId()) {
            case R.id.exampleButton:
                if (checked)
                    text = example;
                    break;
            case R.id.wordButton:
                if (checked)
                    text = words;
                    break;
            case R.id.codeButton:
                if (checked)
                    text = code;
                    break;
        }
    }
    private void signOut() {
        ParseUser.logOutInBackground(e -> {
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
        });
    }

    private void goTestScreen() {
        Intent i = new Intent(this, TypeTestActivity.class);
        startActivity(i);
        finish();
    }
}