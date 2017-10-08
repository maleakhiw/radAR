package radar.radar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import radar.radar.Models.Domain.User;
import radar.radar.R;
import radar.radar.UserDetailActivity;

/**
 * Created by kenneth on 7/10/17.
 */

public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.ViewHolder> {
    HashMap<Integer, User> friends;
    ArrayList<User> friendsList;
    Context context;

    public GroupMembersAdapter(Context context, HashMap<Integer, User> friends) {
        this.context = context;
        this.friends = friends;

        friendsList = new ArrayList<>();
        for (Object entry: friends.entrySet()) {
            User user = (User) entry;
            friendsList.add(user);
        }
    }

    public void updateFriends(HashMap<Integer, User> friends) {
        this.friends = friends;

        friendsList = new ArrayList<>();
        for (Object entry: friends.values()) {
            User user = (User) entry;
            friendsList.add(user);
        }
    }

    @Override
    public GroupMembersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate custom layout
        View titleView = inflater.inflate(R.layout.row_friends, parent, false);

        // return a new VH instance
        return new GroupMembersAdapter.ViewHolder(titleView);
    }

    @Override
    public void onBindViewHolder(GroupMembersAdapter.ViewHolder holder, int position) {
        User user = friendsList.get(position);

        // load stuff
        holder.tvName.setText(user.firstName + " " + user.lastName);
//        holder.tvUsername.setText(" @" + user.username);
        if (user.profileDesc == null) {
            holder.tvOnlineStatus.setText("Hello, I am using Radar!");
        } else {
            holder.tvOnlineStatus.setText(user.profileDesc);
        }
    }

    @Override
    public int getItemCount() {
        if (friends == null) {
            return 0;
        }
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfilePic;
        TextView tvName;
//        TextView tvUsername;
        TextView tvOnlineStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfilePic = itemView.findViewById(R.id.row_friends_profile_picture);
            tvName = itemView.findViewById(R.id.row_friends_name);
//            tvUsername = itemView.findViewById(R.id.row_friends_username);
            tvOnlineStatus = itemView.findViewById(R.id.row_friends_online_status);

            // Setup on click listener on the view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // From the displayed friend list send information
                    User user = friendsList.get(getAdapterPosition());

                    System.out.println(user);

                    Intent intent = new Intent(context, UserDetailActivity.class);
                    intent.putExtra("user", user);
                    context.startActivity(intent);

                }
            });
        }
    }
}
