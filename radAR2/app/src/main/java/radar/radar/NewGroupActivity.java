package radar.radar;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Adapters.NewGroupListAdapter;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Responses.FriendsResponse;
import radar.radar.Models.Responses.GroupsResponse;
import radar.radar.Models.Domain.User;
import radar.radar.Models.Android.UserWithCheckbox;
import radar.radar.Services.GroupsApi;
import radar.radar.Services.GroupsService;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewGroupActivity extends AppCompatActivity {

    GroupsService groupsService;
    UsersService usersService;

    RecyclerView recyclerView;
    NewGroupListAdapter adapter;

    void launchGroup(Group group) {
        Intent intent = new Intent(this, GroupDetailActivity.class);
        intent.putExtra("group", group);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        recyclerView = findViewById(R.id.new_group_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // TODO refactor to MVP

        Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("https://radar.fadhilanshar.com/api/")
                                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();

        groupsService = new GroupsService(this, retrofit.create(GroupsApi.class));
        usersService = new UsersService(this, retrofit.create(UsersApi.class));

        TextInputLayout textInputEditText = findViewById(R.id.new_group_name);

        usersService.getFriends().subscribe(new Observer<FriendsResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(FriendsResponse friendsResponse) {
                // setup recyclerView
                ArrayList<User> users= friendsResponse.friends;
                ArrayList<UserWithCheckbox> users2 = new ArrayList<>();

                for (User user: users) {
                    users2.add(new UserWithCheckbox(user, false));
                }

                adapter = new NewGroupListAdapter(users2);
                recyclerView.setAdapter(adapter);

                Button button = findViewById(R.id.new_group_button);
                button.setOnClickListener(view -> {
                    ArrayList<UserWithCheckbox> userWithCheckboxes = adapter.getUsers();
                    ArrayList<Integer> selectedUsers = new ArrayList<>();
                    for (int i=0; i<userWithCheckboxes.size(); i++) {
                        UserWithCheckbox user = userWithCheckboxes.get(i);
                        if (user.isChecked) {
                            selectedUsers.add(users.get(i).userID);
                        }
                    }

                    if (textInputEditText.getEditText().getText().toString().trim().length() == 0) {
                        textInputEditText.setError("Missing group name");
                    } else {
                        // disable button, don't want duplicate group
                        button.setEnabled(false);
                        groupsService.newGroup(textInputEditText.getEditText().getText().toString(), selectedUsers).subscribe(new Observer<GroupsResponse>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(GroupsResponse groupsResponse) {
                                System.out.println(groupsResponse);

                                if (groupsResponse.success) {
                                    launchGroup(groupsResponse.group);

                                } else {
                                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_LONG);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                System.out.println(e);
                                button.setEnabled(true);
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                    }

                });

            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e);
            }

            @Override
            public void onComplete() {

            }
        });



    }
}
