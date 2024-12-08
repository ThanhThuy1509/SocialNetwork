package me.socialnetwork.post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import me.socialnetwork.R;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView name, username, content;
        ImageView avatar;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
            content = itemView.findViewById(R.id.postContent);
            avatar = itemView.findViewById(R.id.avatar);
        }
    }

    private Context context;
    private List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.name.setText(post.getName());
        holder.username.setText(post.getUsername());
        holder.content.setText(post.getContent());
        holder.avatar.setImageResource(post.getAvatarResource());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

}
