package radar.radar;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Adapters.MessageListAdapter;
import radar.radar.Models.Chat;
import radar.radar.Models.Requests.NewChatRequest;
import radar.radar.Models.Responses.MessageResponse;
import radar.radar.Models.Responses.MessagesResponse;
import radar.radar.Models.Responses.SendMessageResponse;
import radar.radar.Models.Responses.MessageBody;
import radar.radar.Models.Responses.NewChatResponse;
import radar.radar.Models.User;
import radar.radar.Presenters.ChatPresenter;
import radar.radar.Services.AuthService;
import radar.radar.Services.ChatApi;
import radar.radar.Services.ChatService;
import radar.radar.Views.ChatView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity implements ChatView {
    private ChatService chatService;
    private User user;
    private int groupID;
    private ArrayList<MessageResponse> messages;
    private ArrayList<Integer> chatIDs;

    private EditText chatText;
    private Button send;

    // recyclerView
    private RecyclerView messageRecyclerView;
    private MessageListAdapter messageListAdapter;

    private Boolean load;

    private ChatPresenter chatPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Enable back action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Setup UI
        setupUI();
        messages = new ArrayList<>();

        // setup RecyclerView
        messageListAdapter = new MessageListAdapter(ChatActivity.this, messages);
        messageRecyclerView.setAdapter(messageListAdapter);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        // Create instance of retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://35.185.35.117/api/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create instance of chat service
        ChatApi chatApi = retrofit.create(ChatApi.class);
        chatService = new ChatService(this, chatApi);

        // Setup presenter
        chatPresenter = new ChatPresenter(this, chatService);

        // Get the user data that we will be chatting with from the previous intent
        user = (User) getIntent().getSerializableExtra("user");

        // Check whether we should load or generate new message
        load = getIntent().getExtras().getBoolean("load");

        // Process loading message or determine message creation
        chatPresenter.determineMessageCreation();
    }

    /** Method that are used for the back */
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    @Override
    public void setLoad(Boolean load) {
        this.load = load;
    }

    @Override
    public Boolean getLoad() {
        return load;
    }

    @Override
    public Chat getChatFromIntent() {
        return ((Chat) getIntent().getSerializableExtra("chat"));
    }

    @Override
    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    @Override
    public int getGroupID() {
        return groupID;
    }

    @Override
    public void setMessages(ArrayList<MessageResponse> messages) {
        this.messages = messages;
    }

    @Override
    public ArrayList<MessageResponse> getMessages() {
        return messages;
    }

    @Override
    public MessageListAdapter getMessageListAdapter() {
        return messageListAdapter;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    /** More appropriate to put into the view as it only contains minimum amount of logic whil
     * having so much dependency with the view, i.e. manipulating the onclicklistener
     */
    @Override
    public void embedSendMessage() {
        // embed onclick listener, remember the async process
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If send is clicked then send the message
                // Extract string from edit text
                MessageBody messageBody = new MessageBody(chatText.getText().toString());

                chatService.sendMessages(groupID, messageBody).subscribe(new Observer<SendMessageResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SendMessageResponse sendMessageResponse) {
                        if (sendMessageResponse.success) {
                            // When you click send you will add into message
                            messages.add(sendMessageResponse.sentMessage);

                            messageListAdapter.setMessageList(messages);

                            // Here link with recycler view
                            messageListAdapter.notifyDataSetChanged();

                            // Remove the text on the edit view
                            chatText.setText("");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e);
                        Toast.makeText(ChatActivity.this, "go to on error", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        });
    }

    @Override
    public Context getChatContext() {
        return this;
    }

    public void setupUI() {
        chatText = findViewById(R.id.chat_text_field);
        send = findViewById(R.id.button_send);
        messageRecyclerView = findViewById(R.id.messageRecyclerView);
    }

}
