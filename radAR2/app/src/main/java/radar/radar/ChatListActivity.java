package radar.radar;

import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import radar.radar.Adapters.ChatListAdapter;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Responses.GetChatInfoResponse;
import radar.radar.Presenters.ChatListPresenter;
import radar.radar.Services.ChatApi;
import radar.radar.Services.ChatService;
import radar.radar.Views.ChatListView;
import retrofit2.Retrofit;

/**
 * Class that handles chat list display (ChatListActivity)
 */
public class ChatListActivity extends AppCompatActivity implements ChatListView {
    /** Variable related with service, presenter and user interface */
    private ChatService chatService;
    private RecyclerView chatRecyclerView;
    private ChatListAdapter chatListAdapter;
    private ArrayList<Integer> chatIDs;
    private ArrayList<Group> groups;
    private ChatListPresenter chatListPresenter;
    NavigationActivityHelper helper;

    private FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefreshLayout;

    private final String KEY_RECYCLER_STATE = "recycler_state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        groups = new ArrayList<>();

        swipeRefreshLayout.setEnabled(false);

        Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();
        ChatApi chatApi = retrofit.create(ChatApi.class);
        chatService = new ChatService(this, chatApi);

        setupUI();

        // refresh the list of chats on refresh.
        swipeRefreshLayout.setOnRefreshListener(() -> chatListPresenter.loadData(true));

        // Create a presenter object
        chatListPresenter = new ChatListPresenter(this, chatService);

        chatListPresenter.startUpdates();


        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewGroupActivity.class);
            intent.putExtra("newChat", true);
            startActivity(intent);
        });

        chatListAdapter = new ChatListAdapter(ChatListActivity.this, groups, chatListPresenter);
        chatRecyclerView.setAdapter(chatListAdapter);

        // if we have the list of groups after a rotation change/onSaveInstanceState
        if (savedInstanceState != null) {
            Parcelable listState = savedInstanceState.getParcelable(KEY_RECYCLER_STATE);
            ArrayList<Group> groups = (ArrayList<Group>) savedInstanceState.getSerializable("GROUPS_LIST");

            if (listState != null) {
                chatRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
            }
            chatListAdapter.setGroups(groups);

            chatListPresenter.loadData(true);   // update - might have new messages

        } else {
            chatListPresenter.loadData(true);
        }
    }

    /**
     * Connect UI element from XML file to java
     * Setup navigation helper
     */
    public void setupUI() {
        // Setup navigation drawer
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        fab = findViewById(R.id.new_chat_fab);

        TextView name = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        TextView email = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);
        ImageView img = navigationView.getHeaderView(0).findViewById(R.id.profile_picture);
        helper = new NavigationActivityHelper(navigationView, drawerLayout, toolbar, name, email, img, this);

        // Setup recycler view
        chatRecyclerView = findViewById(R.id.chat_list_RV);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        swipeRefreshLayout = findViewById(R.id.chat_list_swipeRefreshLayout);

        setTitle(getString(R.string.chat_list_title));
    }

    @Override
    public void stopRefreshIndicator() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void startRefreshIndicator() {
        swipeRefreshLayout.setRefreshing(true);
    }

    /**
     * Setter for groups
     * @param groups array list of Group
     */
    @Override
    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
        if (chatRecyclerView != null) {
            chatListAdapter.setGroups(groups);
            chatRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    /**
     * Setter for chatIDs
     * @param chatIDs array list of integer
     */
    @Override
    public void setChatIDs(ArrayList<Integer> chatIDs) {
        this.chatIDs = chatIDs;
    }

    /**
     * Getter for chat IDs
     * @return chatIDs
     */
    @Override
    public ArrayList<Integer> getChatIDs() {
        return this.chatIDs;
    }

    /**
     * Getter for groups
     * @return groups
     */
    @Override
    public ArrayList<Group> getGroups() {
        return this.groups;
    }

    /**
     * Show toast message
     * @param message Message to be shown in toast
     */
    @Override
    public void showToastMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Getting the size of chat ids array list
     * @return size of the chat ids array list
     */
    @Override
    public int getChatIDsSize() {
        return chatIDs.size();
    }

    /**
     * Get chat id
     * @param index index of the chatIDs that we want
     * @return id for particular group
     */
    @Override
    public int getChatId(int index) {
        return chatIDs.get(index);
    }

    /**
     * Process displaying chat list
     */
    @Override
    @Deprecated
    public void processDisplayChatList(GetChatInfoResponse getChatInfoResponse) {
        // Add to groups
        groups.add(getChatInfoResponse.group);
        chatListAdapter.setGroups(groups);
        chatListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        System.out.println("onSaveInstanceState");

        // save RV state
        Parcelable listState = chatRecyclerView.getLayoutManager().onSaveInstanceState();
        ArrayList<Group> groups = chatListAdapter.getGroups();
        state.putParcelable(KEY_RECYCLER_STATE, listState);
        state.putSerializable("GROUPS_LIST", groups);
    }

    @Override
    @Deprecated
    public void removeGroup(int groupID) {
        if (groups != null) {
            ArrayList<Group> newGroups = new ArrayList<>();
            for (Group group : groups) {
                if (group.groupID != groupID) {
                    newGroups.add(group);
                }
            }
            groups = newGroups;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (chatListPresenter != null) {
            chatListPresenter.startUpdates();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (chatListPresenter != null) {
            chatListPresenter.stopUpdates();
        }
    }

}
