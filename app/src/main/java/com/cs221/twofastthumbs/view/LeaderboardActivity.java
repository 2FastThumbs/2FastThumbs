package com.cs221.twofastthumbs.view;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs221.twofastthumbs.R;
import com.cs221.twofastthumbs.Score;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

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
    }
}