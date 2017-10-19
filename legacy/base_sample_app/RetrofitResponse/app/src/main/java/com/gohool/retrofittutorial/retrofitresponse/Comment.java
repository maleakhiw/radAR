package com.gohool.retrofittutorial.retrofitresponse;

import com.google.gson.annotations.SerializedName;

/**
 * Created by keyst on 23/09/2017.
 */

public class Comment {
    // value of serialized name need to be equal
    @SerializedName("postId")
    int postId;

    @SerializedName("id")
    int id;

    @SerializedName("name")
    String name;

    @SerializedName("email")
    String email;

    @SerializedName("body")
    String body;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
