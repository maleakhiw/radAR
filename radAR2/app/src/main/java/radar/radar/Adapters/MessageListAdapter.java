package radar.radar.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import radar.radar.Models.Responses.MessageResponse;
import radar.radar.R;
import radar.radar.Services.AuthService;

/**
 * Created by keyst on 1/10/2017.
 */

public class MessageListAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<MessageResponse> messageList;

    // Constant for message sent and received
    public static final int MESSAGE_SENT = 1;
    public static final int MESSAGE_RECEIVED = 2;

    public MessageListAdapter(Context context, List<MessageResponse> messageList) {
        this.messageList = messageList;
        this.context = context;
    }

    public void setMessageList(List<MessageResponse> messageList) {
        this.messageList = messageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_bubble_send, parent, false);
            return new SentMessageHolder(view);
        }
        else if (viewType == MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_bubble_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        MessageResponse message = (MessageResponse) messageList.get(position);

        if (AuthService.getUserID(context) == message.from) {
            // If the current user is the sender of the message
            return MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return MESSAGE_RECEIVED;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageResponse message = (MessageResponse) messageList.get(position);

        switch (holder.getItemViewType()) {
            case MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;

        public ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            nameText = itemView.findViewById(R.id.text_message_name);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        // Bind method
        void bind(MessageResponse message) {
            messageText.setText(message.text);
            //timeText.setText(message.time);
            //nameText.setText(message.)
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        public SentMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body_send);
        }

        // Bind method
        void bind(MessageResponse message) {
            messageText.setText(message.text);
            //timeText.setText(message.time);
            //nameText.setText(message.)
        }
    }
}
