package radar.radar;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // Setup groups
        groups = new ArrayList<>();

        // Setup UI
        setupUI();

        // Create retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://radar.fadhilanshar.com/api/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create chat api
        ChatApi chatApi = retrofit.create(ChatApi.class);

        // Create the service
        chatService = new ChatService(this, chatApi);

        // Create a presenter object
        chatListPresenter = new ChatListPresenter(this, chatService);

        // Call the method to display chat list
        chatListPresenter.getChatIDs();
    }

    /**
     * Connect UI element from XML file to java
     * Setup navigation helper
     */
    public void setupUI() {
        // Setup navigation drawer
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        TextView name = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        TextView email = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);
        helper = new NavigationActivityHelper(navigationView, drawerLayout, toolbar, name, email, this);

        // Setup recycler view
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatListAdapter = new ChatListAdapter(ChatListActivity.this, groups);
        chatRecyclerView.setAdapter(chatListAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(ChatListActivity.this));
        chatRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        setTitle("Chats");
    }

    /**
     * Setter for groups
     * @param groups array list of Group
     */
    @Override
    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
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
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
     * @param index index of the chatids that we want
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
    public void processDisplayChatList(GetChatInfoResponse getChatInfoResponse) {
        // Add to groups
        groups.add(getChatInfoResponse.group);
        chatListAdapter.setChatList(groups);
        chatListAdapter.notifyDataSetChanged();
    }

}
