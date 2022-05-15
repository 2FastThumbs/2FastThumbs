package com.cs221.twofastthumbs;

import java.util.ArrayList;
import java.util.List;

public class TypeRacer {
    /**
     * Split the text into individual character to check right away if the user misspelled
     * something or entered something unexpected. Used to calculate WPM
     *
     * @param text : text that the user will type.
     * @return A char array that contains every character of the text in order.
     */

    public static List<Character> break_text(String text){
        List<Character> charList = new ArrayList<>();
        for (char ch : text.toCharArray()) {
            charList.add(ch);
        }
        return charList;
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
     * @param number_of_chars: number of characters of each input
     * @return The user's WPM. Let WPM = the characters typed per minute divided by 5
     */

    public static long calculate_wpm(double time, int number_of_chars) {
        double cpm = number_of_chars / (time / 60);
        return  Math.round(cpm / 5);
    }
}
