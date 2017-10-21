package radar.radar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import radar.radar.Adapters.NewGroupListAdapter;
import radar.radar.Models.Android.UserWithCheckbox;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Domain.User;
import radar.radar.Models.UpdateGroupBody;
import radar.radar.Services.GroupsApi;
import radar.radar.Services.GroupsService;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import retrofit2.Retrofit;

interface AddMembersView {
    void setFriendsList(ArrayList<User> friends);

    void doOnBackPressed();
}

public class AddMembersActivity extends AppCompatActivity implements AddMembersView {

    AddMembersPresenter presenter;
    RecyclerView recyclerView;

    Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);

        setTitle(getString(R.string.invite_members));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        group = (Group) getIntent().getExtras().get("group");
        if (group == null) {
            onBackPressed();
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed()); // enable back button

        Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();
        presenter = new AddMembersPresenter(this,
                new UsersService(this, retrofit.create(UsersApi.class)),
                new GroupsService(this, retrofit.create(GroupsApi.class)));

        // set up RecyclerView
        recyclerView = findViewById(R.id.RecyclerView_members_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new NewGroupListAdapter(new ArrayList<>()));

        presenter.loadFriends();
    }

    @Override
    public void setFriendsList(ArrayList<User> friends) {
        ArrayList<UserWithCheckbox> friendsWithCheckbox = new ArrayList<>();
        // TODO do not create new ArrayList at all times; ask for existing ones and just add new users
        // for continuous updating

        for (User user: friends) {
            friendsWithCheckbox.add(new UserWithCheckbox(user, false));
        }

        ((NewGroupListAdapter) recyclerView.getAdapter()).setUsers(friendsWithCheckbox);
    }

    @Override
    public void doOnBackPressed() {
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // only item on menu is Done.
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.done:
                ArrayList<UserWithCheckbox> users = ((NewGroupListAdapter) recyclerView.getAdapter()).getUsers();
                ArrayList<Integer> selectedUsers = new ArrayList<>();

                for (UserWithCheckbox user: users) {
                    if (user.isChecked) {
                        selectedUsers.add(user.userID);
                    }
                }

                presenter.addMembers(group.groupID, selectedUsers);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

class AddMembersPresenter {
    GroupsService groupsService;
    AddMembersView view;
    UsersService usersService;

    public AddMembersPresenter(AddMembersView view, UsersService usersService, GroupsService groupsService) {
        this.view = view;
        this.usersService = usersService;
        this.groupsService = groupsService;
    }

    void loadFriends(){
        usersService.getFriends().subscribe(friendsResponse ->
                        view.setFriendsList(friendsResponse.friends),
                Throwable::printStackTrace);
    }

    void addMembers(int groupID, ArrayList<Integer> selectedUsers) {
        groupsService.addMembers(groupID, selectedUsers).subscribe(
                status -> {
                    if (status.success) {
                        view.doOnBackPressed();
                    }
                },
                Throwable::printStackTrace
        );
    }
}