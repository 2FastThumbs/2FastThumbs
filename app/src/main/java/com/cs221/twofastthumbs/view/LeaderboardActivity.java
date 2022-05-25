package com.cs221.twofastthumbs.view;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs221.twofastthumbs.R;
import com.cs221.twofastthumbs.Score;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    private static final int MAX_QUERY_LIMIT = 20;
    private RecyclerView rvScores;
    protected ScoresAdapter adapter;
    protected List<Score> allScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        rvScores = findViewById(R.id.rvScores);
        allScores = new ArrayList<>();
        adapter = new ScoresAdapter(getApplicationContext(), allScores);

        // Steps to use the recycler view:
        // 0. create layout for one row in the list
        // 1. create the adapter
        // 2. create the data source
        // 3. set the adapter on the recycler view
        rvScores.setAdapter(adapter);
        // 4. set the layout manager on the recycler view
        rvScores.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        queryScores();
    }

    private void queryScores() {
        ParseQuery<Score> query = ParseQuery.getQuery(Score.class);
        query.include(Score.KEY_USER);
        query.setLimit(MAX_QUERY_LIMIT);
        query.addDescendingOrder(Score.KEY_CREATED_KEY);
        query.findInBackground(new FindCallback<Score>() {
            @Override
            public void done(List<Score> scores, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting scores", e);
                    return;
                }
                for (Score score : scores) {
                    Log.i(TAG, "Score: " + score.getScore() + ", username: " + score.getUser());
                }
                allScores.addAll(scores);
                adapter.notifyDataSetChanged();
            }
        });
    }
}