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

import radar.radar.Models.Domain.User;
import radar.radar.R;
import radar.radar.UserDetailActivity;

/**
 * Adapter for FriendsActivity that are used to display friend list of a user
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    ArrayList<User> friends;
    Context context;

    /**
     * Constructor for FriendsAdapter
     * @param context context of the activity which call this class
     * @param friends array list of users
     */
    public FriendsAdapter(Context context, ArrayList<User> friends) {
        this.context = context;
        this.friends = friends;
    }

    /**
     * Used to update the friend list of the adapter
     * @param friends new friend list for this adapter
     */
    public void updateFriends(ArrayList<User> friends) {
        this.friends = friends;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate custom layout
        View titleView = inflater.inflate(R.layout.row_friends, parent, false);

        // return a new VH instance
        return new ViewHolder(titleView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = friends.get(position);

        // load stuff
        holder.tvName.setText(user.firstName + " " + user.lastName);
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
        TextView tvOnlineStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfilePic = itemView.findViewById(R.id.row_friends_profile_picture);
            tvName = itemView.findViewById(R.id.row_friends_name);
            tvOnlineStatus = itemView.findViewById(R.id.row_friends_online_status);

            // Setup on click listener on the view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // From the displayed friend list send information
                    User user = friends.get(getAdapterPosition());

                    Intent intent = new Intent(itemView.getContext(), UserDetailActivity.class);
                    intent.putExtra("user", user);
                    context.startActivity(intent);

                }
            });
        }
    }
}
