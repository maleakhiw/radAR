package radar.radar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import radar.radar.ChatActivity;
import radar.radar.Models.Domain.Group;
import radar.radar.R;

/**
 * Adapter for chat list, used to connect data to display and recycler view
 */
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    ArrayList<Group> groups;
    Context context;

    /**
     * Constructor for ChatListAdapter
     * @param context context of the activity that calls the adapter
     * @param groups list of chats/ groups/ tracking groups
     */
    public ChatListAdapter(Context context, ArrayList<Group> groups) {
        this.context = context;
        this.groups = groups;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    /**
     * Set the array list groups with new arraylist/ updated array list
     * @param groups arraylist of Group
     */
    public void setChatList(ArrayList<Group> groups) {
        this.groups = groups;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate custom layout
        View titleView = inflater.inflate(R.layout.row_chat, parent, false);

        // return a new VH instance
        return new ViewHolder(titleView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Group group = groups.get(position);

        // load stuff
        holder.chatName.setText(group.name);

        // Check type of group
        if (group.isTrackingGroup) {
            holder.chatType.setText("Tracking Group");
        } else {
            holder.chatType.setText("Chat");
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    /**
     * Inner class for ViewHolder
     * Connecting row xml file with the application logic
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView chatName;
        TextView chatType;

        public ViewHolder(View itemView) {
            super(itemView);

            chatName = itemView.findViewById(R.id.row_chat_name);
            chatType = itemView.findViewById(R.id.row_type_value);

            // Setup on click listener on the view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // From the displayed friend list send information
                    Group group = groups.get(getAdapterPosition());
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("group", group);
                    intent.putExtra("load", true);
                    context.startActivity(intent);
                }
            });
        }
    }
}
