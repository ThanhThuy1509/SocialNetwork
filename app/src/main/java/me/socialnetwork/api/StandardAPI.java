package me.socialnetwork.api;

import java.util.ArrayList;

import me.socialnetwork.post.Post;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface StandardAPI {

    String url = "http://10.0.2.2:25565";

    StandardAPI getService = new Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StandardAPI.class);


    @POST("/auth/register")
    Call<User> authLRegister(@Body RequestBody body);

    @POST("/auth/login")
    Call<User> authLogin(@Body RequestBody body);
    @GET("/auth/login")
    Call<User> authLoginCookie(@Header("Cookie") String cookie);

    @GET("/post")
    Call<ArrayList<Post>> getPosts(@Header("Cookie") String cookie, @Query("skip") int skip);

    @POST("/post/")
    Call<ArrayList<Post>> getProfilePosts(@Header("Cookie") String cookie, @Body RequestBody body, @Query("profile") String type, @Query("skip") int skip);

    @GET("/post/{id}")
    Call<Post> getPost(@Path(value = "id") String id, @Header("Cookie") String cookie);

    @GET("/{id}")
    Call<User> getProfile(@Path(value = "id") String id, @Header("Cookie") String cookie);

    @GET
    Call<String> getQuery(@Url String url, @Header("Cookie") String cookie);

    @GET("/comment/{post_id}")
    Call<ArrayList<Post>> loadComment(@Path(value = "post_id") String comment_id, @Header("Cookie") String cookie);

    @GET("/comment/{post_id}/{parent_comment_id}")
    Call<ArrayList<Post>> loadChildComment(@Path(value = "post_id") String post_id, @Path(value = "parent_comment_id") String parent_comment_id, @Header("Cookie") String cookie);

    @GET("/query/{id}/follow")
    Call<ResponseBody> follow(@Path(value = "id") String userId, @Header("Cookie") String cookie);

    @GET("/query/{id}/unfollow")
    Call<ResponseBody> unfollow(@Path(value = "id") String userId, @Header("Cookie") String cookie);

    @POST("/post/upload")
    Call<Post> uploadPost(@Header("Cookie") String cookie, @Body Post post);

    @POST("/post/{post_id}/delete")
    Call<ResponseBody> deletePost(@Path(value = "post_id") String postID, @Header("Cookie") String cookie);

    @POST("/comment/{post_id}/add")
    Call<Post> addComment(@Path(value = "post_id") String postID, @Header("Cookie") String cookie, @Body RequestBody body);

    @POST("/profile/update")
    Call<User> updateProfile(@Header("Cookie") String cookie, @Body RequestBody body);

    @GET("/{user_id}/followers")
    Call<ArrayList<User>> getFollowers(@Header("Cookie") String cookie, @Path(value = "user_id") String userId, @Query("skip") int skip);

    @GET("/{user_id}/following")
    Call<ArrayList<User>> getFollowing(@Header("Cookie") String cookie, @Path(value = "user_id") String userId, @Query("skip") int skip);
}


