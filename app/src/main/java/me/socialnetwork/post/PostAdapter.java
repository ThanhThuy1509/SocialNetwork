package me.socialnetwork.post;

import android.annotation.SuppressLint;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import me.socialnetwork.R;
import me.socialnetwork.api.StandardAPI;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static me.socialnetwork.LoginActivity.getData;
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView username;
        TextView createTime;
        TextView content;
        TextView likeCount;
        TextView repliedCount;
        ImageView repostButton;
        TextView repostCount;
        TextView followButton;
        ImageView avatar, likeButton;
        ImageView moreButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
            createTime = itemView.findViewById(R.id.createTime);

            content = itemView.findViewById(R.id.postContent);
            avatar = itemView.findViewById(R.id.avatar);
            likeCount = itemView.findViewById(R.id.likeCount);
            repliedCount = itemView.findViewById(R.id.repliedCount);
            repostButton = itemView.findViewById(R.id.repostButton);
            repostCount = itemView.findViewById(R.id.repostCount);

            likeButton = itemView.findViewById(R.id.likeButton);
            followButton = itemView.findViewById(R.id.followButton);
            moreButton = itemView.findViewById(R.id.postMoreButton);


        }



    }

    private final Context context;
    private final List<Post> postList;

    private final View parentView;

    public PostAdapter(Context context, List<Post> postList, View parentView) {
        this.context = context;
        this.postList = postList;
        this.parentView = parentView;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void updatePost(PostViewHolder holder, Post post) {
        holder.name.setText(post.getName());
        holder.username.setText("@"+post.getUsername());
        holder.content.setText(post.getContent());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            byte[] imgBytes = Base64.getDecoder().decode(post.getAvatar());
            holder.avatar.setImageBitmap(BitmapFactory.decodeByteArray(imgBytes, 0 , imgBytes.length));
        }
        holder.followButton.setVisibility(post.isFollowing() ? View.INVISIBLE: View.VISIBLE);

        holder.likeCount.setText(post.getLikeCount().compareTo("0") > 0? post.getLikeCount(): "");
        holder.repliedCount.setText(post.getRepliedCount().compareTo("0") > 0? post.getRepliedCount(): "");
        holder.repostCount.setText(post.getRepostCount().compareTo("0") > 0? post.getRepostCount(): "");

        if (post.getLiked()) {
            holder.likeButton.setImageDrawable(context.getDrawable(R.drawable.barcelona_heart_filled_18));
            holder.likeButton.setColorFilter(Color.argb(255, 255, 20, 20));
            holder.likeCount.setTextColor(Color.argb(255, 255, 20, 20));
        } else {
            holder.likeButton.setImageDrawable(context.getDrawable(R.drawable.barcelona_heart_outline_18));
            holder.likeButton.setColorFilter(Color.argb(255, 0, 0, 0));
            holder.likeCount.setTextColor(Color.argb(255, 0, 0, 0));
        }
        if (post.getReposted()) {
            holder.repostButton.setImageDrawable(context.getDrawable(R.drawable.barcelona_reposted_outline_18));
            holder.repostButton.setColorFilter(Color.argb(255, 20, 20, 255));
            holder.repostCount.setTextColor(Color.argb(255, 20, 20, 255));
        } else {
            holder.repostButton.setImageDrawable(context.getDrawable(R.drawable.barcelona_repost_outline_18));
            holder.repostButton.setColorFilter(Color.argb(255, 0, 0, 0));
            holder.repliedCount.setTextColor(Color.argb(255, 0, 0, 0));
        }
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        Post post = postList.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); //UTC
            // Devices time
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(post.getCreateTime())
                    .withZoneSameInstant(ZonedDateTime.now().getZone());
            LocalDateTime postCreateTime = zonedDateTime.toLocalDateTime();
            LocalDateTime localDateTime = LocalDateTime.now();
            Log.i("Time Post", postCreateTime.toString());
            Log.i("Time Now", localDateTime.toString());

            if (postCreateTime.isBefore(localDateTime.minusWeeks(1))) {
                holder.createTime.setText(postCreateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")));
            } else if (postCreateTime.isBefore(localDateTime.minusDays(1))) {
                holder.createTime.setText(Duration.between(postCreateTime, localDateTime).toDays() + parentView.getResources().getString(R.string.days));
            } else if (postCreateTime.isBefore(localDateTime.minusHours(1))) {
                holder.createTime.setText(Duration.between(postCreateTime, localDateTime).toHours() + parentView.getResources().getString(R.string.hours));
            } else if (postCreateTime.isBefore(localDateTime.minusMinutes(1))){
                holder.createTime.setText(Duration.between(postCreateTime, localDateTime).toMinutes() + parentView.getResources().getString(R.string.minutes));
            } else {
                holder.createTime.setText(localDateTime.getSecond() - postCreateTime.getSecond() + parentView.getResources().getString(R.string.seconds));
            }
        }
