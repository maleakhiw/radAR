package radar.radar;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

import radar.radar.Adapters.ChatAdapter;
import radar.radar.Models.Chat;
import radar.radar.Presenters.ChatListPresenter;
import radar.radar.Services.ChatApi;
import radar.radar.Services.ChatService;
import radar.radar.Views.ChatListView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatListActivity extends AppCompatActivity implements ChatListView {
    private ChatService chatService;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;

    private ArrayList<Integer> chatIDs;
    private ArrayList<Chat> groups;

    private ChatListPresenter chatListPresenter;
    NavigationActivityHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // Setup navigation drawer
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        helper = new NavigationActivityHelper(navigationView, drawerLayout, toolbar, this);

        // Setup groups
        groups = new ArrayList<>();

        // Setup recycler view
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatAdapter = new ChatAdapter(ChatListActivity.this, groups);
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(ChatListActivity.this));

        // Create retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://35.185.35.117/api/")
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

    @Override
    public void setGroups(ArrayList<Chat> groups) {
        this.groups = groups;
    }

    @Override
    public void setChatIDs(ArrayList<Integer> chatIDs) {
        this.chatIDs = chatIDs;
    }

    @Override
    public ArrayList<Integer> getChatIDs() {
        return this.chatIDs;
    }

    @Override
    public ArrayList<Chat> getGroups() {
        return this.groups;
    }

    @Override
    public void showToastMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setArrayListInAdapter(ArrayList<Chat> groups) {
        chatAdapter.setChatList(groups);
    }

    @Override
    public void notifyAdapterChange() {
        chatAdapter.notifyDataSetChanged();
    }

}
