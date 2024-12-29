package me.socialnetwork;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import me.socialnetwork.profile.ProfileLikeFragment;
import me.socialnetwork.profile.ProfilePostsFragment;
import me.socialnetwork.profile.ProfileRepostFragment;

public class ProfilePagerAdapter extends FragmentStateAdapter {

    private String userId;

    public ProfilePagerAdapter(@NonNull FragmentActivity fragmentActivity, String userId) {
        super(fragmentActivity);
        this.userId = userId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1: return new ProfileLikeFragment(userId);
            case 2: return new ProfileRepostFragment(userId);
            default: return new ProfilePostsFragment(userId);
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Số lượng tab
    }
}
