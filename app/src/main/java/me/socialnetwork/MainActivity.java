package me.socialnetwork;

import static me.socialnetwork.R.*;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tham chiếu đến BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Hiển thị Fragment mặc định (HomeFragment)
        loadFragment(new HomeFragment());

        int nav_home_id = R.id.nav_profile;
        int nav_profile_id = id.nav_profile;

        // Xử lý sự kiện khi người dùng chọn mục trong BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case (id.nav_home):
                        selectedFragment = new HomeFragment();
                        break;
                    case (id.nav_profile):
                        selectedFragment = new ProfileFragment();
                        break;

                }

                // Tải Fragment được chọn
                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }
                return true;
            }
        });

    }



    private void loadFragment(Fragment fragment) {
        // Thay thế Fragment hiện tại trong FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
