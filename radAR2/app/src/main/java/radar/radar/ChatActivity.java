package radar.radar;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Adapters.MessageListAdapter;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Domain.MessageResponse;
import radar.radar.Models.Responses.MessagesResponse;
import radar.radar.Models.Responses.SendMessageResponse;
import radar.radar.Models.Responses.MessageBody;
import radar.radar.Models.Domain.User;
import radar.radar.Presenters.ChatPresenter;
import radar.radar.Services.AuthService;
import radar.radar.Services.ChatApi;
import radar.radar.Services.ChatService;
import radar.radar.Views.ChatView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Classes to display chat to user (ChatActivity)
 */
public class ChatActivity extends AppCompatActivity implements ChatView {
    /** UI and additional variables */
    private User user;
    private int groupID;
    private ArrayList<MessageResponse> messages;
    private ArrayList<Integer> chatIDs;
    private EditText chatText;
    private ImageButton send;
    private RecyclerView messageRecyclerView;
    private MessageListAdapter messageListAdapter;
    private Boolean load;

    /** Variable for presenter and services */
    private ChatPresenter chatPresenter;
    private ChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Enable back action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Getting data from previous activity
        Group group = (Group) getIntent().getSerializableExtra("group");
        if (group != null) {
            setTitle(group.name);
        }

        // Setup UI
        setupUI();
        messages = new ArrayList<>();

        // setup RecyclerView
        messageListAdapter = new MessageListAdapter(ChatActivity.this, messages);
        messageRecyclerView.setAdapter(messageListAdapter);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        // Create instance of retrofit
        Retrofit retrofit = RetrofitFactory.getRetrofit().build();

        // Create instance of chat service
        ChatApi chatApi = retrofit.create(ChatApi.class);
        chatService = new ChatService(this, chatApi);

        // Setup presenter
        chatPresenter = new ChatPresenter(this, chatService);

        // Get the user data that we will be chatting with from the previous intent
        user = (User) getIntent().getSerializableExtra("user");

        // Check whether we should load or generate new message
        load = getIntent().getExtras().getBoolean("load");

        // Call the method to display chat list
        if (savedInstanceState != null) {
            // save RV state
            Parcelable listState = savedInstanceState.getParcelable("LIST_STATE");
            ArrayList<MessageResponse> messages = (ArrayList<MessageResponse>) savedInstanceState.getSerializable("MESSAGES");
            HashMap<Integer, User> usersDetails = (HashMap<Integer, User>) savedInstanceState.getSerializable("USERS_DETAILS");

            if (listState != null) {
                messageRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
            }

            if (messages != null && usersDetails != null) {
                messageListAdapter.setMessageList(messages, usersDetails);
            }
            
//            Parcelable listState = savedInstanceState.getParcelable(KEY_RECYCLER_STATE);
//            ArrayList<Group> groups = (ArrayList<Group>) savedInstanceState.getSerializable("GROUPS_LIST");
//
//            if (listState != null) {
//                chatRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
//            }
//
//            if (groups != null) {
//                chatListAdapter.setGroups(groups);
//            } else {
//                chatListPresenter.getChats();
//            }

        } else {
//            chatListPresenter.getChats();
        }

        // Process loading message or determine message creation
        chatPresenter.determineMessageCreation();
    }

    /**
     * Method that are used for the back button
     */
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        chatPresenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        chatPresenter.onStop();
    }

    /**
     * Set the load status
     * @param load boolean value representing new chat object or loading old chat object
     */
    @Override
    public void setLoad(Boolean load) {
        this.load = load;
    }

    /**
     * Get the load status
     * @return boolean value representing load status
     */
    @Override
    public Boolean getLoad() {
        return load;
    }

    /**
     * Getting the group from previous activity
     * @return Group
     */
    @Override
    public Group getChatFromIntent() {
        return ((Group) getIntent().getSerializableExtra("group"));
    }

    /**
     * Setter for group id
     * @param groupID the id of the group
     */
    @Override
    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    /**
     * Getter for group id
     * @return group id
     */
    @Override
    public int getGroupID() {
        return groupID;
    }

    /**
     * Setter for array list messages
     * @param messages new array list messages
     */
    @Override
    public void setMessages(ArrayList<MessageResponse> messages) {
        this.messages = messages;
    }

    /**
     * Getter for array list messages
     * @return array list messages
     */
    @Override
    public ArrayList<MessageResponse> getMessages() {
        return messages;
    }

    /**
     * Get the adapter
     * @return adapter
     */
    @Override
    public MessageListAdapter getMessageListAdapter() {
        return messageListAdapter;
    }

    /**
     * Getter for user
     * @return user
     */
    @Override
    public User getUser() {
        return user;
    }

    /**
     * Getter for username
     * @return string username
     */
    @Override
    public String getUsername() {
        return user.username;
    }

    /**
     * Getter for user id
     * @return integer user id
     */
    @Override
    public int getUserID() {
        return user.userID;
    }

    /**
     * Setter for user
     * @param user new user
     */
    @Override
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Used to  display toast to user
     * @param message message to be sent in toast
     */
    @Override
    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Used to process displaying messages and connecting to recycler view
     */
    @Override
    public void processRecyclerView(MessagesResponse messagesResponse) {
        setMessages(messagesResponse.messages);
        getMessageListAdapter().setMessageList(getMessages(), messagesResponse.usersDetails);
        getMessageListAdapter().notifyDataSetChanged();
    }

    /**
     * Getting context of the activity
     * @return Context of this activity
     */
    @Override
    public Context getChatContext() {
        return this;
    }

    /**
     * Getting current user id
     * @return userid of the current user
     */
    @Override
    public int getCurrentUserID() {
        return AuthService.getUserID(getChatContext());
    }

    /**
     * Used to embed the send message
     * More appropriate to put into the view as it only contains minimum amount of logic while
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
                        Toast.makeText(ChatActivity.this, "go to on error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        System.out.println("onSaveInstanceState");

        // save RV state
        Parcelable listState = messageRecyclerView.getLayoutManager().onSaveInstanceState();
        ArrayList<MessageResponse> messages = messageListAdapter.getMessageList();
        HashMap<Integer, User> usersDetails = messageListAdapter.getUsersDetails();
        state.putParcelable("LIST_STATE", listState);
        state.putSerializable("MESSAGES", messages);
        state.putSerializable("USERS_DETAILS", usersDetails);
    }

    /**
     * Method to connect UI element with java
     */
    public void setupUI() {
        chatText = findViewById(R.id.chat_text_field);
        send = findViewById(R.id.button_send);
        messageRecyclerView = findViewById(R.id.messageRecyclerView);
    }

}
