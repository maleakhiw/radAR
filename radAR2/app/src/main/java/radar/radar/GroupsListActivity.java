package radar.radar;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import radar.radar.Adapters.GroupsAdapter;
import radar.radar.Models.Domain.Group;
import radar.radar.Presenters.GroupsListPresenter;
import radar.radar.Services.GroupsApi;
import radar.radar.Services.GroupsService;
import radar.radar.Views.GroupsListView;
import retrofit2.Retrofit;

public class GroupsListActivity extends AppCompatActivity
                                implements GroupsListView {

    GroupsListPresenter presenter;

    RecyclerView recyclerView;
    GroupsAdapter rvAdapter;

    SwipeRefreshLayout swipeRefreshLayout;

    NavigationActivityHelper helper;

    private void loadViews() {
        recyclerView = findViewById(R.id.groups_list_recyclerView);
    }


    private boolean first = true;
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
        ImageView img = navigationView.getHeaderView(0).findViewById(R.id.profile_picture);
        helper = new NavigationActivityHelper(navigationView, drawerLayout, toolbar, name, email, img, this);

        setTitle("Groups");  // TODO replace with String resource

        loadViews();

        // swipe refresh layout
        swipeRefreshLayout = findViewById(R.id.group_list_swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadData();
            }
        });

        Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();
        GroupsApi groupsApi = retrofit.create(GroupsApi.class);
        GroupsService groupsService = new GroupsService(this, groupsApi);
        presenter = new GroupsListPresenter(groupsService, this);

        // setup recyclerView
        rvAdapter = new GroupsAdapter(this, new ArrayList<>(), presenter);
        recyclerView.setAdapter(rvAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));


        if (first) {
            presenter.loadData();
            first = false;
        }

        FloatingActionButton fab = findViewById(R.id.new_group_fab);


        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewGroupActivity.class);
            intent.putExtra("newGroup", true);
            startActivity(intent);
        });

    }

    @Override
    public void setRefreshing(boolean refreshing) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!first) {
            presenter.loadData();
            recyclerView.getAdapter().notifyDataSetChanged();
        }
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

    @Override
    public void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT);
    }
}
