package me.socialnetwork;

import static me.socialnetwork.LoginActivity.getData;
import static me.socialnetwork.R.id;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import me.socialnetwork.fragment.HomeFragment;
import me.socialnetwork.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Tham chiếu đến BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Hiển thị Fragment mặc định (HomeFragment)
        loadFragment(new HomeFragment());

        bottomNavigationView.getMenu().findItem(id.nav_home).setChecked(true);

        // Xử lý sự kiện khi người dùng chọn mục trong BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case (id.nav_home):
                    selectedFragment = new HomeFragment();
                    break;
                case (id.nav_profile):
                    selectedFragment = new ProfileFragment(getData(this, "id"));
                    break;
            }

            // Tải Fragment được chọn
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        // Thay thế Fragment hiện tại trong FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
