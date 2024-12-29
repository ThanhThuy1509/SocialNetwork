package me.socialnetwork.profile.follow;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

class FollowPageAdapter extends FragmentStateAdapter {

    String userId;

    UserTagAdapter.InfoChange infoChange;
    public FollowPageAdapter(@NonNull FragmentActivity fragmentActivity, String userId, UserTagAdapter.InfoChange infoChange) {
        super(fragmentActivity);
        this.userId = userId;
        this.infoChange = infoChange;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1: return new FollowingFragment(userId, new UserTagAdapter.InfoChange() {
                @Override
                public void following(String following) {
                    infoChange.following(following);
                }
            });
            default: return new FollowersFragment(userId, infoChange);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
