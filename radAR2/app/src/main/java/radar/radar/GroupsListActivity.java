package radar.radar;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import radar.radar.Adapters.GroupsAdapter;
import radar.radar.Models.Group;
import radar.radar.Presenters.GroupsListPresenter;
import radar.radar.Services.GroupsApi;
import radar.radar.Services.GroupsService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupsListActivity extends AppCompatActivity
                                implements GroupsListView {

    GroupsListPresenter presenter;

    RecyclerView recyclerView;
    GroupsAdapter rvAdapter;

    private void loadViews() {
        recyclerView = findViewById(R.id.groups_list_recyclerView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);

        loadViews();

        System.out.println("Group   sListActivity");

        // setup recyclerView
        rvAdapter = new GroupsAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(rvAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://35.185.35.117/api/")
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
    public void updateRecyclerViewDataSet(ArrayList<Group> groups) {
        rvAdapter.setGroupsList(groups);
        rvAdapter.notifyDataSetChanged();
    }
}
