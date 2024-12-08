package me.socialnetwork.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import me.socialnetwork.R;

public class PostsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

//        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewPosts);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        // Tạo danh sách bài đăng mẫu
//        List<String> samplePosts = new ArrayList<>();
//        samplePosts.add("This is my first post!");
//        samplePosts.add("Loving the new features in this app.");
//        samplePosts.add("Another random post for testing.");
//        samplePosts.add("Keep scrolling to see more!");
//        samplePosts.add("End of the sample posts!");
//
//        // Gắn adapter cho RecyclerView
//        PostAdapter adapter = new PostAdapter(getContext(), samplePosts);
//        recyclerView.setAdapter(adapter);

        return view;
    }
}
