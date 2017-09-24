package com.gohool.retrofittutorial.retrofitresponse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tutorial for logging using httplogginginterceptor
        // Create instance of httplogginginterceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Create instance of okhttpclient
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()) // set default converter factory
                .build();

        Api api = retrofit.create(Api.class);
        api.sendRequestWithHeaders().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        // Tutorial for gson
        api.getCommentById(1).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                // you will get comment from request
                Comment comment = response.body();
                Log.d("RetrofitExample", comment.getBody());
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {

            }
        });

        api.getPostById(1).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                Log.d("RetrofitAssignment", response.body().getTitle());
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });

        api.getUserById("1").enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("RetrofitExample", response.body().getEmail());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }
}
