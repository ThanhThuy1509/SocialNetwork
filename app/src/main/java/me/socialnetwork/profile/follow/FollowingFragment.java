package me.socialnetwork.profile.follow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;

import me.socialnetwork.api.StandardAPI;
import me.socialnetwork.api.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingFragment extends FollowersFragment {

    public FollowingFragment(String userId, UserTagAdapter.InfoChange infoChange) {
        super(userId, infoChange);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void request() {
        StandardAPI.getService.getFollowing(cookie, userId, userList.size()).enqueue(new Callback<ArrayList<User>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<ArrayList<User>> call, @NonNull Response<ArrayList<User>> response) {
                if (!response.isSuccessful()) return;
                userList.addAll(response.body());
                userTagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<User>> call, @NonNull Throwable throwable) {

            }
        });
    }
}
