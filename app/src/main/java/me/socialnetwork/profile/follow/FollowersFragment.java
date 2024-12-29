package me.socialnetwork.profile.follow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.socialnetwork.LoginActivity;
import me.socialnetwork.R;
import me.socialnetwork.api.StandardAPI;
import me.socialnetwork.api.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowersFragment extends Fragment {

    UserTagAdapter userTagAdapter;
    List<User> userList;

    String cookie;
    String userId;

    boolean isSleepQuery;
    UserTagAdapter.InfoChange infoChange;

    public FollowersFragment(String userId, UserTagAdapter.InfoChange infoChange) {
        this.userId = userId;
        this.infoChange = infoChange;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follower, container, false);
        cookie = LoginActivity.getData(requireContext(), "cookie");
        isSleepQuery = false;

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        userList = new ArrayList<>();
        recyclerView.setAdapter(userTagAdapter = new UserTagAdapter(userList, requireContext(), cookie, new UserTagAdapter.InfoChange(){
            @Override
            public void following(String following) {

                infoChange.following(following);
            }
        }));

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                assert linearLayoutManager != null;
                if (linearLayoutManager.findLastVisibleItemPosition() >= linearLayoutManager.getItemCount()-3 && !isSleepQuery) {
                    isSleepQuery = true;
                    request();
                    new Handler(Looper.getMainLooper()).postDelayed(() -> isSleepQuery = false, 5000);
                }
            }
        });

        request();

        return view;
    }

    public void request() {
        StandardAPI.getService.getFollowers(cookie, userId, userList.size()).enqueue(new Callback<ArrayList<User>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<ArrayList<User>> call, @NonNull Response<ArrayList<User>> response) {
                if (!response.isSuccessful()) return;
                userList.addAll(response.body());
                userTagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<User>> call, @NonNull Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    };

}