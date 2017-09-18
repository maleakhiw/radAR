package com.gohool.moviedirectory.moviedirectory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Model.Movie;
import Util.Util;

public class MovieDetailActivity extends AppCompatActivity {
    private Movie movie;
    private TextView movieTitle;
    private ImageView movieImage;
    private TextView movieYear;
    private TextView director;
    private TextView actors;
    private TextView category;
    private TextView rating;
    private TextView writers;
    private TextView plot;
    private TextView boxOffice;
    private TextView runTime;

    private RequestQueue queue;
    private String movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movie = (Movie) getIntent().getSerializableExtra("movie"); // get the intent extra information
        movieId = movie.getImdbID();
        queue = Volley.newRequestQueue(this);

        setUpUI();
        getMovieDetails(movieId);
    }

    private void setUpUI() {
        movieTitle = (TextView) findViewById(R.id.movieTitleIDDets);
        movieImage = (ImageView) findViewById(R.id.movieImageIDDets);
        movieYear = (TextView) findViewById(R.id.movieReleaseIDDets);
        director = (TextView) findViewById(R.id.movieDirectedIDDets);
        category = (TextView) findViewById(R.id.movieCategoryIDDets);
        rating = (TextView) findViewById(R.id.movieRatingIDDets);
        writers = (TextView) findViewById(R.id.movieWritersIDDets);
        plot = (TextView) findViewById(R.id.moviePlotIDDets);
        boxOffice = (TextView) findViewById(R.id.movieBoxOfficeIDDets);
        runTime = (TextView) findViewById(R.id.movieRuntimeIDDets);
        actors = (TextView) findViewById(R.id.movieActorsIDDets);
    }

    private void getMovieDetails(String id) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Util.URL_LEFT_DETAIL + id, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("Ratings")) {
                        JSONArray ratings = response.getJSONArray("Ratings");

                        String source = null;
                        String value = null;
                        if (ratings.length() > 0) {
                            JSONObject mRatings = ratings.getJSONObject(ratings.length() - 1);
                            source = mRatings.getString("Source");
                            value = mRatings.getString("Value");

                            rating.setText(source + ": " + value);
                        }
                        else {
                            rating.setText("Ratings: N/A");
                        }

                        movieTitle.setText("Title: " + response.getString("Title"));
                        movieYear.setText("Release: " + response.getString("Released"));
                        director.setText("Director: " + response.getString("Director"));
                        writers.setText("Writers: " + response.getString("Writer"));
                        plot.setText("Plot: " + response.getString("Plot"));
                        runTime.setText("Runtime" + response.getString("Runtime"));
                        actors.setText("Actors: " + response.getString("Actors"));

                        // picasso image
                        Picasso.with(getApplicationContext())
                                .load(response.getString("Poster"))
                                .into(movieImage);

                        boxOffice.setText("Box Office: " + response.getString("BoxOffice"));
                    }
                } catch(JSONException e) {
                    VolleyLog.d("Error", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(jsonObjectRequest);
    }
}
