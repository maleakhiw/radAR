package radar.radar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Requests.NewChatRequest;
import radar.radar.Models.Responses.SendMessageResponse;
import radar.radar.Models.Responses.MessageBody;
import radar.radar.Models.Responses.NewChatResponse;
import radar.radar.Models.User;
import radar.radar.Services.AuthService;
import radar.radar.Services.ChatApi;
import radar.radar.Services.ChatService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {
    private ChatService chatService;
    private User user;
    private int groupID;

    private EditText chatText;
    private ImageView send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Setup UI
        setupUI();

        // Create instance of retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://35.185.35.117/api/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create instance of chat service
        ChatApi chatApi = retrofit.create(ChatApi.class);
        chatService = new ChatService(this, chatApi);

        // Get the user data that we will be chatting with from the previous intent
        user = (User) getIntent().getSerializableExtra("user");

        generateNewChat();
    }

    /** Used to generate a new chat/ new message for a particular user */
    public void generateNewChat() {
        // Create an object for new chat request which includes the participant of the chat
        // and also the name of the chat
        int id = user.userID;
        ArrayList<Integer> participant = new ArrayList<>();
        participant.add(id); // add user id (the person user id)
        participant.add(AuthService.getUserID(this));
        String name = user.username; // name of the chat is the username

        NewChatRequest newChatRequest = new NewChatRequest(participant, name);

        chatService.newChat(newChatRequest).subscribe(new Observer<NewChatResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(NewChatResponse newChatResponse) {
                // if the response is successful, then we can proceed to create a chat
                if (newChatResponse.success) {
                    groupID = newChatResponse.group.groupID;
                    Toast.makeText(getApplicationContext(), "newchat", Toast.LENGTH_LONG).show();
                    embedSendMessage(); // embed on click listener
                }
                else {
                    Toast.makeText(getApplicationContext(), "Status false", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getApplicationContext(), "Go to OnError", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void setupUI() {
        chatText = findViewById(R.id.chat_text_field_item_text_box);
        send = findViewById(R.id.chat_text_field_item_send_button);
    }

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
                            Toast.makeText(getApplicationContext(), "Send message successful", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        });
    }

}