//        holder.createTime
        updatePost(holder, post);
        holder.likeButton.setOnClickListener(v -> {
            if (post.getLiked()) {
                StandardAPI.getService.unlikePost(post.getId(), getData(context, "cookie")).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        post.setLike(!response.isSuccessful());
                        post.setLikeCount(response.body());
                        updatePost(holder, post);
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {

                    }
                });
            } else {
                StandardAPI.getService.likePost(post.getId(), getData(context, "cookie")).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        post.setLike(response.isSuccessful());
                        post.setLikeCount(response.body());
                        updatePost(holder, post);
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            }
        });

        holder.repostButton.setOnClickListener(v -> {
            if (post.getReposted()) {
                StandardAPI.getService.unrepost(post.getId(), getData(context, "cookie")).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        post.setReposted(!response.isSuccessful());
                        post.setRepostCount(response.body());
                        updatePost(holder, post);
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {

                    }
                });
            } else {
                StandardAPI.getService.repost(post.getId(), getData(context, "cookie")).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        post.setReposted(response.isSuccessful());
                        post.setRepostCount(response.body());
                        updatePost(holder, post);
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            }
        });

        holder.followButton.setOnClickListener(v -> StandardAPI.getService.follow(post.getUserID(), getData(context, "cookie")).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.i("Follow button", String.valueOf(response.isSuccessful()));
                post.setFollowing(response.isSuccessful());
                updatePost(holder, post);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                throwable.printStackTrace();
            }
        }));

        holder.moreButton.setOnClickListener(v -> showPopupMenu(v, post, holder));

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //https://stackoverflow.com/questions/62623784/how-to-set-rounded-background-for-list-popup-window
    @SuppressLint("NonConstantResourceId")
    private void showPopupMenu(View view, Post post, PostViewHolder holder) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.recyclerview_post_menu);
        if (Objects.equals(post.getUserID(), getData(parentView.getContext(), "id"))) {
            popupMenu.getMenu().findItem(R.id.unfollowButton).setVisible(false);
        } else {
            popupMenu.getMenu().findItem(R.id.deleteButton).setVisible(false);
        }
        if (!post.isFollowing()) {
            popupMenu.getMenu().findItem(R.id.unfollowButton).setVisible(false);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.unfollowButton:
                    Log.i("Unfollow", "Unfollow");
                    StandardAPI.getService.unfollow(post.getUserID(), getData(context, "cookie")).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                            Log.i("Follow button", String.valueOf(response.isSuccessful()));
                            post.setFollowing(response.isSuccessful());
                            post.setFollowing(false);
                            updatePost(holder, post);
                            popupMenu.getMenu().findItem(R.id.unfollowButton).setVisible(false);
                        }

                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
                    return true;
                case R.id.deleteButton:
                    Log.i("Delete", "Delete");
                    StandardAPI.getService.deletePost(post.getId(), getData(parentView.getContext(), "cookie")).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                            Log.i("Delete post", response.isSuccessful()?"Success": "Fail");
                        }

                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
                    return true;
//                case R.id.removeButton:
//                    Log.i("Remove", "Remove");
//                    return true;
                default:
                    return false;
            }
        });

        popupMenu.show();
    }

}
