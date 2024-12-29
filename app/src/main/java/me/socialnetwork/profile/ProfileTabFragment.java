package me.socialnetwork.profile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.socialnetwork.LoginActivity;
import me.socialnetwork.R;
import me.socialnetwork.post.ItemDecoration;
import me.socialnetwork.post.ItemType;
import me.socialnetwork.post.Post;
import me.socialnetwork.post.PostAdapter;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public abstract class ProfileTabFragment extends Fragment {

    protected RecyclerView recyclerView;
    protected List<Post> postList;
    protected PostAdapter postAdapter;
    protected boolean isSleepQuery;
    protected String cookie;
    protected String userId;
    protected RequestBody requestBody;

    protected TextView title;

    public ProfileTabFragment(String userId) {
        this.userId = userId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_tab, container, false);

        cookie = LoginActivity.getData(requireContext(), "cookie");

        title = view.findViewById(R.id.title);

        Map<String, String> body = new HashMap<>();
        body.put("user_id", userId);
        requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new JSONObject(body).toString());

        isSleepQuery = false;
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        postList = new ArrayList<>();

        recyclerView.addItemDecoration(new ItemDecoration());
        recyclerView.setAdapter(postAdapter = new PostAdapter(requireContext(), postList, view, ItemType.POST,
                new PostAdapter.PostCallback() {
                    @Override
                    public void Like(boolean isLike, String likeCount) {

                    }

                    @Override
                    public void Comment(Map<String, String> body) {

                    }

                    @Override
                    public void Repost(boolean isRepost, String repostCount) {

                    }
                }
        ));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                assert linearLayoutManager != null;
                if (isSleepQuery) return;
                isSleepQuery = true;
                if (linearLayoutManager.findLastVisibleItemPosition() >= linearLayoutManager.getItemCount() - 3) {
                    request();
                }
                new Handler(Looper.getMainLooper()).postDelayed(() -> isSleepQuery = false, 5000);
            }
        });

        request();
        return view;
    }

    abstract void request();

}
