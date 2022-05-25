package com.cs221.twofastthumbs.view;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cs221.twofastthumbs.R;

import com.cs221.twofastthumbs.Score;
import com.parse.ParseFile;

import java.util.List;

public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.ViewHolder> {


    private Context context;
    private List<Score> scores;

    public ScoresAdapter(Context context, List<Score> scores){
        this.context = context;
        this.scores = scores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_scores, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Score score = scores.get(position);
        holder.bind(score);
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername;
        private TextView tvScore;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvScore = itemView.findViewById(R.id.tvScore);
        }

        public void bind(Score score) {
            // Bind the post data to the view elements
            tvUsername.setText(score.getUser().getUsername());
            tvScore.setText(score.getScore());
            }
        }
    }
