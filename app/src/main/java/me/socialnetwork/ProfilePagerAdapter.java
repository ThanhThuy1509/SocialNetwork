package me.socialnetwork;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import me.socialnetwork.fragment.PostsFragment;
import me.socialnetwork.fragment.RepliesFragment;
import me.socialnetwork.fragment.RepostFragment;

public class ProfilePagerAdapter extends FragmentStateAdapter {

    public ProfilePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new PostsFragment();
            case 1: return new RepliesFragment();
            case 2: return new RepostFragment();
            default: return new PostsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Số lượng tab
    }
}
