package com.example.cineverse.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class SearchAdapter
        extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<Movie> movieList;
    private final OnMovieClickListener listener;
    private int lastAnimatedPosition = -1;

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public SearchAdapter(List<Movie> movieList,
                         OnMovieClickListener listener) {
        this.movieList = new ArrayList<>(movieList);
        this.listener  = listener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_search,
                        parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull SearchViewHolder holder,
            int position) {

        Movie movie = movieList.get(position);
        Context ctx = holder.itemView.getContext();

        holder.textTitle.setText(movie.getTitle());
        holder.textRating.setText(
                String.format("%.1f/10",
                        movie.getRating()));
        holder.textDate.setText(
                formatYear(movie.getReleaseDate()));

        String overview = movie.getOverview();
        holder.textOverview.setText(
                (overview != null && !overview.isEmpty())
                        ? overview
                        : "Synopsis non disponible.");

        // Glide optimisé
        String posterUrl =
                (movie.getPosterPath() != null
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

        holder.itemView.setOnClickListener(v ->
                listener.onMovieClick(movie));
    }

    private String formatYear(String date) {
        if (date == null || date.length() < 4)
            return "—";
        return date.substring(0, 4);
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
        set.setDuration(300);
        set.setStartDelay(
                Math.min(position * 50L, 300L));
        set.start();
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void updateList(List<Movie> newList) {
        DiffUtil.DiffResult result =
                DiffUtil.calculateDiff(
                        new SearchDiffCallback(
                                movieList, newList));
        movieList = new ArrayList<>(newList);
        result.dispatchUpdatesTo(this);
        lastAnimatedPosition = -1;
    }

    private static class SearchDiffCallback
            extends DiffUtil.Callback {

        private final List<Movie> oldList;
        private final List<Movie> newList;

        SearchDiffCallback(
                List<Movie> old, List<Movie> n) {
            this.oldList = old;
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
                int o, int n) {
            return oldList.get(o).getId()
                    == newList.get(n).getId();
        }

        @Override
        public boolean areContentsTheSame(
                int o, int n) {
            return oldList.get(o).getTitle()
                    .equals(newList.get(n).getTitle())
                    && oldList.get(o).getRating()
                    == newList.get(n).getRating();
        }
    }

    public static class SearchViewHolder
            extends RecyclerView.ViewHolder {

        final ImageView imagePoster;
        final TextView  textTitle, textDate,
                textRating, textOverview;

        public SearchViewHolder(
                @NonNull View itemView) {
            super(itemView);
            imagePoster  = itemView.findViewById(
                    R.id.imagePoster);
            textTitle    = itemView.findViewById(
                    R.id.textTitle);
            textDate     = itemView.findViewById(
                    R.id.textDate);
            textRating   = itemView.findViewById(
                    R.id.textRating);
            textOverview = itemView.findViewById(
                    R.id.textOverview);
        }
    }
}