package me.socialnetwork;

import static me.socialnetwork.LoginActivity.getData;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.socialnetwork.api.StandardAPI;
import me.socialnetwork.fragment.PostFragment;
import me.socialnetwork.post.Post;
import me.socialnetwork.post.PostAdapter;
import me.socialnetwork.post.Status;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends AppCompatActivity {

    private Post post;
    private EditText reply;
    private Map<String, String> mainBody;

    private PostFragment postFragment;

    /*
     * https://stackoverflow.com/questions/19069448/null-pointer-error-with-hidesoftinputfromwindow
     */
    InputMethodManager inputManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_post), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        reply = findViewById(R.id.reply_content);
        reply.setOnFocusChangeListener((v, hasFocus) -> {
            Log.i("ReplyLis", hasFocus + "");
            if (!hasFocus) {
                inputManager.hideSoftInputFromWindow(reply.getWindowToken(), 0);
            } else {
                inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        CardView sendReplyButton = findViewById(R.id.send_reply);

        LinearLayout layoutWrapReplyTo = findViewById(R.id.layout_wrap_reply_to);
        layoutWrapReplyTo.setVisibility(View.GONE);

        mainBody = new HashMap<>();

        TextView detailReplyTo = findViewById(R.id.detail_reply_to);
        TextView cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> {
            layoutWrapReplyTo.setVisibility(View.GONE);
            reply.setHint(getResources().getString(R.string.hint_reply_to_post));
            reply.getText().clear();
            reply.clearFocus();
            mainBody = new HashMap<>();
        });

        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        String cookie = getData(this, "cookie");

        String postID = getIntent().getStringExtra("post_id");



        StandardAPI.getService.getPost(postID, cookie).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                if (response.isSuccessful()) {
                    post = response.body();
                    postFragment = new PostFragment(post, new PostAdapter.PostCallback() {
                        @Override
                        public void Like(boolean isLike, String likeCount) {

                        }

                        @Override
                        public void Comment(Map<String, String> body) {
                            Log.i("Comment callback", "PostActivity");
                            if (body.get("parent_comment_id") != null) {
                                layoutWrapReplyTo.setVisibility(View.VISIBLE);
                                detailReplyTo.setText(String.format(getResources().getString(R.string.reply_to_person), body.get("username")));
                                reply.setHint(getResources().getString(R.string.hint_reply_to_person));
                                mainBody = body;
                            } else {
                                reply.setHint(getResources().getString(R.string.hint_reply_to_post));
                                layoutWrapReplyTo.setVisibility(View.GONE);
                            }
                            reply.getText().clear();
                            if (!reply.isFocused()) {
                                inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                reply.requestFocus();
                            }

                        }

                        @Override
                        public void Repost(boolean isRepost, String repostCount) {

                        }
                    }, new PostFragment.StatusCallback() {
                        @Override
                        public void status(Status status) {
                            if (status == Status.SCROLL_DOWN) {
                                reply.clearFocus();
                            }
                        }
                    });
                    getSupportFragmentManager().beginTransaction().replace(R.id.post, postFragment).commit();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Post> call, @NonNull Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        reply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    sendReplyButton.setCardBackgroundColor(Color.argb(255, 224, 224, 224));
                } else sendReplyButton.setCardBackgroundColor(getResources().getColor(R.color.blue));
            }
        });

        sendReplyButton.setOnClickListener(v -> {
            if (reply.getText().toString().isEmpty() || post == null) return;
            Log.i("addComment", "true");

            mainBody.put("content", reply.getText().toString());
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new JSONObject(mainBody).toString());
            Log.i("Info comment", new Gson().toJson(mainBody));
            StandardAPI.getService.addComment(postID, cookie, requestBody).enqueue(new Callback<Post>() {
                @Override
                public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                    if (response.isSuccessful()) {
                        reply.getText().clear();
                        reply.clearFocus();
                        layoutWrapReplyTo.setVisibility(View.GONE);
                        reply.setHint(getResources().getString(R.string.hint_reply_to_post));
                        if (postFragment != null)
                            postFragment.addComment(response.body());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<Post> call, @NonNull Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        });

        boolean replyToPost = getIntent().getBooleanExtra("reply", false);
        if (replyToPost) {
            reply.requestFocus();
        }

    }

}