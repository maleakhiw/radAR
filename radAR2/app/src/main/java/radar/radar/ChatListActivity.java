package radar.radar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Adapters.ChatAdapter;
import radar.radar.Models.Group;
import radar.radar.Models.Responses.GetChatInfoResponse;
import radar.radar.Models.Responses.GetChatsResponse;
import radar.radar.Services.ChatApi;
import radar.radar.Services.ChatService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatListActivity extends AppCompatActivity {
    private ChatService chatService;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;

    private ArrayList<Integer> chatIDs;
    private ArrayList<Group> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

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

        // Call the method to display chat list
        getChatIDs();
    }

    public void getChatIDs() {
        // Getting the chat id that are related to a particular user
        chatService.getChats().subscribe(new Observer<GetChatsResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(GetChatsResponse getChatsResponse) {
                // If we successfully get chat ids
                if (getChatsResponse.success) {
                    chatIDs = getChatsResponse.groups;
                    Toast.makeText(ChatListActivity.this, "Successfully get chat ids", Toast.LENGTH_LONG).show();
                    displayChatList();
                }
                else {
                    Toast.makeText(ChatListActivity.this, "Unsuccessful getting chat ids", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(ChatListActivity.this, "Go to on error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {
            }
        });
    }

    /** Method that are used to display chat list */
    public void displayChatList() {
        // Using the id that we have get display the chat
        // Iterate through all ids
        for (int i=0; i < chatIDs.size(); i++) {
            chatService.getChatInfo(chatIDs.get(i)).subscribe(new Observer<GetChatInfoResponse>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(GetChatInfoResponse getChatInfoResponse) {
                    // If the response successful display on the recycler view
                    if (getChatInfoResponse.success) {
                        // Add to groups
                        groups.add(getChatInfoResponse.group);
                        chatAdapter.setChatList(groups);
                        chatAdapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(ChatListActivity.this, "Failed to display chat", Toast.LENGTH_LONG).show();
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
    }
}
