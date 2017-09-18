package com.gohool.moviedirectory.moviedirectory;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Data.MovieRecyclerViewAdapter;
import Model.Movie;
import Util.Util;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MovieRecyclerViewAdapter movieRecyclerViewAdapter;
    private List<Movie> movieList;
    private RequestQueue queue;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup queue volley
        queue = Volley.newRequestQueue(this);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog();
            }
        });

        movieList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Prefs prefs = new Prefs(MainActivity.this);
        String search = prefs.getSearch();
        movieList = getMovies(search);

        movieRecyclerViewAdapter = new MovieRecyclerViewAdapter(this, movieList);
        recyclerView.setAdapter(movieRecyclerViewAdapter);
        movieRecyclerViewAdapter.notifyDataSetChanged();

    }

    // Get movies
    public List<Movie> getMovies(String search) {
        movieList.clear();

        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.GET, Util.URL_LEFT + search + Util.URL_RIGHT, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray moviesArray = response.getJSONArray("Search");

                    // Iterate through movie arrays
                    for (int i = 0; i < moviesArray.length(); i++) {
                        JSONObject moviesObject = moviesArray.getJSONObject(i);
                        // Get particular things
                        Movie movie = new Movie();
                        movie.setTitle(moviesObject.getString("Title"));
                        movie.setYear("Year Released: " + moviesObject.getString("Year"));
                        movie.setMovieType("Type: " + moviesObject.getString("Type"));
                        movie.setPoster(moviesObject.getString("Poster"));
                        movie.setImdbID(moviesObject.getString("imdbID"));

                        movieList.add(movie);
                        Log.d("Movies: ", movie.getTitle());

                    }
                    movieRecyclerViewAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObject);

        return movieList;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.new_search) {
            showInputDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Method to show input dialog */
    public void showInputDialog() {
        alertDialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_view, null);
        final EditText newSearchEdt = (EditText) view.findViewById(R.id.searchEdt);
        Button submitButton = (Button) view.findViewById(R.id.SubmitButton);

        alertDialogBuilder.setView(view);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get what they entered
                Prefs prefs = new Prefs(MainActivity.this);

                if (!newSearchEdt.getText().toString().isEmpty()) {
                    String search = newSearchEdt.getText().toString();
                    prefs.setSearch(search);
                    movieList.clear();

                    getMovies(search);

                    movieRecyclerViewAdapter.notifyDataSetChanged(); // repopulate the recycle view with the new movies
                }

                alertDialog.dismiss();
            }
        });

    }
}
