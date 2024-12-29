package me.socialnetwork.profile.follow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Base64;
import java.util.List;

import me.socialnetwork.R;
import me.socialnetwork.api.StandardAPI;
import me.socialnetwork.api.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserTagAdapter extends RecyclerView.Adapter<UserTagAdapter.UserTagViewHolder> {

    public static class InfoChange {
        public void followers(String followers) {};

        public void following(String following) {};
    }

    public static class UserTagViewHolder extends RecyclerView.ViewHolder {

        String id;
        ImageView avatar;
        TextView name;
        TextView username;
        Button followButton;

        public UserTagViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
            followButton = itemView.findViewById(R.id.follow_button);
        }
    }

    List<User> userList;
    Context context;
    String cookie;
    InfoChange infoChange;
    public UserTagAdapter(List<User> userList, Context context, String cookie, InfoChange infoChange) {
        this.userList = userList;
        this.context = context;
        this.cookie = cookie;
        this.infoChange = infoChange;
    }

    @NonNull
    @Override
    public UserTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_tag, parent, false);
        return new UserTagViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull UserTagViewHolder holder, int position) {
        User user = userList.get(position);
        holder.name.setText(user.getName());
        holder.username.setText(user.getUsername());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            byte[] imageBytes = Base64.getDecoder().decode(user.getAvatar());
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.avatar.setImageBitmap(imageBitmap);
        }
        followButtonBg(holder.followButton, user.isFollowing());

        holder.followButton.setOnClickListener(v -> {
            String url = String.format("/query/%s/%s", user.getId(), (user.isFollowing() == 1 ?"unfollow":"follow") );
            StandardAPI.getService.getQuery(url, cookie).enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (!response.isSuccessful()) return;
                    user.setFollowing((byte) (user.isFollowing() == 1?0:1));
                    followButtonBg(holder.followButton, user.isFollowing());
                    infoChange.following(response.body());
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {

                }
            });
        });
    }

    private void followButtonBg(Button button, byte status) {
        switch (status) {
            case 0:
                button.setText(context.getString(R.string.follow));
                button.setBackground(context.getDrawable(R.drawable.follow_button_unselect));
                button.setTextColor(context.getResources().getColorStateList(R.color.white));
                break;
            case 1:
                button.setText(context.getString(R.string.unfollow));
                button.setBackground(context.getDrawable(R.drawable.follow_button_selected));
                button.setTextColor(context.getResources().getColorStateList(R.color.black));
                break;
            default:
                button.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
