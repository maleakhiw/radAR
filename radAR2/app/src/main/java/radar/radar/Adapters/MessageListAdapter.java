package radar.radar.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import radar.radar.Models.Responses.MessageResponse;
import radar.radar.Models.Responses.MessageResponseWithDetails;
import radar.radar.Models.Domain.User;
import radar.radar.R;
import radar.radar.Services.AuthService;

/**
 * Adapter for chat (messages)
 */
public class MessageListAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<MessageResponse> messageList;
    private HashMap<Integer, User> usersDetails;

    // Constant for message sent and received
    public static final int MESSAGE_SENT = 1;
    public static final int MESSAGE_RECEIVED = 2;

    /**
     * Constructor for the adapter
     * @param context ChatActivity
     * @param messageList messages to be displayed
     */
    public MessageListAdapter(Context context, List<MessageResponse> messageList) {
        this.messageList = messageList;
        this.context = context;
    }

    /**
     * Setting the message list to new message lsit
     * @param messageList new message list
     */
    public void setMessageList(List<MessageResponse> messageList) {
        this.messageList = messageList;
    }

    /**
     * Updated version of message list (include user details)
     * @param messageList new message list
     * @param userDetails
     */
    public void setMessageList(List<MessageResponse> messageList, HashMap<Integer, User> userDetails) {
        this.messageList = messageList;
        this.usersDetails = userDetails;
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

    /**
     * Determining which use who sent
     * @param position
     * @return integer indicating which user sending message
     */
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
        User user;
        if (usersDetails != null) {
            user = usersDetails.get(message.from);
        } else {
            user = null;
        }

        switch (holder.getItemViewType()) {
            case MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(new MessageResponseWithDetails(message, user));
                break;
            case MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(new MessageResponseWithDetails(message, user));
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    /**
     * Parse time
     * @return string to be displayed in user interface
     */
    private String parseTimeString(String timeString) {
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = sdf.parse(timeString);
            System.out.println(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date.getTime());

            Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
            Integer minute = calendar.get(Calendar.MINUTE);

            String hourString, minuteString;
            if (hour < 10) {
                hourString = "0" + hour.toString();
            } else {
                hourString = hour.toString();
            }

            if (minute < 10) {
                minuteString = "0" + minute.toString();
            } else {
                minuteString = minute.toString();
            }

            // set am or pm
            int hourOfDay = hour;
            if (hourOfDay >= 12) {
                return hourString + ":" + minuteString + " pm";
            } else {
                return hourString + ":" + minuteString + " am";
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Inner class for ReceivedMessageHolder (layout for receive message row)
     */
    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;

        public ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            nameText = itemView.findViewById(R.id.text_message_name);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        // Bind method
        void bind(MessageResponseWithDetails message) {
            messageText.setText(message.text);
            if (message.userDetails != null) {
                nameText.setText(message.userDetails.firstName + " " + message.userDetails.lastName);
            }
            timeText.setText(parseTimeString(message.time));
        }
    }

    /**
     * Inner class for SentMessageHolder (layout for sent message row)
     */
    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        public SentMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body_send);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        // Bind method
        void bind(MessageResponseWithDetails message) {
            messageText.setText(message.text);
            timeText.setText(parseTimeString(message.time));
        }
    }
}
