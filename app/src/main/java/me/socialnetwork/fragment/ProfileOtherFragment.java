package me.socialnetwork.fragment;

import static me.socialnetwork.LoginActivity.getData;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Base64;

import me.socialnetwork.R;
import me.socialnetwork.api.StandardAPI;
import me.socialnetwork.api.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileOtherFragment extends ProfileFragment {

    View view;

    public ProfileOtherFragment(String userId) {
        super(userId);
        this.userId = userId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        backButton.setVisibility(View.VISIBLE);
        drawerButton.setVisibility(View.GONE);
        backButton.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        String cookie = getData(requireContext(), "cookie");

        followButton.setOnClickListener(v -> {
            String url = String.format("/query/%s/%s", user.getId(), (user.isFollowing() == 1 ?"unfollow":"follow") );
            StandardAPI.getService.getQuery(url, cookie).enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (!response.isSuccessful()) return;
                    user.setFollowing((byte) (user.isFollowing() == 1?0:1));
                    followButtonBg(user.isFollowing());
                    user.setFollowers(response.body());
                    followersCount.setText(response.body());
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {

                }
            });
        });

        return view;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "UseCompatLoadingForColorStateLists"})
    private void followButtonBg(byte status) {
        if (user == null) return;
        switch (status) {
            case 0:
                followButton.setText(requireContext().getString(R.string.follow));
                followButton.setBackground(requireContext().getDrawable(R.drawable.follow_button_unselect));
                followButton.setTextColor(requireContext().getResources().getColorStateList(R.color.white));
                break;
            case 1:
                followButton.setText(requireContext().getString(R.string.unfollow));
                followButton.setBackground(requireContext().getDrawable(R.drawable.follow_button_selected));
                followButton.setTextColor(requireContext().getResources().getColorStateList(R.color.black));
                break;
            default:
                break;
        }

    }

    @Override
    public void request() {
        StandardAPI.getService.getProfile(userId, getData(requireContext(), "cookie")).enqueue(new Callback<User>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                assert response.body() != null;
                user = response.body();
                nameProfile.setText(response.body().getName());
                usernameProfile.setText(response.body().getUsername());

                // Base64 to image
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    byte[] imgBytes = Base64.getDecoder().decode(response.body().getAvatar());
                    Bitmap img = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);

                    avatarProfile.setImageBitmap(img);

                }

                followersCount.setText(response.body().getFollowers());
                followingCount.setText(response.body().getFollowing());
                if (user.isFollowing() != 2) {
                    editProfileButton.setVisibility(View.GONE);
                    followButton.setVisibility(View.VISIBLE);
                    followButtonBg(user.isFollowing());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {

            }
        });

    }
}
