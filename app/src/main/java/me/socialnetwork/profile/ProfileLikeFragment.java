package me.socialnetwork.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import me.socialnetwork.api.StandardAPI;
import me.socialnetwork.post.Post;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileLikeFragment extends ProfileTabFragment {

    public ProfileLikeFragment(String userId) {
        super(userId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void request() {
        StandardAPI.getService.getProfilePosts(cookie, requestBody, "likes", postList.size()).enqueue(new Callback<ArrayList<Post>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<ArrayList<Post>> call, @NonNull Response<ArrayList<Post>> response) {
                if (!response.isSuccessful()) return;
                postList.addAll(response.body());
                title.setText(!postList.isEmpty() ? "" : "Không có bài thích nào.");
                postAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(@NonNull Call<ArrayList<Post>> call, @NonNull Throwable throwable) {

            }
        });
    }


}
