package radar.radar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import radar.radar.Models.User;
import radar.radar.R;
import radar.radar.UserDetailActivity;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    ArrayList<User> friends;
    Context context;

    public FriendsAdapter(Context context, ArrayList<User> friends) {
        this.context = context;
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
        holder.tvUsername.setText(" @" + user.username);
        if (user.profileDesc == null) {
            holder.tvOnlineStatus.setText("am not horse");
        } else {
            holder.tvOnlineStatus.setText(user.profileDesc);
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfilePic;
        TextView tvName;
        TextView tvUsername;
        TextView tvOnlineStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfilePic = itemView.findViewById(R.id.row_friends_profile_picture);
            tvName = itemView.findViewById(R.id.row_friends_name);
            tvUsername = itemView.findViewById(R.id.row_friends_username);
            tvOnlineStatus = itemView.findViewById(R.id.row_friends_online_status);

            // Setup on click listener on the view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // From the displayed friend list send information
                    User user = friends.get(getAdapterPosition());

                    Intent intent = new Intent(context, UserDetailActivity.class);
                    intent.putExtra("user", user);
                    context.startActivity(intent);

                }
            });
        }
    }
}
