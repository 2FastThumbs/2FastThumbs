package com.cs221.twofastthumbs.view;

import static com.cs221.twofastthumbs.view.MainActivity.text;
import static com.cs221.twofastthumbs.view.MainActivity.minutes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.cs221.twofastthumbs.R;
import com.cs221.twofastthumbs.TypeRacer;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TypeTestActivity extends AppCompatActivity {

    TextView time;              // shows time at top of screen during test
    TextView prompt;            // the prompt that the user must copy in the test
    TextView warning;
    EditText input;             // the text box that the user will write into
    TextView WPM;               // WPM counter
    TextView accuracy;          // accuracy counter
    TextView characters;        // total character counter

    List<String> answers;   // list of user's inputs
    List<String> split_answers; // list of user's inputs split into individual words
    // index is used for traversal of text lines and keeping track of position in list,
    int index;

    long timeStart = 0;   // Time code was started in milliseconds
    long timeLapsed = 0;  // Time between each submit
    double mistakes;
    boolean hasMistakes = false;

    public static long wordsPerMinute;
    public static double acc;
    public static int totalChars;

    CountDownTimer timer;
    Boolean timer_isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_test);

        time = findViewById(R.id.time);
        prompt = findViewById(R.id.prompt);
        warning = findViewById(R.id.warning);
        input = findViewById(R.id.input);
        WPM = findViewById(R.id.WPM);
        accuracy = findViewById(R.id.accuracy);
        characters = findViewById(R.id.characters);

        answers = new ArrayList<>();             // reset lists
        split_answers = new ArrayList<>();
        index = 0;
        totalChars = 0;
        mistakes = 0;

        prompt.setTypeface(null, Typeface.BOLD);    // make prompt bold
        prompt.setText(text[index]);                   // show current prompt
        WPM.setText(R.string.wpm);                     // reset WPM
        accuracy.setText(R.string.accuracy);           // reset accuracy
        timeStart = System.currentTimeMillis();        // time in ms test was started

        warning.setVisibility(View.INVISIBLE);

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

        timer.start();

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
                timeLapsed = (System.currentTimeMillis() - timeStart) / 1000;
                wordsPerMinute = TypeRacer.calculate_wpm
                        (timeLapsed, totalChars + charSequence.length());
                acc = TypeRacer.calculate_accuracy
                        (totalChars + charSequence.length(), mistakes);
                WPM.setText("WPM: " + wordsPerMinute);
                accuracy.setText("Accuracy: " + acc + "%");
                characters.setText("Total characters: " + (totalChars + charSequence.length()));
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
                        input.getText().length() == text[index % text.length].length() &&
                        input.getText().length() > 0 && !hasMistakes) {

                    answers.add(index, input.getText().toString());
                    totalChars += input.getText().length();
                    split_answers.addAll(TypeRacer.prepare_text(input.getText().toString()));
                    index++;
                    input.getText().clear();    // clear the input box
                    prompt.setText(text[index % text.length]); // set new prompt
                    warning.setVisibility(View.GONE);
                    timeLapsed = (System.currentTimeMillis() - timeStart) / 1000;

                    wordsPerMinute = TypeRacer.calculate_wpm(timeLapsed, totalChars);
                    acc = TypeRacer.calculate_accuracy(totalChars, mistakes);
                    WPM.setText("WPM: " + wordsPerMinute);
                    accuracy.setText("Accuracy: " + acc + "%");
                    characters.setText("Total characters: " + totalChars);
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

    private void goResultScreen() {
        Intent i = new Intent(this, ResultActivity.class);
        startActivity(i);
        finish();
        startActivity(new Intent(this, ResultActivity.class));
    }
}