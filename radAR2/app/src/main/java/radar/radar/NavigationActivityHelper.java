package radar.radar;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import radar.radar.Services.AuthService;


public class NavigationActivityHelper {    // not actually a pure "Presenter"
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    android.support.v7.widget.Toolbar toolbar;
    AppCompatActivity activity;

    // TODO move to constructor
    TextView name;
    TextView email;

    public NavigationActivityHelper(NavigationView navigationView, DrawerLayout drawerLayout, Toolbar toolbar, AppCompatActivity activity) {
        this.navigationView = navigationView;
        this.drawerLayout = drawerLayout;
        this.toolbar = toolbar;
        this.activity = activity;

        initialiseToolbarAndDrawer();
    }

    /**
     * Sets up the navigation drawer. Changing the behaviour app-wide is easier when we reuse
     * the same code.
     */
    private void initialiseToolbarAndDrawer() {
        activity.setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // update profile info display
        // TODO should be provided via constructor
        name = activity.findViewById(R.id.nav_header_name);
        email = activity.findViewById(R.id.nav_header_email);
        name.setText(AuthService.getFirstName(activity) + " " + AuthService.getLastName(activity));
        email.setText(AuthService.getEmail(activity));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.nav_maps) {
                    //TODO: Go to maps
                    Intent intent = new Intent(activity, MapsActivity.class);
                    activity.startActivity(intent);
                } else if (id == R.id.nav_chats) {
                    // launch chats
                    Intent intent = new Intent(activity, ChatListActivity.class);
                    activity.startActivity(intent);
                } else if (id == R.id.nav_friends) {
                    // launch Friends activity
                    Intent intent = new Intent(activity, FriendsActivity.class);
                    activity.startActivity(intent);

                } else if (id == R.id.nav_logout) {
                    // launch Login activity
                    Intent intent = new Intent(activity, LoginActivity.class);
                    AuthService.signOut(activity);
                    activity.startActivity(intent);


                } else if (id == R.id.nav_settings) {
                    // TODO - Define settings
                    // TODO for now used for AR

                    Intent intent = new Intent(activity, ARActivity2.class);
                    activity.startActivity(intent);

                } else if (id == R.id.nav_tracking_groups) {
                    Intent intent = new Intent(activity, GroupsListActivity.class);
                    activity.startActivity(intent);

                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    private void uncheckAllItems() {
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }




}
