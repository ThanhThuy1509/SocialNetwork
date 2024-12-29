package me.socialnetwork.profile.follow;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.w3c.dom.Text;

import java.util.Objects;

import me.socialnetwork.R;
import me.socialnetwork.Values;
import me.socialnetwork.api.User;

public class FollowWidget {

    public  FollowWidget(View parentView, FragmentActivity fragmentActivity, User user, int pos, UserTagAdapter.InfoChange infoChange) {
        LayoutInflater inflater = (LayoutInflater) parentView.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.window_follow, null);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager2 viewPager2 = view.findViewById(R.id.view_paper);
        ImageView closeButton = view.findViewById(R.id.close_button);
        TextView displayName = view.findViewById(R.id.name);

        FragmentStateAdapter fragmentStateAdapter = new FollowPageAdapter(fragmentActivity, user.getId(), new UserTagAdapter.InfoChange(){
            @Override
            public void following(String following) {
                if (tabLayout != null) {
                    infoChange.following(following);
                    TabLayout.Tab tab = tabLayout.getTabAt(1);
                    tab.setText(following + " " +view.getResources().getString(R.string.following));
                }
            }
        });
        viewPager2.setAdapter(fragmentStateAdapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab_, position) -> {
            switch (position) {
                case 0: tab_.setText(user.getFollowers() + " " + view.getResources().getString(R.string.followers)); break;
                case 1: tab_.setText(user.getFollowing() + " " +view.getResources().getString(R.string.following)); break;
            }
        }).attach();
        TabLayout.Tab tab = tabLayout.getTabAt(pos);
        tab.select();

        int width = parentView.getWidth();
        int height = (int) (Resources.getSystem().getDisplayMetrics().heightPixels* Values.POPUPWINDOW_HEIGHT);

        PopupWindow popupWindow = new PopupWindow(view, width, height, true);
//        popupWindow.setBackgroundDrawable(background);
        popupWindow.setAnimationStyle(R.style.Animation);
        popupWindow.showAtLocation(parentView.findViewById(R.id.profileFragment), Gravity.BOTTOM, 0, 0);
        dimBehind(popupWindow);

        displayName.setText(user.getName());

        closeButton.setOnClickListener(v -> popupWindow.dismiss());

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

}
