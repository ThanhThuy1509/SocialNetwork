package me.socialnetwork.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static me.socialnetwork.LoginActivity.clearData;
import static me.socialnetwork.LoginActivity.getData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import me.socialnetwork.LoginActivity;
import me.socialnetwork.MainActivity;
import me.socialnetwork.ProfilePagerAdapter;
import me.socialnetwork.R;
import me.socialnetwork.Values;
import me.socialnetwork.api.StandardAPI;
import me.socialnetwork.api.User;
import me.socialnetwork.profile.follow.FollowWidget;
import me.socialnetwork.profile.follow.UserTagAdapter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private final static String UNSUCCESSFULL = "UNSUCCESSFULL";
    private final static String USERNAME_IS_NOT_AVAILABLE = "USERNAME_IS_NOT_AVAILABLE";
    private final static String NOTHING_CHANGE = "NOTHING_CHANGE";
    private final static String PROFILE_UPDATE = "PROFILE_UPDATE";
    private View view;

    private TextView nameProfile;
    private TextView usernameProfile;
    ImageView avatarProfile;
    ImageView backButton;
    ImageView drawerButton;
    User user;
    String userId;

    public ProfileFragment(String userId) {
        this.userId = userId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        drawerButton = view.findViewById(R.id.drawer_button);
        DrawerLayout drawerLayout = view.findViewById(R.id.profileFragment);
        TextView logoutButton = view.findViewById(R.id.logout);
        drawerButton.setOnClickListener(v -> {
            drawerLayout.openDrawer(Gravity.RIGHT);
        });

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        nameProfile = view.findViewById(R.id.nameProfile);
        usernameProfile = view.findViewById(R.id.usernameProfile);
        avatarProfile = view.findViewById(R.id.profileImage);
        TextView followersCount = view.findViewById(R.id.followersCount);
        TextView followingCount = view.findViewById(R.id.followingCount);
        Button editProfileButton = view.findViewById(R.id.editProfileButton);
        backButton = view.findViewById(R.id.back_button);

        LinearLayout followersShowButton = view.findViewById(R.id.followers_show);
        LinearLayout followingShowButton = view.findViewById(R.id.following_show);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        ProfilePagerAdapter adapter = new ProfilePagerAdapter(requireActivity(), userId);
        viewPager.setAdapter(adapter);

        StandardAPI.getService.getProfile(userId, getData(requireContext(), "cookie")).enqueue(new Callback<User>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                assert response.body() != null;
                user = response.body();
                nameProfile.setText(response.body().getName());
                usernameProfile.setText(response.body().getUsername());

                // Base64 to image
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    byte[] imgBytes = Base64.getDecoder().decode(response.body().getAvatar());
                    Bitmap img = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);

                    avatarProfile.setImageBitmap(img);

                }

                followersCount.setText(response.body().getFollowers());
                followingCount.setText(response.body().getFollowing());

            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {

            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText(view.getResources().getString(R.string.posts)); break;
                case 1: tab.setText(view.getResources().getString(R.string.likes)); break;
                case 2: tab.setText(view.getResources().getString(R.string.reposts)); break;
            }
        }).attach();

        editProfileButton.setOnClickListener(v -> editProfileWindow(view.getContext()));

        followersShowButton.setOnClickListener(v -> {
            new FollowWidget(view,requireActivity(), user, 0, new UserTagAdapter.InfoChange() {
                @Override
                public void following(String following) {
                    followingCount.setText(following);
                    user.setFollowing(following);
                }
            });
        });

        followingShowButton.setOnClickListener(v -> {
            new FollowWidget(view,requireActivity(), user, 1, new UserTagAdapter.InfoChange() {
                @Override
                public void following(String following) {
                    followingCount.setText(following);
                    user.setFollowing(following);
                }
            });
        });

        logoutButton.setOnClickListener(v -> {
            clearData(view.getContext());
            getActivity().onBackPressed();
        });

        return view;
    }


    private ImageView avatar;
    private String avatarBase64;
    private EditText usernameEdit;
    private EditText nameEdit;
    private ImageView checkMarkButton;


    public void editProfileWindow(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.windown_edit_profile, null);

        int width = this.view.getWidth();
        int height = (int) (Resources.getSystem().getDisplayMetrics().heightPixels * Values.POPUPWINDOW_HEIGHT);

        Drawable background = ContextCompat.getDrawable(context, R.drawable.popupwindow_background);
        view.setBackgroundDrawable(background);

        PopupWindow popupWindow = new PopupWindow(view, width, height, true);
        popupWindow.setBackgroundDrawable(background);
        popupWindow.setAnimationStyle(R.style.Animation);
        popupWindow.showAtLocation(this.view.findViewById(R.id.profileFragment), Gravity.BOTTOM, 0, 0);
        dimBehind(popupWindow);

        TextView changeAvatarButton = view.findViewById(R.id.changeAvatarButton);
        TextView nameWarning = view.findViewById(R.id.nameWarning);
        nameEdit = view.findViewById(R.id.name);
        TextView usernameWarning = view.findViewById(R.id.usernameWarning);
        usernameEdit = view.findViewById(R.id.username);

        ImageView closeButton = view.findViewById(R.id.closeButton);
        checkMarkButton = view.findViewById(R.id.checkMarkButton);

        avatar = view.findViewById(R.id.avatar);

        nameEdit.setText(this.nameProfile.getText());
        usernameEdit.setText(this.usernameProfile.getText());

        checkMarkButton.setEnabled(false);
        checkMarkButton.setColorFilter(Color.argb(255, 240, 240, 240));

        avatar.setImageDrawable(avatarProfile.getDrawable());

        closeButton.setOnClickListener(view1 -> {
            if (conditionsUpdateProfile()) {
                HomeFragment.CustomDialogClass customDialogClass = new HomeFragment.CustomDialogClass((Activity) this.view.getContext(), popupWindow);
                customDialogClass.show();
            } else {
                popupWindow.dismiss();
            }

        });

        nameEdit.setOnEditorActionListener((v2, actionId, event) -> {
            nameWarning.setText(nameEdit.getText().toString().isEmpty() ? "Tên không được bỏ trống.": "");
            checkMarkButton.setColorFilter(!conditionsUpdateProfile() ? Color.argb(255, 240, 240, 240): Color.argb(255, 0, 0, 0));
            checkMarkButton.setEnabled(conditionsUpdateProfile());
            Log.i("ProfileFragment", checkMarkButton.isEnabled() + "");
            return false;
        });

        usernameEdit.setOnEditorActionListener((v2, actionId, event) -> {
            nameWarning.setText(nameEdit.getText().toString().isEmpty() ? "Tên tài khoản không được bỏ trống.": "");
            checkMarkButton.setColorFilter(!conditionsUpdateProfile() ? Color.argb(255, 240, 240, 240): Color.argb(255, 0, 0, 0));
            checkMarkButton.setEnabled(conditionsUpdateProfile());
            Log.i("ProfileFragment", checkMarkButton.isEnabled() + "");
            return false;
        });

        checkMarkButton.setOnClickListener(v1 -> {
            Log.i("ProfileFragment", "Click");
            if (!nameProfile.equals(nameEdit.getText().toString()) ||
                    !usernameProfile.equals(usernameEdit.getText().toString())
            ) {
                // Call API
                Map<String, String> mapBody = new HashMap<>();
                mapBody.put("name", nameEdit.getText().toString());
                mapBody.put("username", usernameEdit.getText().toString());
                mapBody.put("avatar", avatarBase64);

                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new JSONObject(mapBody).toString());
                StandardAPI.getService.updateProfile(getData(context, "cookie"), body).enqueue(new Callback<User>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if (response.isSuccessful()) {
                            Log.i("ProfileFragment", new Gson().toJson(response.body()));
                            switch (Objects.requireNonNull(response.body()).getStatus()) {
                                case UNSUCCESSFULL:
                                    nameWarning.setText(nameEdit.getText().toString().isEmpty() ? "Tên không được bỏ trống." : "");
                                    usernameWarning.setText(usernameEdit.getText().toString().isEmpty() ? "Tên tài khoản không được bỏ trống." : "");
                                    break;
                                case USERNAME_IS_NOT_AVAILABLE:
                                    usernameWarning.setText("Tên tài khoản đã tồn tại.");
                                    break;
                                case NOTHING_CHANGE:
                                    popupWindow.dismiss();
                                    break;
                                case PROFILE_UPDATE:
                                    nameProfile.setText(response.body().getName());
                                    usernameProfile.setText(response.body().getUsername());
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        byte[] imageBytes = Base64.getDecoder().decode(response.body().getAvatar());
                                        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                        avatarProfile.setImageBitmap(imageBitmap);
                                    }
                                    popupWindow.dismiss();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            } else {
                popupWindow.dismiss();
            }
        });

        changeAvatarButton.setOnClickListener(v1 -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 3);
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

    private boolean conditionsUpdateProfile() {
        if (nameEdit.getText().toString().isEmpty() || usernameEdit.getText().toString().isEmpty())
            return false;
        if (nameProfile.getText().toString().equals(nameEdit.getText().toString()) && usernameProfile.getText().toString().equals(usernameEdit.getText().toString()))
            return avatarBase64 != null && avatarBase64.isEmpty();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && avatar != null) {
            Uri uri = data.getData();
//            String -> byte[] -> bitmap
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    avatarBase64 = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());

                    checkMarkButton.setColorFilter(!conditionsUpdateProfile() ? Color.argb(255, 240, 240, 240): Color.argb(255, 0, 0, 0));
                    checkMarkButton.setEnabled(conditionsUpdateProfile());

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            avatar.setImageURI(uri);
        }
    }
}

