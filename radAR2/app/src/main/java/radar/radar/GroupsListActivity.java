package radar.radar;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import radar.radar.Adapters.GroupsAdapter;
import radar.radar.Models.Domain.Group;
import radar.radar.Presenters.GroupsListPresenter;
import radar.radar.Services.GroupsApi;
import radar.radar.Services.GroupsService;
import radar.radar.Views.GroupsListView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupsListActivity extends AppCompatActivity
                                implements GroupsListView {

    GroupsListPresenter presenter;

    RecyclerView recyclerView;
    GroupsAdapter rvAdapter;

    NavigationActivityHelper helper;

    private void loadViews() {
        recyclerView = findViewById(R.id.groups_list_recyclerView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);
        
        // Setup navigation drawer
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        TextView name = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        TextView email = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);
        helper = new NavigationActivityHelper(navigationView, drawerLayout, toolbar, name, email, this);

        loadViews();


        // setup recyclerView
        rvAdapter = new GroupsAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(rvAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));


        Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("https://radar.fadhilanshar.com/api/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                        .build();
        GroupsApi groupsApi = retrofit.create(GroupsApi.class);
        GroupsService groupsService = new GroupsService(this, groupsApi);

        presenter = new GroupsListPresenter(groupsService, this);

        FloatingActionButton fab = findViewById(R.id.new_group_fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), NewGroupActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateRecyclerViewDataSet(ArrayList<Group> groups) {
        rvAdapter.setGroupsList(groups);
        rvAdapter.notifyDataSetChanged();
    }
}
