package com.example.project_gift;

import android.os.Bundle;

import com.example.project_gift.auth.LoggedUser;
import com.example.project_gift.model.Student;
import com.example.project_gift.ui.dashboard.DashboardFragment;
import com.example.project_gift.ui.home.StudentHomeFragment;
import com.example.project_gift.ui.home.TeacherHomeFragment;
import com.example.project_gift.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navListener);
        getSupportActionBar().setTitle(R.string.title_home);

        if (LoggedUser.getType() instanceof Student)
            newStudentHomeFragment();
        else
            newTeacherHomeFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();
    }

    private void newStudentHomeFragment() {
        selectedFragment = new StudentHomeFragment();
        getSupportActionBar().setTitle(R.string.title_home);
    }

    private void newTeacherHomeFragment() {
        selectedFragment = new TeacherHomeFragment();
        getSupportActionBar().setTitle(R.string.title_home);
    }

    private void newDashboardFragment() {
        selectedFragment = new DashboardFragment();
        getSupportActionBar().setTitle(getText(R.string.title_horarios));
    }

    private void newNotificationsFragment() {
        selectedFragment = new SettingsFragment();
        getSupportActionBar().setTitle(getText(R.string.title_settings));
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        if (LoggedUser.getType() instanceof Student)
                            newStudentHomeFragment();
                        else
                            newTeacherHomeFragment();
                        break;
                    case R.id.navigation_dashboard:
                        newDashboardFragment();
                        break;
                    case R.id.navigation_notifications:
                        newNotificationsFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();

                return true;
            };
}