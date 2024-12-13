package me.socialnetwork.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import me.socialnetwork.post.Post;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface StandardAPI {

    final String url = "http://10.0.2.2:25565";

    Gson gson = new GsonBuilder().setDateFormat("yy MM dd HH:mm:ss").create();

    StandardAPI getService = new Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StandardAPI.class);


    @POST("/auth/register")
    Call<IUser> authLRegister(@Body RequestBody body);

    @POST("/auth/login")
    Call<IUser> authLogin(@Body RequestBody body);

    @GET("/post")
    Call<ArrayList<Post>> getPosts(@Header("Cookie") String cookie);

    @GET("/{id}")
    Call<IUser> getProfile(@Path(value = "id") String id, @Header("Cookie") String cookie);

    @GET("/like/{post_id}/like")
    Call<String> likePost(@Path(value = "post_id") String post_id, @Header("Cookie") String cookie);

    @GET("/like/{post_id}/unlike")
    Call<String> unlikePost(@Path(value = "post_id") String post_id, @Header("Cookie") String cookie);

    @GET("/comment/like/{comment_id}")
    Call<String> likeComment(@Path(value = "comment_id") String comment_id, @Header("Cookie") String cookie);

    @GET("/comment/unlike/comment_id")
    Call<String> unlikeComment(@Path(value = "comment_id") String comment_id, @Header("Cookie") String cookie);

    @GET("/post/{post_id}/repost")
    Call<String> repost(@Path(value = "post_id") String post_id, @Header("Cookie") String cookie);

    @GET("/post/{post_id}/unrepost")
    Call<String> unrepost(@Path(value = "post_id") String post_id, @Header("Cookie") String cookie);

    @POST("/query/{id}/follow")
    Call<ResponseBody> follow(@Path(value = "id") String post_id, @Header("Cookie") String cookie);

    @POST("/query/{id}/unfollow")
    Call<ResponseBody> unfollow(@Path(value = "id") String post_id, @Header("Cookie") String cookie);

    @POST("/post/upload")
    Call<ResponseBody> uploadPost(@Header("Cookie") String cookie, @Body Post post);

    @POST("/post/{post_id}/delete")
    Call<ResponseBody> deletePost(@Path(value = "post_id") String postID, @Header("Cookie") String cookie);

    @POST("/comment/{post_id}/add")
    Call<ResponseBody> addComment(@Header("Cookie") String cookie, @Body Post post);

    @POST("/comment/{post_id}/add")
    Call<ResponseBody> replyComment(@Header("Cookie") String cookie, @Body Post post);

    @POST("/profile/update")
    Call<IUser> updateProfile(@Header("Cookie") String cookie, @Body RequestBody body);
}


