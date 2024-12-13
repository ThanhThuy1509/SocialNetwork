package me.socialnetwork.fragment;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import me.socialnetwork.api.StandardAPI;
import me.socialnetwork.post.Post;
import me.socialnetwork.post.PostAdapter;
import me.socialnetwork.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static me.socialnetwork.LoginActivity.getData;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        // Add some sample posts


        StandardAPI.getService.getPosts(getData(requireContext(), "cookie")).enqueue(new Callback<ArrayList<Post>>() {
            @Override
            public void onResponse(Call<ArrayList<Post>> call, Response<ArrayList<Post>> response) {
                Log.i("Posts", new Gson().toJson(response.body()));
                recyclerView.setAdapter(new PostAdapter(getContext(), response.body(), view));
            }

            @Override
            public void onFailure(Call<ArrayList<Post>> call, Throwable throwable) {

            }
        });

//        postList.add(new Post("Hai Duong", "@_h.duong", "This is a post content", R.drawable.avt));
//        postList.add(new Post("Thanh Thuy", "@thanh_h2o", "Another post content", R.drawable.avt));
//
//        postAdapter = new PostAdapter(getContext(), postList);
//        recyclerView.setAdapter(postAdapter);

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
        int height = (int) (Resources.getSystem().getDisplayMetrics().heightPixels * 0.9);

        Drawable background = ContextCompat.getDrawable(context, R.drawable.popupwindow_background);
        view.setBackgroundDrawable(background);

        PopupWindow popupWindow = new PopupWindow(view, width, height, true);
        popupWindow.setBackgroundDrawable(background);
        popupWindow.showAtLocation(v.findViewById(R.id.homeFragment), Gravity.BOTTOM, 0, 0);
        dimBehind(popupWindow);

        @SuppressLint({"NewApi", "LocalSuppress"}) byte[] imageBytes = Base64.getDecoder().decode(getData(view.getContext(), "avatar"));
        avatar.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));

        uploadPost.setOnClickListener(v1 -> {
            Post post = new Post(composeContent.getText().toString());
            Log.i("Content", post.getContent());
            StandardAPI.getService.uploadPost(getData(view.getContext(), "cookie"), post).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        popupWindow.dismiss();
                    } else {
                        CustomDialogClass customDialogClass = new CustomDialogClass((Activity) view.getContext(), popupWindow, "Tải lên thất bại.");
                        customDialogClass.show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
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
        /*
        Get View VERSION
         */
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

        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) view.getLayoutParams();
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        params.dimAmount = 0.3f;
        wm.updateViewLayout(view, params);
    }

    public static class CustomDialogClass extends Dialog {

        private Activity c;

        private PopupWindow popupWindow;
//        public Dialog d;
        private String alert;

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
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView alretText = findViewById(R.id.alertText);

            if (!alert.isEmpty()) {
                alretText.setText(alert);
            }
            @SuppressLint("CutPasteId") TextView accept = findViewById(R.id.close_alert_acceptButton),
                    deny = findViewById(R.id.close_alert_denyButton);

            deny.setOnClickListener(v -> {
                dismiss();
            });

            accept.setOnClickListener(v -> {
                dismiss();
                popupWindow.dismiss();
            });
        }
    }

}