package me.socialnetwork.post;

import static me.socialnetwork.LoginActivity.getData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import me.socialnetwork.PostActivity;
import me.socialnetwork.ProfileActivity;
import me.socialnetwork.R;
import me.socialnetwork.api.StandardAPI;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public interface CommentCallback {
        void Comment(Map<String, String> body);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView username;
        TextView createTime;
        TextView content;
        ImageView likeButton;
        TextView likeCount;
        ImageView replyButton;
        TextView repliedCount;
        ImageView repostButton;
        TextView repostCount;
        TextView followButton;
        ImageView avatar;
        ImageView moreButton;
        LinearLayout layout;

        // Reply layout
        RecyclerView replies;
        TextView loadRepliesButton;
        TextView replyToId;
        String id;
        PostAdapter postAdapter;
        List<Post> listChildComment;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
            createTime = itemView.findViewById(R.id.createTime);
            content = itemView.findViewById(R.id.postContent);

            likeButton = itemView.findViewById(R.id.likeButton);
            likeCount = itemView.findViewById(R.id.likeCount);

            replyButton = itemView.findViewById(R.id.reply_button);
            repliedCount = itemView.findViewById(R.id.repliedCount);

            repostButton = itemView.findViewById(R.id.repostButton);
            repostCount = itemView.findViewById(R.id.repostCount);

            followButton = itemView.findViewById(R.id.followButton);
            moreButton = itemView.findViewById(R.id.postMoreButton);

            layout = itemView.findViewById(R.id.post);

            // Reply layout
            if (itemView.getRootView().getId() == R.id.item_reply) {
                replies = itemView.findViewById(R.id.replies);
                loadRepliesButton = itemView.findViewById(R.id.load_replies_button);
                listChildComment = new ArrayList<>();
            }

            if (itemView.getRootView().getId() == R.id.item_reply_2) {
                replyToId = itemView.findViewById(R.id.reply_to_id);
            }
        }
    }

    private final Context context;
    private final List<Post> postList;
    private final View parentView;
    private final ItemType itemType;

    private CommentCallback commentCallback;

    private String cookie;

    public PostAdapter(Context context, List<Post> postList, View parentView, ItemType itemType, CommentCallback commentCallback) {
        this.context = context;
        this.postList = postList;
        this.parentView = parentView;
        this.itemType = itemType;
        this.commentCallback = commentCallback;
        this.cookie = getData(context, "cookie");
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(itemType.getLayout(), parent, false);
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
            holder.repostCount.setTextColor(Color.argb(255, 0, 0, 0));
        }

        // reply layout
        if (itemType == ItemType.COMMENT) {
            if (post.getRepliedCount().compareTo("0") <= 0) {
                holder.loadRepliesButton.setVisibility(View.GONE);
            } else {
                holder.loadRepliesButton.setText(String.format(context.getResources().getString(R.string.view_n_replies), post.getRepliedCount()));
                holder.loadRepliesButton.setOnClickListener(v -> {

                });
            }
        }

    }

    private List<PostViewHolder> holderList = new ArrayList<>();

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        this.holderList.add(holder);
        Post post = postList.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); //UTC
            // Devices time
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(post.getCreateTime())
                    .withZoneSameInstant(ZonedDateTime.now().getZone());
            LocalDateTime postCreateTime = zonedDateTime.toLocalDateTime();
            LocalDateTime localDateTime = LocalDateTime.now();

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

        holder.id = post.getId();

        holder.likeButton.setOnClickListener(v -> {
            String path = post.isLike() ? itemType.unlike(post.getId()): itemType.like(post.getId());
            StandardAPI.getService.getQuery(path, getData(context, "cookie")).enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (!response.isSuccessful()) return;
                    post.setLike(!post.isLike());
                    post.setLikeCount(response.body());
                    updatePost(holder, post);
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {

                }
            });
        });

        holder.replyButton.setOnClickListener(v -> {
            Map<String , String> body = new HashMap<>();
            if (itemType == ItemType.POST) {
                Intent i = new Intent(context, PostActivity.class);
                i.putExtra("post_id", post.getId());
                i.putExtra("reply", true);
                context.startActivity(i);
            } else if (itemType == ItemType.POST_2) {
                commentCallback.Comment(body);
            } else if (itemType == ItemType.COMMENT) {
                body.put("username", post.getUsername());
                body.put("parent_comment_id", post.getId());
                body.put("replied_to_comment_id", post.getId());
                commentCallback.Comment(body);
            } else if (itemType == ItemType.REPLY_TO_COMMENT) {
                body.put("username", post.getUsername());
                body.put("parent_comment_id", post.getParentCommentId());
                body.put("replied_to_comment_id", post.getId());
                commentCallback.Comment(body);
            }
        });

        holder.repostButton.setOnClickListener(v -> {
            String path = post.getReposted() ? itemType.unrepost(post.getId()): itemType.repost(post.getId());
            StandardAPI.getService.getQuery(path, getData(context, "cookie")).enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (!response.isSuccessful()) return;
                    post.setReposted(!post.getReposted());
                    post.setRepostCount(response.body());
                    updatePost(holder, post);
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {

                }
            });
        });

        holder.followButton.setOnClickListener(v -> StandardAPI.getService.follow(post.getUserID(), cookie).enqueue(new Callback<ResponseBody>() {
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

        holder.moreButton.setOnClickListener(v -> showPopupMenu(v, post, holder, position));

        holder.layout.setOnClickListener(v -> {
            if (itemType != ItemType.POST) return;
            Intent i = new Intent(context, PostActivity.class);
            i.putExtra("post_id", post.getId());
            context.startActivity(i);
        });

        if (itemType == ItemType.COMMENT) {
            holder.loadRepliesButton.setOnClickListener(v -> {
                StandardAPI.getService.loadChildComment(post.getPostId(), post.getId(), cookie).enqueue(new Callback<ArrayList<Post>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Post>> call, Response<ArrayList<Post>> response) {
                        if (!response.isSuccessful()) return;
                        holder.replies.setLayoutManager(new LinearLayoutManager(context));
                        holder.replies.addItemDecoration(new ItemDecoration());

                        holder.replies.setAdapter(holder.postAdapter = new PostAdapter(context, holder.listChildComment = response.body(), parentView, ItemType.REPLY_TO_COMMENT, new CommentCallback() {
                            @Override
                            public void Comment(Map<String, String> body) {
                                commentCallback.Comment(body);
                            }
                        }));
                        holder.loadRepliesButton.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Post>> call, Throwable throwable) {

                    }
                });
            });
        }

        if (itemType == ItemType.REPLY_TO_COMMENT) {
            holder.replyToId.setText("@"+post.getRepliedToUsername());
        }

        holder.name.setOnClickListener(v -> {
            Intent i = new Intent(context, ProfileActivity.class);
            i.putExtra("user_id", post.getUserID());
            context.startActivity(i);
        });

        holder.username.setOnClickListener(v -> {
            Intent i = new Intent(context, ProfileActivity.class);
            i.putExtra("user_id", post.getUserID());
            context.startActivity(i);
        });

        holder.avatar.setOnClickListener(v -> {
            Intent i = new Intent(context, ProfileActivity.class);
            i.putExtra("user_id", post.getUserID());
            context.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //https://stackoverflow.com/questions/62623784/how-to-set-rounded-background-for-list-popup-window
    @SuppressLint("NonConstantResourceId")
    private void showPopupMenu(View view, Post post, PostViewHolder holder, int position) {
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
                            if (response.isSuccessful()) {
                                postList.remove(position);
                                notifyDataSetChanged();
                            }
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

    public void addChildComment(Post post) {
        for (PostViewHolder holder: holderList) {
            if (post.getParentCommentId().equals(holder.id)) {
                if (holder.listChildComment.isEmpty()) {
                    holder.replies.setVisibility(View.VISIBLE);
                    holder.listChildComment.add(post);
                    holder.replies.setLayoutManager(new LinearLayoutManager(context));
                    holder.replies.setAdapter(holder.postAdapter = new PostAdapter(context, holder.listChildComment, parentView, ItemType.REPLY_TO_COMMENT, new CommentCallback() {
                        @Override
                        public void Comment(Map<String, String> body) {
                            commentCallback.Comment(body);
                        }
                    }));
                    return;
                }

                holder.listChildComment.add(post);
                holder.postAdapter.notifyDataSetChanged();

                return;
            }
        }
    }

}
