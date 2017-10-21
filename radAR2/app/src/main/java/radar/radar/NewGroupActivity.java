package radar.radar;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Adapters.NewGroupListAdapter;
import radar.radar.Models.Android.UserWithCheckbox;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Domain.MeetingPoint;
import radar.radar.Models.Domain.User;
import radar.radar.Models.Responses.FriendsResponse;
import radar.radar.Models.Responses.GroupsResponse;
import radar.radar.Models.Responses.NewChatResponse;
import radar.radar.Services.GroupsApi;
import radar.radar.Services.GroupsService;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import retrofit2.Retrofit;

public class NewGroupActivity extends AppCompatActivity {

    private static final String DEFAULT_TEXT = "Click 'SELECT' to select a location";
    private static final String TAG = "NewGroupActivity";
    private static final int REQUEST_CODE_AUTOCOMPLETE = 111;

    GroupsService groupsService;
    UsersService usersService;

    RecyclerView recyclerView;
    NewGroupListAdapter adapter;

    Button button;

    private Boolean first = true;
    private String placeName;
    private Double placeLat;
    private Double placeLng;
    private TextView locationText;
    private ImageButton cancelButton;

    void launchGroup(Group group) {
        Intent intent = new Intent(this, GroupDetailActivity.class);
        intent.putExtra("group", group);
        startActivity(intent);
        finish();
    }

    void launchChat(Group group) {
        Intent chatListIntent = new Intent(this, ChatListActivity.class);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("group", group);
        intent.putExtra("load", true);
        startActivities(new Intent[]{chatListIntent, intent});
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        Toolbar toolbar = findViewById(R.id.new_group_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> onBackPressed()); // enable back button

        Bundle bundle = getIntent().getExtras();

        placeName = bundle.getString("name");
        try {
            placeLat = Double.parseDouble(bundle.getString("lat"));
            placeLng = Double.parseDouble(bundle.getString("lng"));
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }


        //set up meeting point view
        locationText = findViewById(R.id.textViewLocn);
        if (placeName != null) {
            locationText.setText(placeName);
            cancelButton.setVisibility(View.VISIBLE);
        }

        Button selectButton = findViewById(R.id.select_button);
        selectButton.setOnClickListener(view -> {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent i;
            try {
                i = builder.build(this);
                startActivityForResult(i, REQUEST_CODE_AUTOCOMPLETE);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        });

        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> {
            cancelButton.setVisibility(View.INVISIBLE);
            locationText.setText(DEFAULT_TEXT);
            placeName = null;
            placeLat = null;
            placeLng = null;
        });

        recyclerView = findViewById(R.id.new_group_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up retrofit
        Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();

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
                ArrayList<User> users = friendsResponse.friends;
                ArrayList<UserWithCheckbox> users2 = new ArrayList<>();

                for (User user : users) {
                    users2.add(new UserWithCheckbox(user, false));
                }

                adapter = new NewGroupListAdapter(users2);
                recyclerView.setAdapter(adapter);

                button = findViewById(R.id.new_group_button);
                button.setOnClickListener(view -> {
                    ArrayList<UserWithCheckbox> usersWithCheckboxes = adapter.getUsers();
                    ArrayList<Integer> selectedUsers = new ArrayList<>();

                    for (UserWithCheckbox user: usersWithCheckboxes) {
                        if (user.isChecked) {
                            selectedUsers.add(user.userID);
                        }
                    }

                    if (textInputEditText.getEditText().getText().toString().trim().length() == 0) {
                        textInputEditText.setError("Missing group name");
                    } else {
                        // disable button, don't want duplicate group
                        button.setEnabled(false);

                        Intent intent = getIntent();
                        if (intent.getExtras().containsKey("newGroup")) {
                            newGroup(textInputEditText.getEditText().getText().toString(), selectedUsers);
                        } else if (intent.getExtras().containsKey("newChat")) {
                            newChat(textInputEditText.getEditText().getText().toString(), selectedUsers);
                        } else {
                            // default to new group
                            newGroup(textInputEditText.getEditText().getText().toString(), selectedUsers);
                        }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlacePicker.getPlace(this, data);
                Log.i(TAG, "Place Selected: " + place.getName());
                placeName = place.getName().toString();
                placeLat = place.getLatLng().latitude;
                placeLng = place.getLatLng().longitude;
                locationText.setText(place.getName());
                cancelButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public void newChat(String groupName, ArrayList<Integer> selectedUsers) {
        groupsService.newChat(groupName, selectedUsers).subscribe(new Observer<NewChatResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(NewChatResponse groupsResponse) {
                System.out.println(groupsResponse);

                if (groupsResponse.success) {
                    launchChat(groupsResponse.group);
                } else {
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
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

    public void newGroup(String groupName, ArrayList<Integer> selectedUsers) {

        Observer<GroupsResponse> observer = new Observer<GroupsResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(GroupsResponse groupsResponse) {
                System.out.println(groupsResponse);

                if (groupsResponse.success) {
                    launchGroup(groupsResponse.group);

                } else {
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
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
        };

        if (placeName == null) {
            groupsService.newGroup(groupName, selectedUsers).subscribe(observer);
        } else {
            groupsService.newGroup(groupName, selectedUsers,
                    new MeetingPoint(placeLat,
                            placeLng,
                            placeName,
                            "")).subscribe(observer);
        }

    }
}
