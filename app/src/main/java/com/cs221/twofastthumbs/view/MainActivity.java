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
    TextView warning;           // text that shows when user does not type before sending
    EditText input;             // the text box that the user will write into
    Button start;               // start button
    TextView WPM;               // WPM counter
    TextView accuracy;          // accuracy counter
    Button btnSignOut;          // sign out button
    SeekBar timeSetting;
    TextView timeView;

    public static String[] text = { "This is an example sentence.",
                                    "Let's hope that this code works properly.",
                                    "If not, I don't know what I'll do!",
                                    "Anyways, we're looping back to the start.", };

    List<String> answers;   // list of user's inputs
    List<String> split_answers; // list of user's inputs split into individual words
    List<Character> split_chars; // List of user's input split by character
    // index is used for traversal of text lines and keeping track of position in list,
    int index;
    long minutes;
    long timeStart = 0;  // Time code was started in milliseconds
    long timeLapsed = 0;  // Time between each submit
    public static long wordsPerMinute;
    public static double acc;

    double mistakes;
    boolean hasMistakes = false;

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
        input = (EditText) findViewById(R.id.input);
        start = (Button) findViewById(R.id.start);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        WPM = (TextView) findViewById(R.id.WPM);
        accuracy = (TextView) findViewById(R.id.accuracy);
        timeSetting = (SeekBar) findViewById(R.id.timeSetting);
        timeView = (TextView) findViewById(R.id.timeView);

        input.setVisibility(View.GONE);             // hide input box at startup
        warning.setVisibility(View.GONE);           // hide warning at startup

        WPM.setVisibility(View.GONE);               // hide WPM at startup
        accuracy.setVisibility(View.GONE);          // hide accuracy at startup

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
                        time.setText("Time left: " + l / (1000*60) % 60 + ":" + l / 1000 % 60);
                    }

                    @Override
                    public void onFinish() {
                        timer_isRunning = false;                       // stop timer
                        goResultScreen();                              // result screen
                    }
                };
            }
        });


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timer == null){
                    warning.setVisibility(View.VISIBLE);
                    warning.setText(R.string.setTime);
                }
                else {
                    timer.start();                                 // start timer
                    start.setVisibility(View.INVISIBLE);           // hide start button
                    instructions.setVisibility(View.GONE);         // hide instructions
                    timeSetting.setVisibility(View.GONE);          // hide time seekbar
                    timeView.setVisibility(View.GONE);             // hide time setting view
                    btnSignOut.setVisibility(View.INVISIBLE);      // hide sign out button
                    input.setVisibility(View.VISIBLE);             // show input box
                    WPM.setVisibility(View.VISIBLE);               // show WPM
                    accuracy.setVisibility(View.VISIBLE);          // show accuracy
                    answers = new ArrayList<String>();             // reset lists
                    split_answers = new ArrayList<String>();
                    split_chars = new ArrayList<>();
                    index = 0;
                    mistakes = 0;
                    prompt.setVisibility(View.VISIBLE);            // make prompt visible
                    prompt.setTypeface(null, Typeface.BOLD);    // make prompt bold
                    prompt.setText(text[index]);                   // show current prompt
                    WPM.setText(R.string.wpm);                     // reset WPM
                    accuracy.setText(R.string.accuracy);           // reset accuracy
                    timeStart = System.currentTimeMillis();        // time in ms test was started
                }
            }
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String actual = charSequence.toString();
                String expected;
                if(actual.length() > text[index % text.length].length()) {
                    expected = text[index % text.length];
                }
                else{
                    expected = text[index % text.length].substring(0, actual.length());
                }
                if(!expected.equals(actual)){
                    warning.setVisibility(View.VISIBLE);
                    warning.setText("Expected input:\n" + expected);
                    if(!hasMistakes){
                        hasMistakes = true; // only increase number of mistakes if it is new
                        mistakes++;         // i.e. not adding onto the current mistake
                    }
                }
                else{
                    warning.setVisibility(View.GONE);
                    hasMistakes = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        input.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    // pressing the send button on the keyboard only while timer is running
                    if (actionId == EditorInfo.IME_ACTION_SEND && timer_isRunning &&
                    input.getText().length() == text[index % text.length].length() && input.getText().length() > 0 && !hasMistakes) {
                        answers.add(index, input.getText().toString());
                        split_answers.addAll(TypeRacer.prepare_text(input.getText().toString()));
                        split_chars.addAll(TypeRacer.break_text(input.getText().toString()));
                        index++;
                        input.getText().clear();    // clear the input box
                        prompt.setText(text[index % text.length]); // set new prompt
                        warning.setVisibility(View.GONE);
                        timeLapsed = (System.currentTimeMillis() - timeStart) / 1000;
                        wordsPerMinute = TypeRacer.calculate_wpm(timeLapsed, split_chars.size());
                        acc = TypeRacer.calculate_accuracy(split_chars.size(), mistakes);
                        WPM.setText("WPM: " + wordsPerMinute);
                        accuracy.setText("Accuracy: " + acc + "%");
                        return true;
                    }
                    if(input.getText().length() != text[index % text.length].length()) {
                        warning.setVisibility(View.VISIBLE);
                        warning.setText(R.string.please_type_the_prompt_given);
                    }
                    return false;
                }
        });

    }

    private void signOut() {
        ParseUser.logOutInBackground(e -> {
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
        });
    }
    private void goResultScreen() {
        Intent i = new Intent(this, ResultActivity.class);
        startActivity(i);
        finish();
        startActivity(new Intent(this, ResultActivity.class));
    }
}