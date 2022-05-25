package com.cs221.twofastthumbs;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Score")
public class Score extends ParseObject {

    public static final String KEY_SCORE= "score";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_KEY = "createdAt";

    public String getScore() {
        return getString(KEY_SCORE);
    }

    public void setScore(String description) { put(KEY_SCORE, description); }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
}
