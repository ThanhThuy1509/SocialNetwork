package me.socialnetwork.fragment;

import static me.socialnetwork.LoginActivity.getData;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import me.socialnetwork.R;
import me.socialnetwork.api.StandardAPI;
import me.socialnetwork.post.ItemDecoration;
import me.socialnetwork.post.ItemType;
import me.socialnetwork.post.Post;
import me.socialnetwork.post.PostAdapter;
import me.socialnetwork.post.Status;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostFragment extends Fragment {

    public interface StatusCallback {
        void status(Status status);
    }

    private final Post post;
    private View view;
    private RecyclerView recyclerView;
    private List<Post> commentList;
    private PostAdapter postAdapter;
    private final PostAdapter.PostCallback postCallback;

    private final StatusCallback statusCallback;

    public PostFragment(Post post, PostAdapter.PostCallback postCallback, StatusCallback statusCallback) {
        this.post = post;
        this.postCallback = postCallback;
        this.statusCallback = statusCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_post, container, false);


        ImageView backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        RecyclerView recyclerView = view.findViewById(R.id.post);
        NestedScrollView nestedScrollView = view.findViewById(R.id.nestedScrollView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(new PostAdapter(view.getContext(), new ArrayList<>(Collections.singletonList(post)), view, ItemType.POST_2, new PostAdapter.PostCallback() {
            @Override
            public void Like(boolean isLike, String likeCount) {
                postCallback.Like(isLike, likeCount);
            }

            @Override
            public void Comment(Map<String, String> body) {
                postCallback.Comment(body);
            }

            @Override
            public void Repost(boolean isRepost, String repostCount) {
                postCallback.Repost(isRepost, repostCount);
            }
        }));

        commentsLoad();

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (oldScrollY < scrollY && statusCallback != null) {
                statusCallback.status(Status.SCROLL_DOWN);
            }
        });

        return view;
    }

    private void commentsLoad() {
        recyclerView = view.findViewById(R.id.comment_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.addItemDecoration(new ItemDecoration());

        StandardAPI.getService.loadComment(post.getId(), getData(view.getContext(), "cookie")).enqueue(new Callback<ArrayList<Post>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Post>> call, @NonNull Response<ArrayList<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recyclerView.setAdapter(postAdapter = new PostAdapter(view.getContext(), commentList = response.body(), view, ItemType.COMMENT, new PostAdapter.PostCallback() {
                        @Override
                        public void Like(boolean isLike, String likeCount) {
                        }

                        @Override
                        public void Comment(Map<String, String> body) {
                            postCallback.Comment(body);
                        }

                        @Override
                        public void Repost(boolean isRepost, String repostCount) {
                        }
                    }));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Post>> call, @NonNull Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    public void addComment(Post post) {
        if (post == null || postAdapter == null) return;
        if (post.getParentCommentId() != null && !post.getParentCommentId().isEmpty()) {
            postAdapter.addChildComment(post);
            return;
        }
        commentList.add(0, post);
        postAdapter.notifyDataSetChanged();
    }

}