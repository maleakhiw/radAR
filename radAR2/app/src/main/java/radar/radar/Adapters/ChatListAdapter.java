package radar.radar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import radar.radar.ChatActivity;
import radar.radar.Models.Domain.Group;
import radar.radar.Presenters.ChatListPresenter;
import radar.radar.R;
import radar.radar.Services.AuthService;

/**
 * Adapter for chat list, used to connect data to display and recycler view
 */
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    ArrayList<Group> groups;
    Context context;
    ChatListPresenter presenter;

    /**
     * Constructor for ChatListAdapter
     * @param context context of the activity that calls the adapter
     * @param groups list of chats/ groups/ tracking groups
     * @param presenter presenter for the Activity
     */
    public ChatListAdapter(Context context, ArrayList<Group> groups, ChatListPresenter presenter) {
        this.context = context;
        this.groups = groups;
        this.presenter = presenter;
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

        if (group.lastMessage != null) {
            int sender = group.lastMessage.from;
            if (AuthService.getUserID(holder.context) == sender) {
                holder.lastMessageFrom.setText("Me: ");
            } else {
                holder.lastMessageFrom.setText(group.usersDetails.get(group.lastMessage.from).firstName + ": ");
            }
            holder.lastMessage.setText(group.lastMessage.text);
        }

//        // Check type of group
//        if (group.isTrackingGroup) {
//
//        } else {
//            holder.lastMessage.setText("Chat");
//        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    /**
     * Inner class for ViewHolder
     * Connecting row xml file with the application logic
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements MenuItem.OnMenuItemClickListener, View.OnCreateContextMenuListener {
        TextView chatName;
        TextView lastMessage;
        TextView lastMessageFrom;

        Context context;

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem delete = menu.add(Menu.NONE,1,1,"Delete chat");

            delete.setOnMenuItemClickListener(this);
        }

        public ViewHolder(View itemView) {
            super(itemView);

            context = itemView.getContext();

            chatName = itemView.findViewById(R.id.row_chat_name);
            lastMessageFrom = itemView.findViewById(R.id.row_last_message_from);
            lastMessage = itemView.findViewById(R.id.row_last_message);

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

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case 1:
                    System.out.println(groups.get(getAdapterPosition()));
                    Group group = groups.get(getAdapterPosition());
                    if (group != null) {
                        presenter.deleteGroup(group.groupID);
                    } else {
                        Log.w("onMenuItemClick", "Group missing");
                    }
//                    presenter.deleteGroup(groups.get(getAdapterPosition()).groupID);
                    return true;
            }
            return false;
        }
    }
}
