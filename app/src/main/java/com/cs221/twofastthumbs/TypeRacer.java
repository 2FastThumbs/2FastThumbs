package com.cs221.twofastthumbs;

import java.util.ArrayList;
import java.util.List;

public class TypeRacer {

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
     */

    public static double calculate_accuracy (int totalCharacters, double mistakes) {
        if(mistakes > totalCharacters) return 0;
        return Math.round(100 * (totalCharacters - mistakes) / totalCharacters);
    }

    /**
     * Calculate the user's WPM, or words per minute.
     *
     * @param time: the time spent in the typing text
     * @param number_of_chars: number of characters of each input
     * @return The user's WPM. Let WPM = the characters typed per minute divided by 5
     */

    public static long calculate_wpm(double time, int number_of_chars) {
        double cpm = number_of_chars / (time / 60);
        return  Math.round(cpm / 5);
    }
}
