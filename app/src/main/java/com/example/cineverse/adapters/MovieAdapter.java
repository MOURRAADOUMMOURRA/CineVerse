package com.example.cineverse.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.cineverse.R;
import com.example.cineverse.models.Movie;
import com.example.cineverse.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter
        extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private final OnMovieClickListener listener;
    private int lastAnimatedPosition = -1;

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie, int position);
        void onMovieLongClick(
                Movie movie, int position, View view);
    }

    // ✅ Accepte ArrayList et List
    public MovieAdapter(List<Movie> movieList,
                        OnMovieClickListener listener) {
        this.movieList = new ArrayList<>(movieList);
        this.listener  = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_movie,
                        parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MovieViewHolder holder,
            int position) {

        Movie movie   = movieList.get(position);
        Context ctx   = holder.itemView.getContext();

        // Données texte
        holder.textTitle.setText(movie.getTitle());
        holder.textRating.setText(
                String.format("%.1f/10",
                        movie.getRating()));
        holder.ratingBar.setRating(
                movie.getPersonalRating());
        applyStatusStyle(
                holder.textStatus, movie.getStatus());

        // Glide optimisé
        String posterUrl = (movie.getPosterPath() != null
                && !movie.getPosterPath().isEmpty())
                ? Constants.IMAGE_URL
                + movie.getPosterPath()
                : null;

        Glide.with(ctx)
                .load(posterUrl)
                .apply(new RequestOptions()
                        .placeholder(
                                R.drawable.ic_poster_placeholder)
                        .error(
                                R.drawable.ic_poster_placeholder)
                        .centerCrop()
                        .override(300, 450))
                .diskCacheStrategy(
                        com.bumptech.glide.load.engine
                                .DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions
                        .withCrossFade(200))
                .into(holder.imagePoster);

        // Animation
        if (position > lastAnimatedPosition) {
            animateItem(holder.itemView, position);
            lastAnimatedPosition = position;
        }

        // Clics
        holder.itemView.setOnClickListener(v ->
                listener.onMovieClick(
                        movie,
                        holder.getAdapterPosition())
        );
        holder.itemView.setOnLongClickListener(v -> {
            listener.onMovieLongClick(
                    movie,
                    holder.getAdapterPosition(),
                    v);
            return true;
        });
    }

    private void animateItem(View view, int position) {
        view.setTranslationY(60f);
        view.setAlpha(0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(
                        view, "translationY", 60f, 0f),
                ObjectAnimator.ofFloat(
                        view, "alpha", 0f, 1f)
        );
        set.setDuration(350);
        set.setStartDelay(
                Math.min(position * 50L, 300L));
        set.start();
    }

    private void applyStatusStyle(
            TextView chip, String status) {
        if (status == null) status = "À voir";
        chip.setText(status);
        switch (status) {
            case "Vu":
                chip.setTextColor(
                        chip.getContext().getColor(
                                R.color.status_vu));
                chip.setBackgroundResource(
                        R.drawable.bg_chip_vu);
                break;
            case "En cours":
                chip.setTextColor(
                        chip.getContext().getColor(
                                R.color.status_en_cours));
                chip.setBackgroundResource(
                        R.drawable.bg_chip_en_cours);
                break;
            default:
                chip.setTextColor(
                        chip.getContext().getColor(
                                R.color.status_a_voir));
                chip.setBackgroundResource(
                        R.drawable.bg_status_chip);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    // ✅ DiffUtil — pas de notifyDataSetChanged
    public void updateList(List<Movie> newList) {
        DiffUtil.DiffResult result =
                DiffUtil.calculateDiff(
                        new MovieDiffCallback(
                                movieList, newList));
        movieList = new ArrayList<>(newList);
        result.dispatchUpdatesTo(this);
        lastAnimatedPosition = -1;
    }

    private static class MovieDiffCallback
            extends DiffUtil.Callback {

        private final List<Movie> oldList;
        private final List<Movie> newList;

        MovieDiffCallback(List<Movie> o, List<Movie> n) {
            this.oldList = o;
            this.newList = n;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(
                int oldPos, int newPos) {
            return oldList.get(oldPos).getId()
                    == newList.get(newPos).getId();
        }

        @Override
        public boolean areContentsTheSame(
                int oldPos, int newPos) {
            Movie o = oldList.get(oldPos);
            Movie n = newList.get(newPos);
            return o.getTitle().equals(n.getTitle())
                    && o.getRating() == n.getRating()
                    && o.getPersonalRating()
                    == n.getPersonalRating()
                    && String.valueOf(o.getStatus())
                    .equals(String.valueOf(
                            n.getStatus()));
        }
    }

    public static class MovieViewHolder
            extends RecyclerView.ViewHolder {

        final ImageView imagePoster;
        final TextView  textTitle, textRating,
                textStatus;
        final RatingBar ratingBar;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePoster = itemView.findViewById(
                    R.id.imagePoster);
            textTitle   = itemView.findViewById(
                    R.id.textTitle);
            textRating  = itemView.findViewById(
                    R.id.textRating);
            textStatus  = itemView.findViewById(
                    R.id.textStatus);
            ratingBar   = itemView.findViewById(
                    R.id.ratingBar);
        }
    }
}