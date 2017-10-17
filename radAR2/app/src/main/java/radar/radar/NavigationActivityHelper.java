package radar.radar;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.SearchUserResponse;
import radar.radar.Services.AuthService;
import radar.radar.Services.ResourcesApi;
import radar.radar.Services.ResourcesService;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import retrofit2.Retrofit;


public class NavigationActivityHelper {    // not actually a pure "Presenter"
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    android.support.v7.widget.Toolbar toolbar;
    AppCompatActivity activity;

    // TODO move to constructor
    TextView name;
    TextView email;
    ImageView profilePicture;

    public NavigationActivityHelper(NavigationView navigationView, DrawerLayout drawerLayout, Toolbar toolbar, TextView name, TextView email, AppCompatActivity activity) {
        this.navigationView = navigationView;
        this.drawerLayout = drawerLayout;
        this.toolbar = toolbar;
        this.activity = activity;
        this.name = name;
        this.email = email;

        initialiseToolbarAndDrawer();
    }

    public NavigationActivityHelper(NavigationView navigationView, DrawerLayout drawerLayout, Toolbar toolbar,TextView name, TextView email, ImageView profilePicture,  AppCompatActivity activity) {
        this.navigationView = navigationView;
        this.drawerLayout = drawerLayout;
        this.toolbar = toolbar;
        this.activity = activity;
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;

        initialiseToolbarAndDrawer();

    }

    private void finishIfNotHomeScreen(Activity activity) {
        if (!(activity instanceof HomeScreenActivity)) {
            activity.finish();
        }
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

        navigationView.getHeaderView(0).setOnClickListener(view -> {
            System.out.println("clicked");
        });

        // update profile info display
        name.setText(AuthService.getFirstName(activity) + " " + AuthService.getLastName(activity));
        email.setText(AuthService.getEmail(activity));

        if (name != null) {
            // TODO pass this in as a dependency
            Retrofit retrofit = RetrofitFactory.getRetrofit().build();
            ResourcesApi resourcesApi = retrofit.create(ResourcesApi.class);
            UsersApi usersApi = retrofit.create(UsersApi.class);
            ResourcesService resourcesService = new ResourcesService(activity, resourcesApi);
            UsersService usersService = new UsersService(activity, usersApi);

            // get userID
            int userID = AuthService.getUserID(activity);
            usersService.getProfilePicture(userID, resourcesService).subscribe(new Observer<File>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(File file) {
                    if (file != null) {
                        Picasso.with(activity).load(file).into(profilePicture);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.w("loadProfilePicture", e);
                }

                @Override
                public void onComplete() {

                }
            });


        }

        navigationView.setNavigationItemSelectedListener(item -> {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_maps) {
                Intent intent = new Intent(activity, HomeScreenActivity.class);
                activity.startActivity(intent);
//                finishIfNotHomeScreen(activity);
                activity.finish();  // finish anyway.
            } else if (id == R.id.nav_chats) {
                // launch chats
                Intent intent = new Intent(activity, ChatListActivity.class);
                activity.startActivity(intent);
                finishIfNotHomeScreen(activity);

            } else if (id == R.id.nav_friends) {
                // launch Friends activity
                Intent intent = new Intent(activity, FriendsActivity.class);
                activity.startActivity(intent);
                finishIfNotHomeScreen(activity);

            } else if (id == R.id.nav_logout) {
                // launch Login activity
                Intent intent = new Intent(activity, LoginActivity.class);
                AuthService.signOut(activity);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);    // clear entire Activity stack
                activity.startActivity(intent);
                finishIfNotHomeScreen(activity);

            }

            else if (id == R.id.nav_tracking_groups) {
                Intent intent = new Intent(activity, GroupsListActivity.class);
                activity.startActivity(intent);
                finishIfNotHomeScreen(activity);
            }


            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

    }



}
