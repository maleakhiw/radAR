package radar.radar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import radar.radar.Services.ChatService;

public class ChatListActivity extends AppCompatActivity {
    private ChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
    }
}
