package com.gohool.retrofittutorial.retrofitresponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by keyst on 23/09/2017.
 */

public interface Api {
    @Headers({"Content-Type: application/json", "User-Agent: RetrofitExample"})
    @GET("http://httpbin.org/get")
    Call<ResponseBody> sendRequestWithHeaders();

    @GET("comments/{id}")
    Call<Comment> getCommentById(@Path("id") int id);

    @GET("posts/{id}")
    Call<Post> getPostById(@Path("id") int id);

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") String id);
}
