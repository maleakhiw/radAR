package radar.radar.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Domain.MessageResponse;
import radar.radar.Models.Responses.MessageResponseWithDetails;
import radar.radar.Models.Domain.User;
import radar.radar.R;
import radar.radar.RetrofitFactory;
import radar.radar.Services.AuthService;
import radar.radar.Services.ResourcesApi;
import radar.radar.Services.ResourcesService;
import radar.radar.Services.TimeFormatService;
import retrofit2.Retrofit;

/**
 * Adapter for chat (messages)
 */
public class MessageListAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<MessageResponse> messageList;
    private HashMap<Integer, User> usersDetails;

    // Constant for message sent and received
    public static final int MESSAGE_SENT = 1;
    public static final int MESSAGE_RECEIVED = 2;

    /**
     * Constructor for the adapter
     * @param context ChatActivity
     * @param messageList messages to be displayed
     */
    public MessageListAdapter(Context context, ArrayList<MessageResponse> messageList) {
        this.messageList = messageList;
        this.context = context;
    }

    /**
     * Setting the message list to new message lsit
     * @param messageList new message list
     */
    public void setMessageList(ArrayList<MessageResponse> messageList) {
        this.messageList = messageList;
    }

    /**
     * Updated version of message list (include user details)
     * @param messageList new message list
     * @param userDetails
     */
    public void setMessageList(ArrayList<MessageResponse> messageList, HashMap<Integer, User> userDetails) {
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

    public ArrayList<MessageResponse> getMessageList() {
        return messageList;
    }

    public HashMap<Integer, User> getUsersDetails() {
        return usersDetails;
    }

    public void setUsersDetails(HashMap<Integer, User> usersDetails) {
        this.usersDetails = usersDetails;
    }

    /**
     * Inner class for ReceivedMessageHolder (layout for receive message row)
     */
    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profilePic;
        ResourcesService resourcesService;

        private boolean profilePicSet = false;


        public ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            nameText = itemView.findViewById(R.id.text_message_name);
            timeText = itemView.findViewById(R.id.text_message_time);
            profilePic = itemView.findViewById(R.id.image_message_profile);

            Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();
            resourcesService = new ResourcesService(context, retrofit.create(ResourcesApi.class));   // TODO move to factory, along with other instances of new UsersService

        }

        // Bind method
        void bind(MessageResponseWithDetails message) {
            messageText.setText(message.text);
            if (message.userDetails != null) {
                nameText.setText(message.userDetails.firstName + " " + message.userDetails.lastName);

                // load profile picture
                if (!profilePicSet) {
                    if (message.userDetails.profilePicture != null) {
                        resourcesService.getResourceWithCache(message.userDetails.profilePicture, context).subscribe(new Observer<File>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(File file) {
                                Picasso.with(context).load(file).into(profilePic);
                                profilePicSet = true;
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

            timeText.setText(TimeFormatService.parseTimeString(message.time, context));
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
            Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();
        }

        // Bind method
        void bind(MessageResponseWithDetails message) {
            messageText.setText(message.text);
            timeText.setText(TimeFormatService.parseTimeString(message.time, context));
        }
    }
}
