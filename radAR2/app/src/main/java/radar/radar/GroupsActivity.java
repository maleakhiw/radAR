package radar.radar;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Responses.AuthResponse;
import radar.radar.Models.Responses.GroupsResponse;
import radar.radar.Models.Responses.UpdateLocationResponse;
import radar.radar.Services.AuthApi;
import radar.radar.Services.AuthService;
import radar.radar.Services.GroupManagementSystemApi;
import radar.radar.Services.GroupsApi;
import radar.radar.Services.GroupsService;
import radar.radar.Services.LocationApi;
import radar.radar.Services.LocationService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupsActivity extends AppCompatActivity {

    NavigationActivityHelper helper;
    Retrofit retrofit;

    AuthService authService;
    GroupsService groupsService;
    LocationService locationService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        retrofit = new Retrofit.Builder()
                   .baseUrl("http://35.185.35.117/api/")
                   .addConverterFactory(GsonConverterFactory.create())
                   .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                   .build();

        AuthApi authApi = retrofit.create(AuthApi.class);
        authService = new AuthService(authApi, this);

        LocationApi locationApi = retrofit.create(LocationApi.class);
        GroupsApi groupsApi = retrofit.create(GroupsApi.class);
        // static measure to test location service working

        locationService = new LocationService(locationApi, this);
        groupsService = new GroupsService(this, groupsApi);

        groupsService.getGroup(2).subscribe(new Observer<GroupsResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(GroupsResponse groupsResponse) {
                // received a response from the server
                if (groupsResponse.group != null) {

                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e);
                // e.g. invalid token, internal errors, etc.
            }

            @Override
            public void onComplete() {

            }
        });

        locationService.updateLocation(1,1,1,72)
                .subscribe(new Observer<UpdateLocationResponse>() {
                    @Override
                    public void onSubscribe(Disposable d){

                    }

                    @Override
                    public void onNext(UpdateLocationResponse updateLocationResponse){
                        Toast.makeText(getApplicationContext(),
                                "Something didn't happen.",

                                Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getApplicationContext(),
                                "Something happened.",

                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {

                    }
        });





        // FAB used to create new chat
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Current placeholder
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        helper = new NavigationActivityHelper(navigationView, drawer, toolbar, this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }






}
