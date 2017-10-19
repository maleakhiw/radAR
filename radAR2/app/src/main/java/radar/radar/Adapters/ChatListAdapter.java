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
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.ChatActivity;
import radar.radar.Models.Domain.Group;
import radar.radar.Presenters.ChatListPresenter;
import radar.radar.R;
import radar.radar.RetrofitFactory;
import radar.radar.Services.AuthService;
import radar.radar.Services.ResourcesApi;
import radar.radar.Services.ResourcesService;
import radar.radar.Services.TimeFormatService;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import retrofit2.Retrofit;

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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate custom layout
        View titleView = inflater.inflate(R.layout.row_chat, parent, false);

        // return a new VH instance
        return new ViewHolder(titleView);
    }

    private void resetHolder(ViewHolder holder) {
        // we do not reset the profile picture here because it'll result in a "flash" - distracting
        holder.isTrackingGroup.setVisibility(View.GONE);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Group group = groups.get(position);

        // NOTE have to reset. Views might have been recycled.
        /*
         * When the page is reloaded, and the contents of the RecyclerView is invalidated, Android
         * might allocate what used to be the 2nd row to the 1st row, or so on.

         * If we don't clean the rows back up to a fresh state, we might end up with
         * wrong profile pictures and tracking group indicators between the rows.
         */
        resetHolder(holder);

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

            // set last message time
            holder.timestamp.setText(TimeFormatService.parseTimeString(group.lastMessage.time, context));
        } else {
            holder.lastMessage.setText("No messages yet.");
        }

        if (group.isTrackingGroup) {
            System.out.println("Show tracking group symbol for: " + group.name);
            holder.isTrackingGroup.setVisibility(View.VISIBLE);
        }

//        System.out.println(holder.profPicLoaded);
        if (group.profilePicture != null) {
            holder.resourcesService.getResourceWithCache(group.profilePicture, holder.context).subscribe(new Observer<File>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(File file) {
                    System.out.println("Update profile picture for: " + group.name);
                    Picasso.with(holder.context).load(file).into(holder.profilePic);
                    holder.profPicLoaded = true;
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
        } else {
            holder.profilePic.setImageResource(R.mipmap.ic_launcher_round);
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
    public class ViewHolder extends RecyclerView.ViewHolder implements MenuItem.OnMenuItemClickListener, View.OnCreateContextMenuListener {
        TextView chatName;
        TextView lastMessage;
        TextView lastMessageFrom;
        ImageView profilePic;
        ImageView isTrackingGroup;
        TextView timestamp;

        ResourcesService resourcesService;
        Context context;

        private boolean profPicLoaded = false;

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            menu.setHeaderTitle("Select Action");
            MenuItem delete = menu.add(Menu.NONE,1,1,"Delete chat");    // TODO move to strings
            delete.setOnMenuItemClickListener(this);
        }

        public ViewHolder(View itemView) {
            super(itemView);

            context = itemView.getContext();

            Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();
            resourcesService = new ResourcesService(context, retrofit.create(ResourcesApi.class));   // TODO move to factory, along with other instances of new UsersService

            chatName = itemView.findViewById(R.id.row_chat_name);
            lastMessageFrom = itemView.findViewById(R.id.row_last_message_from);
            lastMessage = itemView.findViewById(R.id.row_last_message);
            profilePic = itemView.findViewById(R.id.row_profile_picture);
            isTrackingGroup = itemView.findViewById(R.id.is_tracking_group);
            timestamp = itemView.findViewById(R.id.timestamp);
//            isChat = itemView.findViewById(R.id.is_chat);

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
