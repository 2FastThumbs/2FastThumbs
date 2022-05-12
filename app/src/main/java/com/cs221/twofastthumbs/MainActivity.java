package com.cs221.twofastthumbs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import java.lang.Math;

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
    public static String[] text = { "This is an example sentence.",
                                    "Let's hope that this code works properly.",
                                    "If not, I don't know what I'll do!",
                                    "Anyways, we're looping back to the start.", };

    List<String> answers;   // list of user's inputs
    List<String> split_answers; // list of user's inputs split into individual words
    // index is used for traversal of text lines and keeping track of position in list,
    // sentencesCleared is used for calculating WPM and accuracy if user clears a loop
    int index, sentencesCleared;
    long minutes = 2;   // time is hardcoded at the moment, sorry

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

        input.setVisibility(View.GONE);             // hide input box at startup
        warning.setVisibility(View.GONE);           // hide warning at startup

        WPM.setVisibility(View.GONE);               // hide WPM at startup
        accuracy.setVisibility(View.GONE);          // hide accuracy at startup

        btnSignOut.setOnClickListener(v -> {
            signOut();
        });

        timer = new CountDownTimer(minutes * 60 * 1000, 1000) {
            @Override
            public void onTick(long l) {
                timer_isRunning = true;
                time.setText("Time left: " + l / (1000*60) % 60 + ":" + l / 1000 % 60);
            }

            @Override
            public void onFinish() {
                timer_isRunning = false;                       // stop timer
                time.setText(R.string.time_up);
                instructions.setVisibility(View.VISIBLE);     // show instructions
                instructions.setText(R.string.test_end);
                start.setVisibility(View.VISIBLE);            // show start button
                prompt.setVisibility(View.INVISIBLE);         // hide prompt
                input.setVisibility(View.GONE);               // hide input box
                warning.setVisibility(View.GONE);             // hide warning
            }
        };

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.start();                                 // start timer
                start.setVisibility(View.INVISIBLE);           // hide start button
                instructions.setVisibility(View.GONE);         // hide instructions
                btnSignOut.setVisibility(View.INVISIBLE);      // hide sign out button
                input.setVisibility(View.VISIBLE);             // show input box
                WPM.setVisibility(View.VISIBLE);               // show WPM
                accuracy.setVisibility(View.VISIBLE);          // show accuracy
                answers = new ArrayList<String>();             // reset lists
                split_answers = new ArrayList<String>();
                index = 0;
                sentencesCleared = 0;
                prompt.setVisibility(View.VISIBLE);            // make prompt visible
                prompt.setTypeface(null, Typeface.BOLD);    // make prompt bold
                prompt.setText(text[index]);                   // show current prompt
                WPM.setText(R.string.wpm);                     // reset WPM
                accuracy.setText(R.string.accuracy);           // reset accuracy
            }
        });

        input.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    // pressing the send button on the keyboard only while timer is running
                    if (actionId == EditorInfo.IME_ACTION_SEND && timer_isRunning && input.getText().length() > 0) {
                        answers.add(index, input.getText().toString());
                        split_answers.addAll(prepare_text(input.getText().toString()));
                        index++;
                        sentencesCleared++;
                        input.getText().clear();    // clear the input box
                        prompt.setText(text[index % text.length]); // set new prompt
                        warning.setVisibility(View.GONE);
                        WPM.setText("WPM: " + calculate_wpm(minutes, split_answers.size()));
                        accuracy.setText("Accuracy: " + calculate_accuracy(sentencesCleared, text, answers) + "%");
                        return true;
                    }
                    if(input.getText().length() == 0)
                        warning.setVisibility(View.VISIBLE);
                    return false;
                }
        });

    }

    /**
     * Split sentences into words so that we can easily compare expected word with the
     * given word in order to calculate accuracy.
     *
     * @param text: text that the user will type.
     * @return A list that contains every word in the text parameter as individual strings.
     */

    public static List<String> prepare_text(String text){
        List<String> words = new ArrayList<String>();
        List<Integer> breaks = new ArrayList<Integer>();    // indices where character is a space
         for(int i = 0; i < text.length(); i++){
            if(text.charAt(i) == ' ')
                breaks.add(i);
         }
         int startingIndex = 0;
         // break the sentence into individual words
         // also checks for blank space strings that may tamper results
         for(int i: breaks){
             String currentWord = text.substring(startingIndex, i);
             if(currentWord.length() > 0) {
                 words.add(text.substring(startingIndex, i));
             }
             startingIndex = i + 1;
         }
        words.add(text.substring(startingIndex, text.length())); // add the last word in sentence
        return words;
    }

    /**
     * Calculate the user's accuracy based on the correctness of the words they typed.
     *
     * @param sentencesCleared: the number of sentences the user cleared during the test
     * @param original_text: the original text exactly how it was written
     * @param answers: the list of words that the user typed
     * @return The accuracy of the user's typing. Assume for the time being we
     * consider accuracy = (# correct / total # of words written) * 100
     */

    public static double calculate_accuracy
    (int sentencesCleared, String[] original_text, List<String> answers){
        List<String> expectedWords;
        List<String> actualWords;
        int total = 0;
        int mistakes = 0;
        for(int i = 0; i < sentencesCleared; i++){
            expectedWords = prepare_text(original_text[i % original_text.length]);
            total += expectedWords.size();
            actualWords = prepare_text(answers.get(i));
            int j = 0;
            while(j < expectedWords.size() && j < actualWords.size()){
                if(!expectedWords.get(j).equals(actualWords.get(j)))
                    mistakes++;
                j++;
            }
            // This line *should* have the effect of adding a mistake for every single
            // missing word or extraneous word, and doing nothing if the number of words match
            mistakes += Math.max(expectedWords.size(), actualWords.size()) - j;
        }
        // this case may happen if the user inputs too many words or too little words
        if (mistakes > total)
            return 0;
        else{
            return Math.round(100 * ((total - mistakes) / (double) total));
        }
    }

    /**
     * Calculate the user's WPM, or words per minute.
     *
     * @param time: the time spent in the typing text
     * @param number_of_words: number of words the user typed (aka user_input.length)
     * @return The user's WPM. Let WPM = the number of words typed / time spent typing
     */

    public static long calculate_wpm(long time, int number_of_words){ return number_of_words / time; }

    private void signOut() {
        ParseUser.logOutInBackground(e -> {
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
        });
    }
}