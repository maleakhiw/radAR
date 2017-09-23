package com.gohool.retrofittutorial.retrofittutorial;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by keyst on 23/09/2017.
 */

public interface Api {
    @GET("/posts")
    Call<ResponseBody> getPosts();

    @GET("/users")
    Call<ResponseBody> getUsers();

    // Post Annotation to submit users
    @POST("/users")
    Call<ResponseBody> postUser(@Body RequestBody requestBody);

    @POST("/posts")
    Call<ResponseBody> postPost(@Body RequestBody requestBody);

    @GET("/posts")
    Call<ResponseBody> getPostsByUserId(@Query("userId") int userId);

    @GET("/comments")
    Call<ResponseBody> getCommentsById(@Query("postId") int postId);

    @GET("/posts")
    Call<ResponseBody> getPostsByIds(@Query("id") List<Integer> ids);

    @GET("/posts")
    Call<ResponseBody> getPostByParams(@QueryMap Map<String, String> params);

    @GET("/posts/{id}")
    Call<ResponseBody> getPostById(@Path("id") String id); // the id will replace {id}

    @GET("/users/{id}")
    Call<ResponseBody> getUserById(@Path("id") int id);

    // Set requests to url which is different from base url
    @GET("https://api.ipify.org")
    Call<ResponseBody> getIp();

    @GET
    Call<ResponseBody> sendRequest(@Url String url);

    // sending formurl encoded
    @FormUrlEncoded
    @POST("/posts")
    Call<ResponseBody> postingPost(@Field("id") String id, @Field("userId") String userId,
                                   @Field("title") String title, @Field("body") String body);
}
