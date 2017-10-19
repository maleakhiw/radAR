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
 * Adapter class for SearchUserFragment
 * Used to connect recycler view with data
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    ArrayList<User> users;
    Context context;

    /**
     * Constructor for SearchAdapter
     * @param context context of the activity calling this
     * @param users list of users to be displayed
     */
    public SearchAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate custom layout
        View titleView = inflater.inflate(R.layout.row_search, parent, false);

        // return a new VH instance
        return new ViewHolder(titleView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);

        // Bind with the view holder
        holder.tvName.setText(user.firstName + " " + user.lastName);
        holder.tvUsername.setText(" @" + user.username);
        holder.description.setText(user.profileDesc);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * ViewHolder class to connect UI in row with java
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfilePic;
        TextView tvName;
        TextView tvUsername;
        TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfilePic = itemView.findViewById(R.id.searchImageID);
            tvName = itemView.findViewById(R.id.row_input_name);
            tvUsername = itemView.findViewById(R.id.row_search_username);
            description = itemView.findViewById(R.id.row_description);

            // Setup on click listener on the view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // From the displayed friend list send information
                    User user = users.get(getAdapterPosition());

                    Intent intent = new Intent(context, UserDetailActivity.class);
                    intent.putExtra("user", user);
                    context.startActivity(intent);
                }
            });
        }
    }
}