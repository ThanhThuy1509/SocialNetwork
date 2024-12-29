package me.socialnetwork.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ProfileOtherFragment extends ProfileFragment {

    public ProfileOtherFragment(String userId) {
        super(userId);
        this.userId = userId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        backButton.setVisibility(View.VISIBLE);
        drawerButton.setVisibility(View.GONE);
        backButton.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });
        return view;
    }
}
