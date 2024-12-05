package me.socialnetwork;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private List<Post> postList;

    public HomeAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.username.setText(post.getUsername());
        holder.content.setText(post.getContent());
        holder.avatar.setImageResource(post.getAvatarResource());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {

        TextView username, content;
        ImageView avatar;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            content = itemView.findViewById(R.id.postContent);
            avatar = itemView.findViewById(R.id.avatar);
        }
    }
}
