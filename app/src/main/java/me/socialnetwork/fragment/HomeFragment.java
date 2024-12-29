package me.socialnetwork.fragment;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static me.socialnetwork.LoginActivity.getData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import me.socialnetwork.R;
import me.socialnetwork.Values;
import me.socialnetwork.api.StandardAPI;
import me.socialnetwork.post.ItemDecoration;
import me.socialnetwork.post.ItemType;
import me.socialnetwork.post.Post;
import me.socialnetwork.post.PostAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private String cookie;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private boolean isSleepQuery;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.refresh);

        cookie = getData(requireContext(), "cookie");

        Log.i("HomeFragment", "onCreateView");

        recyclerView = view.findViewById(R.id.recyclerViewHome);
        recyclerView.addItemDecoration(new ItemDecoration());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        isSleepQuery = false;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                assert linearLayoutManager != null;
                Log.i("RV on scroll", linearLayoutManager.findLastVisibleItemPosition()+ " " + linearLayoutManager.getItemCount());
                if (isSleepQuery) return;
                isSleepQuery = true;
                if (linearLayoutManager.findLastVisibleItemPosition() >= linearLayoutManager.getItemCount() - 3) {
                    StandardAPI.getService.getPosts(cookie, linearLayoutManager.getItemCount()).enqueue(new Callback<ArrayList<Post>>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onResponse(@NonNull Call<ArrayList<Post>> call, @NonNull Response<ArrayList<Post>> response) {
                            if (!response.isSuccessful() || Objects.requireNonNull(response.body()).isEmpty())
                                return;
                            postList.addAll(response.body());
                            postAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onFailure(@NonNull Call<ArrayList<Post>> call, @NonNull Throwable throwable) {

                        }
                    });
                }
                new Handler(Looper.getMainLooper()).postDelayed(() -> isSleepQuery = false, 5000);
            }
        });

        postList = new ArrayList<>();
        // Add some sample posts

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postList = new ArrayList<>();
                request();
                refreshLayout.setRefreshing(false);
            }
        });
        request();
        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatButton);

        floatingActionButton.setOnClickListener(v -> {
            composeWindow(view.getContext(), view);
        });

        return view;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "MissingInflatedId"})
    public void composeWindow(Context context, View v) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.add_post, null);

        ImageView closeButton = view.findViewById(R.id.composeCloseButton);
        EditText composeContent = view.findViewById(R.id.composeContent);
        Button uploadPost = view.findViewById(R.id.uploadPost);
        ImageView avatar = view.findViewById(R.id.avatar_uploadPost);


        int width = v.getWidth();
        int height = (int) (Resources.getSystem().getDisplayMetrics().heightPixels * Values.POPUPWINDOW_HEIGHT);

        Drawable background = ContextCompat.getDrawable(context, R.drawable.popupwindow_background);
        view.setBackgroundDrawable(background);

        PopupWindow popupWindow = new PopupWindow(view, width, height, true);
        popupWindow.setBackgroundDrawable(background);
        popupWindow.setAnimationStyle(R.style.Animation);
        popupWindow.showAtLocation(v.findViewById(R.id.homeFragment), Gravity.BOTTOM, 0, 0);
        dimBehind(popupWindow);

        @SuppressLint({"NewApi", "LocalSuppress"}) byte[] imageBytes = Base64.getDecoder().decode(getData(view.getContext(), "avatar"));
        avatar.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));

        uploadPost.setOnClickListener(v1 -> {
            Post post = new Post(composeContent.getText().toString());
            Log.i("Content", post.getContent());
            StandardAPI.getService.uploadPost(getData(view.getContext(), "cookie"), post).enqueue(new Callback<Post>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                    if (response.isSuccessful()) {
                        popupWindow.dismiss();
                        postList.add(0, response.body());
                        postAdapter.notifyItemInserted(postList.size() - 1);
                        postAdapter.notifyDataSetChanged();
                    } else {
                        CustomDialogClass customDialogClass = new CustomDialogClass((Activity) view.getContext(), popupWindow, "Tải lên thất bại.");
                        customDialogClass.show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Post> call, @NonNull Throwable throwable) {
                    throwable.printStackTrace();
                    CustomDialogClass customDialogClass = new CustomDialogClass((Activity) view.getContext(), popupWindow, "Tải lên thất bại.");
                    customDialogClass.show();
                }
            });
        });

        closeButton.setOnClickListener(v1 -> {
            if (!composeContent.getText().toString().isEmpty()) {
                CustomDialogClass c = new CustomDialogClass((Activity) view.getContext(), popupWindow);
                c.show();
            } else {
                popupWindow.dismiss();
            }
        });

    }

    /*
    https://stackoverflow.com/questions/35874001/dim-the-background-using-popupwindow-in-android
    */
    public static void dimBehind(PopupWindow popupWindow) {
        View view = getView(popupWindow);

        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) view.getLayoutParams();
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        params.dimAmount = 0.3f;
        wm.updateViewLayout(view, params);
    }

    private static View getView(PopupWindow popupWindow) {
        View view;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                view = (View) popupWindow.getContentView().getParent();
            } else {
                view = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                view = (View) popupWindow.getContentView().getParent();
            }
        }
        return view;
    }

    public static class CustomDialogClass extends Dialog {

        private final Activity c;

        private final PopupWindow popupWindow;
//        public Dialog d;
        private final String alert;

        public CustomDialogClass(Activity a, PopupWindow popupWindow, String alert) {
            super(a);
            // TODO Auto-generated constructor stub
            this.popupWindow = popupWindow;
            this.c = a;
            this.alert = alert;
        }

        public CustomDialogClass(Activity a, PopupWindow popupWindow) {
            super(a);
            // TODO Auto-generated constructor stub
            this.popupWindow = popupWindow;
            this.c = a;
            this.alert = "";
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.close_alert);
            Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView alertText = findViewById(R.id.alertText);

            if (!alert.isEmpty()) {
                alertText.setText(alert);
            }
            @SuppressLint("CutPasteId") TextView accept = findViewById(R.id.close_alert_acceptButton),
                    deny = findViewById(R.id.close_alert_denyButton);

            deny.setOnClickListener(v -> dismiss());

            accept.setOnClickListener(v -> {
                dismiss();
                popupWindow.dismiss();
            });
        }
    }

    public void request() {
        StandardAPI.getService.getPosts(cookie, 0).enqueue(new Callback<ArrayList<Post>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Post>> call, @NonNull Response<ArrayList<Post>> response) {
                if (!response.isSuccessful()) return;
                postList = response.body();
                postAdapter = new PostAdapter(getContext(), postList, view, ItemType.POST, new PostAdapter.PostCallback() {
                    @Override
                    public void Like(boolean isLike, String likeCount) {

                    }

                    @Override
                    public void Comment(Map<String, String> body) {

                    }

                    @Override
                    public void Repost(boolean isRepost, String repostCount) {

                    }
                });
                recyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Post>> call, @NonNull Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}