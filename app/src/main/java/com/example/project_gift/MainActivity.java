package com.example.project_gift;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.project_gift.auth.LoggedUser;
import com.example.project_gift.model.Student;
import com.example.project_gift.model.Teacher;
import com.example.project_gift.ui.dashboard.DashboardFragment;
import com.example.project_gift.ui.home.StudentHomeFragment;
import com.example.project_gift.ui.home.TeacherHomeFragment;
import com.example.project_gift.ui.notifications.NotificationsFragment;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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
        getSupportActionBar().setTitle(getText(R.string.title_dashboard));
    }

    private void newNotificationsFragment() {
        selectedFragment = new NotificationsFragment();
        getSupportActionBar().setTitle(getText(R.string.title_notifications));
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